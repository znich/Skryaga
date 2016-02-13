package by.laguta.skryaga.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import by.laguta.skryaga.R;
import by.laguta.skryaga.SettingsActivity;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.dao.model.UserSettings;
import by.laguta.skryaga.service.*;
import by.laguta.skryaga.service.impl.SmsService;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.Settings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    private CalculationService calculationService = HelperFactory.getServiceHelper()
            .getCalculationService();

    private ExchangeRateService exchangeRateService = HelperFactory.getServiceHelper()
            .getExchangeRateService();

    private StatisticsService statisticsService = HelperFactory.getServiceHelper()
            .getStatisticsService();

    private TextView totalAmountField;
    private TextView spentToday;
    private TextView exchangeRate;
    private TextView restForToday;
    private TextView dailyAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        updateSmsTransactions();
    }

    private void bindViews() {
        totalAmountField = (TextView) findViewById(R.id.total_amount_field);
        spentToday = (TextView) findViewById(R.id.spent_amount_field);
        restForToday = (TextView) findViewById(R.id.rest_amount_field);
        dailyAmount = (TextView) findViewById(R.id.daily_amount_field);
        exchangeRate = (TextView) findViewById(R.id.exchange_amount_field);
    }

    private void updateSmsTransactions() {
        Context context = getApplicationContext();
        Intent service = new Intent(context, SmsService.class);
        context.bindService(service, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ISkryaga iSkryaga = ISkryaga.Stub.asInterface(service);
                try {
                    iSkryaga.updateTransactions();
                } catch (RemoteException e) {
                    Log.e(TAG, "Error updating sms transactions");
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkFirstStart();

        if (SmsService.isUpdateComplete()) {
            populateMainInfo();
        }

    }

    private void checkFirstStart() {
        UserSettings model = Settings.getInstance().getModel();
        if (model == null) {
            Settings.getInstance().updateSettings(new UserSettings().createDefaultSettings());
        }
    }

    private void populateMainInfo() {
        MainInfoModel mainInfoModel = calculationService.getMainInfoModel();

        totalAmountField.setText(formatCurrencyByr(mainInfoModel.getTotalAmount()));

        spentToday.setText(formatCurrencyByr(mainInfoModel.getTodaySpending()));

        restForToday.setText(formatCurrencyByr(mainInfoModel.getRestForToday()));

        dailyAmount.setText(formatCurrencyByr(mainInfoModel.getDailyAmount()));

        ExchangeRate lowestSellExchangeRate = exchangeRateService.getLowestExchangeRate(
                new UpdateExchangeRateListener() {
                    @Override
                    public void onExchangeRateUpdated(ExchangeRate exchangeRate) {
                        populateExchangeRate(exchangeRate);

                    }
                });
        populateExchangeRate(lowestSellExchangeRate);
    }

    private void populateExchangeRate(ExchangeRate lowestSellExchangeRate) {
        if (lowestSellExchangeRate != null) {
            Double sellingRate = lowestSellExchangeRate.getSellingRate();
            exchangeRate.setText(formatCurrencyByr(sellingRate));
        }
    }

    private String formatCurrencyByr(Double totalAmount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(totalAmount) + "р";
    }

    public void onStatisticsUpdate(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        /*statisticsService.updateStatistics();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
