package by.laguta.skryaga.dao.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import by.laguta.skryaga.dao.*;
import by.laguta.skryaga.dao.impl.*;
import by.laguta.skryaga.dao.model.*;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:15
 *
 * @author Anatoly
 */
public class DBConnector extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DBConnector.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME = "skryaga_dev.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 1;

    private TransactionDao transactionDao;
    private BankAccountDao bankAccountDao;
    private CurrencyDAOImpl currencyDAO;
    private ExchangeRateDao exchangeRateDao;
    private BalanceDao balanceDao;
    private SpendingStatisticsDao spendingStatisticsDao;
    private UserSettingsDao userSettingsDao;

    public DBConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, BankAccount.class);
            TableUtils.createTable(connectionSource, SpendingStatistics.class);
            TableUtils.createTable(connectionSource, Currency.class);
            TableUtils.createTable(connectionSource, Transaction.class);
            TableUtils.createTable(connectionSource, Balance.class);
            TableUtils.createTable(connectionSource, ExchangeRate.class);
            TableUtils.createTable(connectionSource, GoalTransaction.class);
            TableUtils.createTable(connectionSource, UserSettings.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase database, ConnectionSource connectionSource,
            int oldVersion, int newVersion) {
      /*  try {
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Goal.class, true);
            TableUtils.dropTable(connectionSource, Role.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "error upgrading db " + DATABASE_NAME + "from ver " + oldVersion);
            throw new RuntimeException(e);
        }*/
    }

    public TransactionDao getTransactionDao() {
        if (transactionDao == null) {
            try {
                transactionDao = new TransactionDaoImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating " + TransactionDaoImpl.class.getName(), e);
            }
        }
        return transactionDao;
    }

    public BankAccountDao getBankAccountDao() {
        if (bankAccountDao == null) {
            try {
                bankAccountDao = new BankAccountDaoImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating " + BankAccountDaoImpl.class.getName(), e);
            }
        }
        return bankAccountDao;
    }

    public CurrencyDAOImpl getCurrencyDAO() {
        if (currencyDAO == null) {
            try {
                currencyDAO = new CurrencyDAOImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating " + CurrencyDAOImpl.class.getName(), e);
            }
        }
        return currencyDAO;
    }

    public ExchangeRateDao getExchangeRateDao() {
        if (exchangeRateDao == null) {
            try {
                exchangeRateDao = new ExchangeRateDaoImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating " + ExchangeRateDaoImpl.class.getName(), e);
            }
        }
        return exchangeRateDao;
    }

    public BalanceDao getBalanceDao() {
        if (balanceDao == null) {
            try {
                balanceDao = new BalanceDaoImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating " + BalanceDaoImpl.class.getName(), e);
            }
        }
        return balanceDao;
    }

    public SpendingStatisticsDao getSpendingStatisticsDao() {
        if (spendingStatisticsDao == null) {
            try {
                spendingStatisticsDao = new SpendingStatisticsDaoImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating " + SpendingStatisticsDaoImpl.class.getName(), e);
            }
        }
        return spendingStatisticsDao;
    }

    public UserSettingsDao getUserSettingsDao() {
        if (userSettingsDao == null) {
            try {
                userSettingsDao = new UserSettingsDaoImpl(getConnectionSource());
            } catch (SQLException e) {
                Log.e(TAG, "Error creating UserSettingsDaoImpl", e);
            }
        }
        return userSettingsDao;
    }
}
