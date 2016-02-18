package by.laguta.skryaga.service.impl;

import android.util.Log;
import by.laguta.skryaga.dao.BalanceDao;
import by.laguta.skryaga.dao.SpendingStatisticsDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.SpendingStatistics;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.StatisticsService;
import by.laguta.skryaga.service.util.HelperFactory;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : Anatoly
 * Created : 06.02.2016 13:49
 *
 * @author Anatoly
 */
public class StatisticsServiceImpl implements StatisticsService {

    private static final String TAG = StatisticsServiceImpl.class.getSimpleName();

    private static final int statisticsDaysPeriod = 60;
    private TransactionDao transactionDao = HelperFactory.getDaoHelper().getTransactionDao();

    private SpendingStatisticsDao spendingStatisticsDao = HelperFactory.getDaoHelper()
            .getSpendingStatisticsDao();

    private BalanceDao balanceDao = HelperFactory.getDaoHelper().getBalanceDao();


    @Override
    public void updateStatistics() {
        try {
            SpendingStatistics lastStatistics = spendingStatisticsDao.getLastStatistics();

            DateTime lastStatisticsDate = getLastStatisticsDate(lastStatistics);

            if (lastStatisticsDate == null) {
                return;
            }

            DateTime today = new DateTime().withTimeAtStartOfDay();
            while (!lastStatisticsDate.isAfter(today)) {
                DateTime startDate = lastStatisticsDate.minusDays(statisticsDaysPeriod);

                List<Transaction> transactions = transactionDao.getSpendingTransactionsBetween(
                        startDate, lastStatisticsDate);

                double[] amountArray = calculateAmounts(transactions);

                double median = new Median().evaluate(amountArray);

                double average = roundAmount(new Mean().evaluate(amountArray));

                double relative = roundAmount(new Mean().evaluate(new double[]{average, median}));

                SpendingStatistics spendingStatistics = new SpendingStatistics(
                        null, lastStatisticsDate, median, average, relative);

                if (lastStatistics != null && lastStatistics.getDate().equals(lastStatisticsDate)) {
                    lastStatistics.populate(spendingStatistics);
                    spendingStatisticsDao.update(lastStatistics);
                } else {
                    spendingStatisticsDao.create(spendingStatistics);
                }

                lastStatisticsDate = lastStatisticsDate.plusDays(1);
            }


        } catch (SQLException e) {
            Log.e(TAG, "Error updating statistics", e);
        }
    }

    private double roundAmount(double amount) {
        return new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    private double[] calculateAmounts(List<Transaction> transactions) throws SQLException {
        HashMap<DateTime, BigDecimal> spendingMap = new HashMap<DateTime, BigDecimal>();
        for (Transaction transaction : transactions) {
            DateTime date = transaction.getDate().withTimeAtStartOfDay();
            BigDecimal amount = spendingMap.get(date);
            if (amount == null) {
                amount = new BigDecimal(0d);
            }
            amount = amount.add(getTransactionAmount(transaction));
            spendingMap.put(date, amount);
        }

        List<BigDecimal> values = new ArrayList<BigDecimal>(spendingMap.values());

        int size = values.size();
        double[] amountArray = new double[size];
        for (int i = 0; i < size; i++) {
            amountArray[i] = values.get(i).doubleValue();
        }
        return amountArray;
    }

    private DateTime getLastStatisticsDate(SpendingStatistics lastStatistics) throws SQLException {
        DateTime lastStatisticsDate = null;
        if (lastStatistics == null) {
            List<Transaction> firstDaySpendingTransactions = transactionDao
                    .getFirstDaySpendingTransactions();

            if (firstDaySpendingTransactions != null) {
                lastStatisticsDate = saveFirstStatistics(firstDaySpendingTransactions).plusDays(1);
            }
        } else {
            lastStatisticsDate = lastStatistics.getDate();
        }
        return lastStatisticsDate;
    }

    private DateTime saveFirstStatistics(List<Transaction> firstDaySpendingTransactions)
            throws SQLException {

        DateTime lastStatisticsDate = firstDaySpendingTransactions.iterator()
                .next().getDate().withTimeAtStartOfDay();

        BigDecimal totalAmount = new BigDecimal(0d);
        for (Transaction transaction : firstDaySpendingTransactions) {
            totalAmount = totalAmount.add(getTransactionAmount(transaction));
        }

        double totalDouble = totalAmount.doubleValue();
        SpendingStatistics spendingStatistics = new SpendingStatistics(
                null,
                lastStatisticsDate,
                totalDouble,
                totalDouble,
                totalDouble);

        spendingStatisticsDao.create(spendingStatistics);
        return lastStatisticsDate;
    }

    private BigDecimal getTransactionAmount(Transaction transaction) throws SQLException {
        return transaction.isByrCurrency()
                ? new BigDecimal(transaction.getAmount())
                : getAmountForForeignCurrency(transaction);
    }

    private BigDecimal getAmountForForeignCurrency(Transaction transaction) throws SQLException {
        Long balanceId = transaction.getBalance().getId();
        Balance currentBalance = balanceDao.queryForId(balanceId);
        Balance prevBalance = balanceDao.getPreviousBalance(balanceId);
        return new BigDecimal(prevBalance.getAmount())
                .add(new BigDecimal(currentBalance.getAmount()).negate());
    }

}
