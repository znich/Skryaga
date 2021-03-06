package by.laguta.skryaga.service.impl;

import android.util.Log;
import by.laguta.skryaga.dao.BalanceDao;
import by.laguta.skryaga.dao.SpendingStatisticsDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.*;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.service.CalculationService;
import by.laguta.skryaga.service.ExchangeRateService;
import by.laguta.skryaga.service.StatisticsService;
import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.model.TransactionDayListModel;
import by.laguta.skryaga.service.model.TransactionUIModel;
import by.laguta.skryaga.service.util.ConvertUtil;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.Settings;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 27.12.2014 15:41
 *
 * @author Anatoly
 */
public class CalculationServiceImpl implements CalculationService {
    private static final String TAG = CalculationServiceImpl.class.getName();

    private TransactionDao transactionDao;
    private BalanceDao balanceDao;
    private ExchangeRateService exchangeRateService;
    private SpendingStatisticsDao spendingStatisticsDao;
    private StatisticsService statisticsService;

    public CalculationServiceImpl() {
        initializeServices();
    }

    private void initializeServices() {
        transactionDao = HelperFactory.getDaoHelper().getTransactionDao();
        balanceDao = HelperFactory.getDaoHelper().getBalanceDao();
        spendingStatisticsDao = HelperFactory.getDaoHelper().getSpendingStatisticsDao();
        exchangeRateService = HelperFactory.getServiceHelper().getExchangeRateService();
        statisticsService = HelperFactory.getServiceHelper().getStatisticsService();
    }

    @Override
    public MainInfoModel getMainInfoModel() {
        Double totalAmount = getTotalAmount();
        Double dailyAmount = getDailyAmount();
        Double todaySpending = getTodaySpending();
        Double restForToday = new BigDecimal(dailyAmount)
                .add(new BigDecimal(todaySpending).negate()).doubleValue();
        Goal goal = calculateGoal();

        return new MainInfoModel(totalAmount, dailyAmount, todaySpending, restForToday, goal);
    }

    private Double getDailyAmount() {
        try {
            SpendingStatistics lastStatistics = spendingStatisticsDao.getLastStatistics();
            return lastStatistics == null ? 0d : lastStatistics.getRelative();
        } catch (SQLException e) {
            Log.e(TAG, "Error getting daily amount");
        }
        return 0d;
    }

    @Override
    public Double getTotalAmount() {
        Double amount = 0d;
        try {
            Balance currentBalance = balanceDao.getCurrentBalance();
            if (currentBalance != null) {
                amount = currentBalance.getAmount();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting total amount", e);
        }

        return amount;
    }

    @Override
    public Double getTodaySpending() {
        try {
            List<Transaction> todaySpendingTransactions = transactionDao.getTodaySpendingTransactions();
            return getSummarySpendingAmount(todaySpendingTransactions);

        } catch (SQLException e) {
            Log.e(TAG, "Error getting spent for today", e);
        }
        return 0d;
    }

    private Double getSummarySpendingAmount(List<Transaction> transactions) throws SQLException {
        BigDecimal spendingAmount = new BigDecimal(0);
        for (Transaction transaction : transactions) {
            if (transaction.isSpending()) {
                BigDecimal transactionAmount = statisticsService.getTransactionAmount(transaction);
                spendingAmount = spendingAmount.add(transactionAmount);
            }
        }
        return spendingAmount.doubleValue();
    }

    @Override
    public Goal calculateGoal() {
        Goal goal = new Goal(new BigDecimal(0d), Currency.CurrencyType.USD);
        try {
            SpendingStatistics spendingStatistics = spendingStatisticsDao.getLastStatistics();
            if (spendingStatistics == null) {
                return goal;
            }
            double relative = spendingStatistics.getAverage();

            boolean nextIncomePrepaid = isNextIncomePrepaid();

            long nextIncomeDays = nextIncomePrepaid ? getDaysBeforePrepaid() : getDaysBeforeSalary();

            BigDecimal reserve = new BigDecimal(relative).multiply(new BigDecimal(nextIncomeDays));

            if (nextIncomePrepaid) {
                reserve = reserve.add(getPrepaidShortage(spendingStatistics));
            }

            Double totalAmount = getTotalAmount();
            totalAmount = totalAmount != null ? totalAmount : 0d;
            BigDecimal byrGoal = new BigDecimal(totalAmount).add(reserve.negate());

            ExchangeRate exchangeRate = exchangeRateService.getSavedLowestSellExchangeRate();

            if (exchangeRate != null) {
                BigDecimal amount = byrGoal.divide(
                        new BigDecimal(exchangeRate.getSellingRate()),
                        0,
                        BigDecimal.ROUND_HALF_DOWN);
                goal = new Goal(amount, Currency.CurrencyType.USD);
            } else {
                goal = new Goal(byrGoal, Currency.CurrencyType.BYR);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error calculating goal", e);
        }

        return goal;
    }

    @Override
    public List<TransactionUIModel> getAllTransactions() {
        List<Transaction> transactions = getTransactions();
        return ConvertUtil.convertToUIModels(transactions);
    }

    private List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            transactions = transactionDao.getTransactionsList();
        } catch (SQLException e) {
            Log.e(TAG, "Error getting transactions", e);
        }
        return transactions;
    }

