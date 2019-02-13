package by.laguta.skryaga.service.util;

import android.content.Context;
import by.laguta.skryaga.service.*;
import by.laguta.skryaga.service.impl.*;

public class ServiceHelper {

    private CalculationService calculationService;
    private Connectivity connectivity;
    private ExchangeRateService exchangeRateService;
    private SmsParser smsParser;
    private StatisticsService statisticsService;
    private BalanceService balanceService;
    private UssdParser ussdParser;

    private Context context;

    public ServiceHelper(Context context) {
        this.context = context;
    }

    public CalculationService getCalculationService() {
        if (calculationService == null) {
            calculationService = new CalculationServiceImpl();
        }
        return calculationService;
    }

    public Connectivity getConnectivity() {
        if (connectivity == null) {
            connectivity = new Connectivity(context);
        }
        return connectivity;
    }

    public ExchangeRateService getExchangeRateService() {
        if (exchangeRateService == null) {
            exchangeRateService = new ExchangeRateServiceImpl(context);
        }
        return exchangeRateService;
    }

    public SmsParser getSmsParser(String account) {
        //TODO: choose parser

        if (smsParser == null) {
            smsParser = new PriorSmsParser(context);
        }
        return smsParser;
    }

    public StatisticsService getStatisticsService() {
        if (statisticsService == null) {
            statisticsService = new StatisticsServiceImpl(context);
        }
        return statisticsService;
    }

    public BalanceService getBalanceService() {
        if (balanceService == null) {
            balanceService = new BalanceServiceImpl(context);
        }
        return balanceService;
    }

    public UssdParser getUssdParser() {
        if (ussdParser == null) {
            ussdParser = new UssdParserImpl();
        }
        return ussdParser;
    }
}
