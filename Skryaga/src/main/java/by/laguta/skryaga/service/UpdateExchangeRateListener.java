package by.laguta.skryaga.service;

import by.laguta.skryaga.dao.model.ExchangeRate;

/**
 * Created by Anatoly on 30.01.2016.
 */
public interface UpdateExchangeRateListener {

    void onExchangeRateUpdated(ExchangeRate exchangeRate);
}