    @Override
    public List<TransactionDayListModel> getDayTransactionModels() {
        List<Transaction> transactions = getTransactions();
        Map<DateTime, List<Transaction>> gropedTransactions = groupTransactions(transactions);

        List<TransactionDayListModel> transactionDayListModels = new ArrayList<>();
        for (Map.Entry<DateTime, List<Transaction>> entry : gropedTransactions.entrySet()) {
            DateTime date = entry.getKey();
            List<Transaction> list = entry.getValue();
            List<TransactionUIModel> transactionUIModels = ConvertUtil.convertToUIModels(list);
            Double summarySpendingAmount = null;
            if (Settings.getInstance().getModel().isShowDailySpending()) {
                try {
                    summarySpendingAmount = getSummarySpendingAmount(list);
                } catch (SQLException e) {
                    Log.e(TAG, "Error getting spent for date: " + date, e);
                }
            }

            TransactionDayListModel transactionDayListModel = new TransactionDayListModel(date, summarySpendingAmount, Currency.CurrencyType.BYR, transactionUIModels);
            transactionDayListModels.add(transactionDayListModel);
        }

        return transactionDayListModels;
    }

    private Map<DateTime, List<Transaction>> groupTransactions(List<Transaction> transactions) {
        Map<DateTime, List<Transaction>> gropedTransactions = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            DateTime date = transaction.getDate().withTimeAtStartOfDay();
            List<Transaction> transactionList = gropedTransactions.get(date);
            if (transactionList == null) {
                transactionList = new ArrayList<>();
                gropedTransactions.put(date, transactionList);
            }
            transactionList.add(transaction);
        }
        return gropedTransactions;
    }

    private long getDaysBeforePrepaid() {
        DateTime today = new DateTime().withTimeAtStartOfDay();
        return new Duration(today, getNextPrepaidDate()).getStandardDays();
    }

    private Long getDaysBeforeSalary() {
        DateTime salaryDate = getNextSalaryDate();
        DateTime today = new DateTime().withTimeAtStartOfDay();

        return new Duration(today, salaryDate).getStandardDays();
    }

    private DateTime getNextSalaryDate() {
        Integer salaryDate = Settings.getInstance().getSalaryDate();
        return calculateIncomeDate(new DateTime().withTimeAtStartOfDay(), salaryDate);
    }

    private DateTime calculateIncomeDate(DateTime start, Integer incomeDayOfMonth) {
        DateTime startWithIncomeDate = start.withDayOfMonth(incomeDayOfMonth);
        DateTime result = checkHolidays(startWithIncomeDate);
        if (result.isAfter(start))  {
            return result;
        }
        return checkHolidays(result.plusMonths(1));
    }

    private DateTime checkHolidays(DateTime date) {
        int dayOfWeek = date.getDayOfWeek();
        int correctionDays;
        switch (dayOfWeek) {
            case 6:
                correctionDays = 1;
                break;
            case 7:
                correctionDays = 2;
                break;
            default:
                correctionDays = 0;
                break;
        }
        return date.minusDays(correctionDays);
    }

    private DateTime getNextPrepaidDate() {
        return getPrepaidDate(new DateTime().withTimeAtStartOfDay());
    }

    private DateTime getPrepaidDate(DateTime month) {
        Integer prepaidDate = Settings.getInstance().getPrepaidDate();
        return calculateIncomeDate(month, prepaidDate);
    }

    private BigDecimal getPrepaidShortage(SpendingStatistics spendingStatistics) {
        DateTime salaryDate = getNextSalaryDate();

        long prepaidToSalaryDays = new Duration(getNextPrepaidDate(), salaryDate).getStandardDays();

        BigDecimal prepaid = getPrepaidAmount();
        double relative = spendingStatistics.getRelative();

        BigDecimal shortage = new BigDecimal(relative)
                .multiply(new BigDecimal(prepaidToSalaryDays))
                .add(prepaid.negate());

        if (shortage.doubleValue() > 0) {
            return shortage;
        }

        return new BigDecimal(0d);
    }

    private boolean isNextIncomePrepaid() {
        //TODO: AL  check when today is salary or prepaid date
        DateTime today = new DateTime().withTimeAtStartOfDay();
        Interval interval = new Interval(today, getNextSalaryDate());
        return interval.contains(getNextPrepaidDate());
    }

    private BigDecimal getPrepaidAmount() {
        BigDecimal prepaidAmount = new BigDecimal(0);
        try {
            DateTime month = new DateTime().withDayOfMonth(1).withTimeAtStartOfDay().minusMonths(1);
            DateTime prepaidDate = getPrepaidDate(month);
            while (prepaidAmount.doubleValue() == 0d) {
                prepaidAmount = transactionDao.getIncomeAmount(prepaidDate);
                prepaidDate = prepaidDate.minusDays(1);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting prepaid amount", e);
        }
        return prepaidAmount;
    }

}
