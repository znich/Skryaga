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
        smsParser = HelperFactory.getServiceHelper().getSmsParser();
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
            saveTransaction(smsSender, smsBody, smsDate);
        }
        return START_STICKY;
    }

    private void saveTransaction(String smsSender, String smsBody, Long smsDate) {
        try {
            DateTime messageDate = new DateTime(new Date(smsDate));
            Transaction transaction = smsParser.parseToTransaction(smsBody, messageDate);
            BankAccount bankAccount = bankAccountDao.getByNumber(smsSender);
            transaction.setBankAccount(bankAccount);
            transaction.setMessageDate(messageDate);
            balanceDao.create(transaction.getBalance());
            transactionDao.create(transaction);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing message: \n" + smsBody);
        } catch (SQLException e) {
            Log.d(TAG, "Error saving transaction from message:\n" + smsBody);
            Log.e(TAG, "Error saving transaction " + smsSender, e);
        }
    }

    private void updateTransactionsPrivate() {
        try {
            Transaction lastTransaction = transactionDao.getLastTransaction();
            Long lastSmsDate = lastTransaction != null
                    ? lastTransaction.getMessageDate().getMillis()
                    : null;
            saveFromReceived(lastSmsDate);
        } catch (SQLException e) {
            Log.e(TAG, "Error getting transaction", e);
        }
    }

    void saveFromReceived(Long lastSmsDate) {
        String whereCondition = "address IN (" + getAddresses() + ")";
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
                updateComplete = false;
                while (cursor.moveToNext()) {
                    long messageId = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    String address = cursor.getString(2);
                    long contactId = cursor.getLong(3);
                    String contactId_string = String.valueOf(contactId);
                    long timestamp = cursor.getLong(4);
                    String body = cursor.getString(5);

                    saveTransaction(address, body, timestamp);
                }
            } finally {
                cursor.close();
                updateComplete = true;
            }
        }
    }

    private String getAddresses() {
        String[] accounts = getStringResource(R.string.bank_accounts).split(",");
        String inSelection = "";
        for (String account : accounts) {
            inSelection += "'" + account + "',";
        }
        inSelection = inSelection.replaceAll(",$", "");
        return inSelection;
    }

    private String getStringResource(int id) {
        return getResources().getString(id);
    }

    public static boolean isUpdateComplete() {
        return updateComplete;
    }
}
