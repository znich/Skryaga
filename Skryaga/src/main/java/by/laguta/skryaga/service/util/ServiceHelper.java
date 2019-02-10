package by.laguta.skryaga.service.util;

import android.content.Context;
import by.laguta.skryaga.service.CalculationService;
import by.laguta.skryaga.service.ExchangeRateService;
import by.laguta.skryaga.service.SmsParser;
import by.laguta.skryaga.service.StatisticsService;
import by.laguta.skryaga.service.impl.*;

public class ServiceHelper {

    private CalculationService calculationService;
    private Connectivity connectivity;
    private ExchangeRateService exchangeRateService;
    private SmsParser smsParser;
    private StatisticsService statisticsService;

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

    public SmsParser getSmsParser() {
        if (smsParser == null) {
            smsParser = new MMBankSmsParser();
        }
        return smsParser;
    }

    public StatisticsService getStatisticsService() {
        if (statisticsService == null) {
            statisticsService = new StatisticsServiceImpl(context);
        }
        return statisticsService;
    }
}
