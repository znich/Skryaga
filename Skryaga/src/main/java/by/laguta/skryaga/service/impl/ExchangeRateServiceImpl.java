package by.laguta.skryaga.service.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.ExchangeRateDao;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.service.ExchangeRateService;
import by.laguta.skryaga.service.UpdateExchangeRateListener;
import by.laguta.skryaga.service.util.HelperFactory;
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
            exchangeRate = exchangeRateDao.getExchangeRate(new DateTime().withTimeAtStartOfDay());
        } catch (SQLException e) {
            Log.e(TAG, "Error getting lowest selling rate");
        }

        return exchangeRate;
    }

    @Override
    public ExchangeRate getLowestExchangeRate(UpdateExchangeRateListener listener) {
        new UpdateRatesTask().execute(listener);
        return getSavedLowestSellExchangeRate();
    }

    private Set<ExchangeRate> getExchangeRates() {
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
        }
        return exchangeRateSet;
    }

    Document getDocument() throws IOException {
        String url = getResourceString(R.string.ecopress_url);
        return Jsoup.connect(url).timeout(3000).get();
    }

    private ExchangeRate parseExchangeRate(Element element, DateTime date) {
        Element name = element.select(getResourceString(R.string.ecopress_name_selector)).first();
        if (name != null && name.children().isEmpty()) {
            String bankName = name.text();
            Element address = element.select(getResourceString(R.string.ecopress_address_selector))
                    .first();
            String bankAddress = "";
            if (address != null) {
                bankAddress = address.text();
            }

            Element usdBuy = element.select(getResourceString(R.string.ecopress_buy_selector))
                    .first();
            Double buyCourse = Double.valueOf(usdBuy.text());
            Double sellCourse = Double.valueOf(usdBuy.nextElementSibling().text());
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

    private class UpdateRatesTask extends AsyncTask<UpdateExchangeRateListener, Integer, ExchangeRate> {

        private UpdateExchangeRateListener[] listeners = new UpdateExchangeRateListener[0];

        protected ExchangeRate doInBackground(UpdateExchangeRateListener... listeners) {
            this.listeners = listeners;

            if (lastUpdated != null) {
                long minutes = new Duration(lastUpdated, new DateTime()).getStandardMinutes();
                if (minutes <= updateInterval) {
                    return getSavedLowestSellExchangeRate();
                }
            }
            ExchangeRate lowestRate = getLowestRate();
            saveOrUpdateLowestExchangeRate(lowestRate);
            lastUpdated = new DateTime();

            return lowestRate;
        }

        ExchangeRate getLowestRate() {
            Set<ExchangeRate> exchangeRates = getExchangeRates();

            return Collections.min(exchangeRates);
        }

        protected void onPostExecute(ExchangeRate result) {
            for (UpdateExchangeRateListener listener : listeners) {
                listener.onExchangeRateUpdated(result);
            }
        }
    }

}
