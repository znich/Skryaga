package by.laguta.skryaga.service.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import by.laguta.skryaga.dao.BalanceDao;
import by.laguta.skryaga.dao.BankAccountDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.BankAccount;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.BalanceService;
import by.laguta.skryaga.service.UpdateListener;
import by.laguta.skryaga.service.UssdParser;
import by.laguta.skryaga.service.util.HelperFactory;

import java.sql.SQLException;

public class BalanceServiceImpl implements BalanceService {

    private static final String TAG = BalanceServiceImpl.class.getName();
    public static final String BALANCE_UPDATE = "by.laguta.skryaga.service.impl.balance.update";

    private final Context context;

    private UssdParser ussdParser = HelperFactory.getServiceHelper().getUssdParser();
    private BalanceDao balanceDao = HelperFactory.getDaoHelper().getBalanceDao();
    private TransactionDao transactionDao = HelperFactory.getDaoHelper().getTransactionDao();
    private BankAccountDao bankAccountDao = HelperFactory.getDaoHelper().getBankAccountDao();
    private static boolean balanceRetrievingActive = false;

    private BroadcastReceiver mMessageReceiver;

    public BalanceServiceImpl(Context context) {
        this.context = context;
    }

    private void sendUssd(String cardNumber, String smsSender) {
        // TODO: get number from property
        String ussdCode = "*" + "212*" + cardNumber + Uri.encode("#");
        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));
        balanceRetrievingActive = true;
    }

    @Override
    public void updateBalance(UpdateListener<Void> updateListener) {
        try {
            Balance currentBalance = balanceDao.getCurrentBalance();
            if (currentBalance != null) {
                Transaction transaction = transactionDao.queryForId(currentBalance.getTransaction().getId());
                BankAccount bankAccount = bankAccountDao.queryForId(transaction.getBankAccount().getId());
                String defaultCard = bankAccount.getDefaultCard();

                if (defaultCard != null) {
                    addReceiver(updateListener);
                    sendUssd(defaultCard, bankAccount.getPhoneNumber());
                }


            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting balance", e);
        }
    }

    private void addReceiver(UpdateListener<Void> updateListener) {
        if (mMessageReceiver == null) {
            mMessageReceiver = new BalanceUssdBroadcastReceiver(updateListener);
            IntentFilter mFilter = new IntentFilter(BALANCE_UPDATE);
            context.registerReceiver(mMessageReceiver, mFilter);
        }
    }

    private String getStringResource(int id) {
        return context.getResources().getString(id);
    }

    private class BalanceUssdBroadcastReceiver extends BroadcastReceiver {
        private final UpdateListener<Void> updateListener;

        public BalanceUssdBroadcastReceiver(UpdateListener<Void> updateListener) {
            this.updateListener = updateListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                Balance currentBalance = balanceDao.getCurrentBalance();
                if (currentBalance != null) {
                    Double amount = ussdParser.parseBalanceAmount(message);
                    if (amount != null) {
                        currentBalance.setAmount(amount);
                        balanceDao.update(currentBalance);
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error retrieving current balance", e);
            }
            balanceRetrievingActive = false;
            updateListener.onUpdated(null);
        }
    }

    public static boolean isBalanceRetrievingActive() {
        return balanceRetrievingActive;
    }
}
