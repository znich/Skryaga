package by.laguta.skryaga.service.util;

import by.laguta.skryaga.dao.model.ExchangeRate;

import java.math.BigDecimal;

/**
 * Created by Anatoly on 31.01.2016.
 */
public class CurrencyConvertUtil {

    public static BigDecimal convertUsdToByr(BigDecimal usd, ExchangeRate exchangeRate) {

        Double sellingRate = exchangeRate.getSellingRate();

        return usd.multiply(new BigDecimal(sellingRate));
    }
}
