package by.laguta.skryaga.service.impl;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.BalanceDao;
import by.laguta.skryaga.dao.BankAccountDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.BankAccount;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.ISkryaga;
import by.laguta.skryaga.service.SmsParser;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.Settings;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 26.12.2014 16:25
 *
 * @author Anatoly
 */
public class SmsService extends Service {

    private static final String TAG = SmsService.class.getName();

    public static final String SMS_BODY = "sms_body";
    public static final String SMS_SENDER = "sms_sender";
    public static final String SMS_TIMESTAMP = "sms_timestamp";

    public static boolean updateComplete = true;

    private SmsParser smsParser;
    private BankAccountDao bankAccountDao;
    private TransactionDao transactionDao;
    private BalanceDao balanceDao;
    private static final Uri SMS_INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");

    @Override
    public IBinder onBind(Intent intent) {
        return new ISkryaga.Stub() {

            @Override
            public void updateTransactions() throws RemoteException {
                updateTransactionsPrivate();
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeServices();
    }

    void initializeServices() {
        HelperFactory.initialize(getApplicationContext());
        smsParser = HelperFactory.getServiceHelper().getSmsParser(null);
        bankAccountDao = HelperFactory.getDaoHelper().getBankAccountDao();
        transactionDao = HelperFactory.getDaoHelper().getTransactionDao();
        balanceDao = HelperFactory.getDaoHelper().getBalanceDao();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Settings.getInstance().isTransactionsProcessed() && intent != null) {
            String smsSender = intent.getExtras().getString(SMS_SENDER);
            String smsBody = intent.getExtras().getString(SMS_BODY);
            Long smsDate = intent.getExtras().getLong(SMS_TIMESTAMP);
            saveTransaction(smsSender, smsBody, smsDate, null);
        }
        return START_STICKY;
    }

    private void saveTransaction(
            String smsSender, String smsBody, Long smsDate, DateTime lastSmsDate) {

        try {
            DateTime messageDate = new DateTime(new Date(smsDate)).withMillisOfSecond(0);
            Transaction transaction = smsParser.parseToTransaction(smsBody, messageDate);
            if (transaction == null || transaction.getDate().equals(lastSmsDate)) {
                // do not process already saved transaction
                return;
            }
            if (!isAcceptedCard(transaction)) {
                return;
            }

            BankAccount bankAccount = bankAccountDao.getByNumber(smsSender);
            transaction.setBankAccount(bankAccount);
            transaction.setMessageDate(messageDate);
            balanceDao.create(transaction.getBalance());
            transactionDao.create(transaction);
            balanceDao.update(transaction.getBalance());
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing message: \n" + smsBody);
        } catch (SQLException e) {
            Log.d(TAG, "Error saving transaction from message:\n" + smsBody);
            Log.e(TAG, "Error saving transaction " + smsSender, e);
        }
    }

    private boolean isAcceptedCard(Transaction transaction) {
        String cardNumber = transaction.getCardNumber();
        String acceptedCardNumber = Settings.getInstance().getAcceptedCardNumber();
        return isBlank(acceptedCardNumber) || (!isBlank(cardNumber) && cardNumber.equals(acceptedCardNumber));
    }

    private void updateTransactionsPrivate() {
        try {
            updateComplete = false;
            String[] accounts = getResources().getStringArray(R.array.bank_accounts);
            for (String account : accounts) {
                Transaction lastTransaction = transactionDao.getLastTransaction(account);
                Long lastSmsDate = lastTransaction != null
                        ? lastTransaction.getMessageDate().getMillis() : null;
                DateTime smsDate = lastTransaction != null ? lastTransaction.getDate() : null;
                saveFromReceived(account, lastSmsDate, smsDate);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting transaction", e);
        } finally {
            updateComplete = true;
        }
    }

    private void saveFromReceived(String account, Long lastSmsDate, DateTime smsDate) {
        String whereCondition = "address == ('" + account + "')";
        if (lastSmsDate != null) {
            whereCondition += "AND date > " + lastSmsDate;
        }
        String sortOrder = "date ASC";

        Cursor cursor = getContentResolver().query(
                SMS_INBOX_CONTENT_URI,
                new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                whereCondition,
                null,
                sortOrder);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    long messageId = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    String address = cursor.getString(2);
                    long contactId = cursor.getLong(3);
                    String contactId_string = String.valueOf(contactId);
                    long timestamp = cursor.getLong(4);
                    String body = cursor.getString(5);

                    saveTransaction(address, body, timestamp, smsDate);
                }
            } finally {
                cursor.close();
            }
        }
    }

    private String getStringResource(int id) {
        return getResources().getString(id);
    }

    public static boolean isUpdateComplete() {
        return updateComplete;
    }
}
