package by.laguta.skryaga.service.impl;

import android.util.Log;
import by.laguta.skryaga.dao.BalanceDao;
import by.laguta.skryaga.dao.SpendingStatisticsDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.dao.model.SpendingStatistics;
import by.laguta.skryaga.service.CalculationService;
import by.laguta.skryaga.service.ExchangeRateService;
import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.Settings;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 27.12.2014 15:41
 *
 * @author Anatoly
 */
public class CalculationServiceImpl implements CalculationService {
    private static final String TAG = CalculationServiceImpl.class.getName();
    private static final int AVG_MOTH_COUNT = 3;

    private TransactionDao transactionDao;
    private BalanceDao balanceDao;
    private ExchangeRateService exchangeRateService;
    private SpendingStatisticsDao spendingStatisticsDao;

    public CalculationServiceImpl() {
        initializeServices();

    }

    private void initializeServices() {
        transactionDao = HelperFactory.getDaoHelper().getTransactionDao();
        balanceDao = HelperFactory.getDaoHelper().getBalanceDao();
        spendingStatisticsDao = HelperFactory.getDaoHelper().getSpendingStatisticsDao();
        exchangeRateService = HelperFactory.getServiceHelper().getExchangeRateService();
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
        Double amount = 0d;
        try {
            amount = transactionDao.getTodaySpending();
        } catch (SQLException e) {
            Log.e(TAG, "Error getting spent for today", e);
        }
        return amount;
    }

    @Override
    public Goal calculateGoal() {
        Goal goal = new Goal(new BigDecimal(0d), Currency.CurrencyType.USD);
        try {
            SpendingStatistics spendingStatistics = spendingStatisticsDao.getLastStatistics();
            if (spendingStatistics == null) {
                return goal;
            }
            double relative = spendingStatistics.getRelative();

            boolean nextIncomePrepaid = isNextIncomePrepaid();

            long nextIncomeDays = nextIncomePrepaid ? getDaysBeforePrepaid() : getDaysBeforeSalary();

            BigDecimal reserve = new BigDecimal(relative).multiply(new BigDecimal(nextIncomeDays));

            if (nextIncomePrepaid) {
                reserve = reserve.add(getPrepaidShortage(spendingStatistics));
            }

            BigDecimal byrGoal = new BigDecimal(getTotalAmount()).add(reserve.negate());

            ExchangeRate exchangeRate = exchangeRateService.getSavedLowestSellExchangeRate();

            if (exchangeRate != null) {
                BigDecimal amount = byrGoal.divide(
                        new BigDecimal(exchangeRate.getSellingRate()),
                        2,
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

    private long getDaysBeforePrepaid() {
        DateTime today = new DateTime().withTimeAtStartOfDay();
        return new Duration(today, getPrepaidDate()).getStandardDays();
    }

    private Long getDaysBeforeSalary() {
        DateTime salaryDate = getNextSalaryDate();
        DateTime today = new DateTime().withTimeAtStartOfDay();

        return new Duration(today, salaryDate).getStandardDays();
    }

    private DateTime getNextSalaryDate() {
        //TODO: AL calculate date according on salary date from settings and holidays
        Integer salaryDate = Settings.getInstance().getSalaryDate();
        return new DateTime().withDayOfMonth(salaryDate);
    }

    private BigDecimal getPrepaidShortage(SpendingStatistics spendingStatistics) {
        DateTime salaryDate = getNextSalaryDate();

        long prepaidToSalaryDays = new Duration(getPrepaidDate(), salaryDate).getStandardDays();

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
        return interval.contains(getPrepaidDate());
    }

    private BigDecimal getPrepaidAmount() {
        //TODO: AL get last prepaid amount
        return new BigDecimal(0d);
    }

    private DateTime getPrepaidDate() {
        //TODO: AL  get prepaid date from settings
        Integer prepaidDate = Settings.getInstance().getPrepaidDate();
        return new DateTime().withDayOfMonth(prepaidDate);
    }
}
