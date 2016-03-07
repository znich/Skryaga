package by.laguta.skryaga.service.util;

import android.util.Log;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.ExchangeRate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Author : Anatoly
 * Created : 18.02.2016 0:23
 *
 * @author Anatoly
 */
public class CurrencyUtil {

    private static final String TAG = CurrencyUtil.class.getName();


    private static DecimalFormat formatter = createDecimalFormat();

    private static DecimalFormat createDecimalFormat() {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter;
    }

    public static BigDecimal convertUsdToByr(BigDecimal usd, ExchangeRate exchangeRate) {

        Double buyRate = exchangeRate.getBuyRate();

        return usd.multiply(new BigDecimal(buyRate));
    }

    public static BigDecimal convertByrToUsd(BigDecimal byr, ExchangeRate exchangeRate) {

        Double sellingRate = exchangeRate.getSellingRate();

        return convertByrToUsd(byr, sellingRate);
    }

    public static BigDecimal convertByrToUsd(BigDecimal byr, Double sellingRate) {
        if (sellingRate == 0) {
            return new BigDecimal(0d);
        }
        return byr.divide(new BigDecimal(sellingRate), 2, BigDecimal.ROUND_HALF_DOWN);
    }

    public static Double calculateRate(BigDecimal byr, BigDecimal usd) {
        if (usd.doubleValue() == 0) {
            return 0d;
        }
        return byr.divide(usd, 0, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public static String formatCurrency(double amount, Currency.CurrencyType currencyType) {
        return formatCurrency(amount, currencyType, true);
    }

    public static String formatCurrency(
            double amount, Currency.CurrencyType currencyType, boolean addCurrencySymbol) {
        switch (currencyType) {
            case USD:
                return formatCurrencyUsd(amount, addCurrencySymbol);
            case RUB:
                return formatCurrencyRub(amount, addCurrencySymbol);
            case EUR:
                return formatCurrencyEur(amount, addCurrencySymbol);
            default:
                return formatCurrencyByr(amount, addCurrencySymbol);
        }
    }

    public static String formatCurrencyByr(Double totalAmount, boolean addCurrencySymbol) {
        String currencyString = formatCurrency(totalAmount, 0);
        return addCurrencySymbol ? currencyString + "р" : currencyString;
    }

    public static String formatCurrencyUsd(Double totalAmount, boolean addCurrencySymbol) {
        String currencyString = formatCurrency(totalAmount, 2);
        return addCurrencySymbol ? "$" + currencyString : currencyString;
    }

    public static String formatCurrencyRub(Double totalAmount, boolean addCurrencySymbol) {
        String currencyString = formatCurrency(totalAmount, 2);
        return addCurrencySymbol ? currencyString + "rur" : currencyString;
    }

    public static String formatCurrencyEur(Double totalAmount, boolean addCurrencySymbol) {
        String currencyString = formatCurrency(totalAmount, 2);
        return addCurrencySymbol ? "€" + currencyString : currencyString;
    }

    private static String formatCurrency(Double totalAmount, int fractionDigits) {
        formatter.setMaximumFractionDigits(fractionDigits);
        return formatter.format(totalAmount);
    }

    public static Double parseCurrency(String currencyString) {
        currencyString = currencyString.replaceAll("\\s", "");
        Double result = null;
        try {
            Number parse = formatter.parse(currencyString);
            result = parse.doubleValue();
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing string: " + currencyString, e);
        }
        return result;
    }


}
