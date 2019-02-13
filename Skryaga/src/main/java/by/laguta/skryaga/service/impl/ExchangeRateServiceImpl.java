package by.laguta.skryaga.service.impl;

import android.content.Context;
import android.util.Log;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.ExchangeRateDao;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.service.ExchangeRateService;
import by.laguta.skryaga.service.ExchangeRateUpdateException;
import by.laguta.skryaga.service.UpdateExchangeRateListener;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.UpdateTask;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 19.12.2014 21:30
 *
 * @author Anatoly
 */
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private static final String TAG = ExchangeRateServiceImpl.class.getName();

    private Integer updateInterval;

    private DateTime lastUpdated;

    private ExchangeRateDao exchangeRateDao;
    private Context context;

    public ExchangeRateServiceImpl(Context context) {
        this.context = context;
        this.updateInterval = getResourceInteger(R.integer.exchange_rate_update_interval);

        initialiseServices();
    }

    void initialiseServices() {
        exchangeRateDao = HelperFactory.getDaoHelper().getExchangeRateDao();
    }

    @Override
    public ExchangeRate getSavedLowestSellExchangeRate() {
        ExchangeRate exchangeRate = null;
        try {
            exchangeRate = exchangeRateDao.getLastExchangeRate();
        } catch (SQLException e) {
            Log.e(TAG, "Error getting lowest selling rate", e);
        }

        return exchangeRate;
    }

    @Override
    public ExchangeRate getLowestExchangeRate(UpdateExchangeRateListener listener) {
        new UpdateRatesTask().execute(listener);
        return getSavedLowestSellExchangeRate();
    }

    private Set<ExchangeRate> getExchangeRates() throws ExchangeRateUpdateException {
        Set<ExchangeRate> exchangeRateSet = new HashSet<ExchangeRate>();
        try {
            Document doc = getDocument();
            Elements table = doc.select(getResourceString(R.string.ecopress_table_selector));
            Elements rows = table.select("tr");
            List<Element> currencyRows = rows.subList(3, rows.size());
            for (Element row : currencyRows) {
                ExchangeRate exchangeRate = parseExchangeRate(row, new DateTime());
                if (exchangeRate != null) {
                    exchangeRateSet.add(exchangeRate);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Could not load page ", e);
            throw new ExchangeRateUpdateException("Error loading exchange rates from server", e);
        }
        return exchangeRateSet;
    }

    Document getDocument() throws IOException {
        String url = getResourceString(R.string.ecopress_url);
        return Jsoup.connect(url).timeout(3000).get();
    }

    private ExchangeRate parseExchangeRate(Element row, DateTime date) {
        try {
            Element name = row.select(getResourceString(R.string.ecopress_name_selector)).first();
            if (name != null && name.children().isEmpty()) {
                String bankName = name.text();
                Element address = row.select(getResourceString(R.string.ecopress_address_selector))
                        .first();
                String bankAddress = "";
                if (address != null) {
                    bankAddress = address.text();
                }

                Element usdBuyElement = row.select(getResourceString(R.string.ecopress_buy_selector)).first();
                Double buyCourse = getCourse(usdBuyElement);
                Element usdSellElement = usdBuyElement.nextElementSibling();
                Double sellCourse = getCourse(usdSellElement);
                return new ExchangeRate(
                        null,
                        date,
                        Currency.CurrencyType.USD,
                        bankName,
                        bankAddress,
                        buyCourse,
                        sellCourse);
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getCourse(Element courseEleemnt) {
        Elements usdBuyChildNodes = courseEleemnt.children();
        if (usdBuyChildNodes.isEmpty()) {
            return Double.valueOf(courseEleemnt.text());
        } else {
            for (Element element : usdBuyChildNodes) {
                if (element.tagName().equals("strong")) {
                    return   Double.valueOf(element.text());
                }
            }
        }
        return null;
    }

    private synchronized void saveOrUpdateLowestExchangeRate(ExchangeRate exchangeRate) {
        try {
            ExchangeRate exist = exchangeRateDao.getExchangeRate(exchangeRate.getDate());
            if (exist != null) {
                exist.populateWith(exchangeRate);
                exchangeRateDao.update(exist);
            } else {
                exchangeRateDao.create(exchangeRate);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error with saving exchange rate" + exchangeRate.toString(), e);
        }
    }

    @Override
    public ExchangeRate getNearestExchangeRate(DateTime dateTime) {
        try {
            ExchangeRate exchangeRateBefore = exchangeRateDao.getNearestExchangeRate(
                    dateTime, false);

            ExchangeRate exchangeRateAfter = exchangeRateDao.getNearestExchangeRate(
                    dateTime, true);

            if (exchangeRateBefore == null) {
                return exchangeRateAfter;
            }
            if (exchangeRateAfter == null) {
                return exchangeRateBefore;
            }

            Duration durationBefore = new Duration(exchangeRateBefore.getDate(), dateTime);

            Duration durationAfter = new Duration(dateTime, exchangeRateAfter.getDate());

            if (durationBefore.isShorterThan(durationAfter)) {
                return exchangeRateBefore;
            } else {
                return exchangeRateAfter;
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error getting nearest exchange rate", e);
        }
        return null;
    }


    public void setExchangeRateDao(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    private String getResourceString(int code) {
        return context.getResources().getString(code);
    }

    private int getResourceInteger(int code) {
        return context.getResources().getInteger(code);
    }

    public void setUpdateInterval(Integer updateInterval) {
        this.updateInterval = updateInterval;
    }

    private class UpdateRatesTask extends UpdateTask<ExchangeRate> {
        @Override
        protected ExchangeRate performInBackground() {
            if (lastUpdated != null) {
                long minutes = new Duration(lastUpdated, new DateTime()).getStandardMinutes();
                if (minutes <= updateInterval) {
                    return getSavedLowestSellExchangeRate();
                }
            }
            try {
                ExchangeRate lowestRate = getLowestRate();
                saveOrUpdateLowestExchangeRate(lowestRate);
                lastUpdated = new DateTime();
                return lowestRate;
            } catch (ExchangeRateUpdateException e) {
                return null;
            }
        }

        private ExchangeRate getLowestRate() throws ExchangeRateUpdateException {
            Set<ExchangeRate> exchangeRates = getExchangeRates();
            return Collections.min(exchangeRates);
        }

    }

}
