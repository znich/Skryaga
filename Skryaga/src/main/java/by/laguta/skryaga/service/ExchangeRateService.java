package by.laguta.skryaga.service;

import by.laguta.skryaga.dao.model.ExchangeRate;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 19.12.2014 21:29
 *
 * @author Anatoly
 */
public interface ExchangeRateService {

    ExchangeRate getSavedLowestSellExchangeRate();

    ExchangeRate getLowestExchangeRate(UpdateExchangeRateListener listener);

}
