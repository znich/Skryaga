package by.laguta.skryaga.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import by.laguta.skryaga.R;
import by.laguta.skryaga.activity.dialog.Progress;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.dao.model.UserSettings;
import by.laguta.skryaga.service.*;
import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.Settings;
import by.laguta.skryaga.service.util.UpdateTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    private static final int SETTINGS_REQUEST_CODE = 1;

    private CalculationService calculationService = HelperFactory.getServiceHelper()
            .getCalculationService();

    private ExchangeRateService exchangeRateService = HelperFactory.getServiceHelper()
            .getExchangeRateService();

    private StatisticsService statisticsService = HelperFactory.getServiceHelper()
            .getStatisticsService();

    private Progress progressDialog;

    private TextView totalAmountField;
    private TextView spentToday;
    private TextView exchangeRate;
    private TextView restForToday;
    private TextView dailyAmount;
    private Toolbar toolbar;
    private TextView toolbarText;
    private TextView goalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(R.layout.activity_main);

        bindViews();

        initToolbar();
    }

    private void bindViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbarText = (TextView) findViewById(R.id.toolbar_title);

        totalAmountField = (TextView) findViewById(R.id.total_amount_field);
        spentToday = (TextView) findViewById(R.id.spent_amount_field);
        restForToday = (TextView) findViewById(R.id.rest_amount_field);
        dailyAmount = (TextView) findViewById(R.id.daily_amount_field);
        goalAmount = (TextView) findViewById(R.id.goal_amount_field);
        exchangeRate = (TextView) findViewById(R.id.exchange_amount_field);
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu_main);
        Typeface tf = Typeface.createFromAsset(getAssets(), getStringResource(R.string.fontArial));
        toolbarText.setTypeface(tf);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Settings.getInstance().isTransactionsProcessed()) {
            onStatisticsUpdate(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkFirstStart();
    }

    private void checkFirstStart() {
        UserSettings model = Settings.getInstance().getModel();

        if (model == null) {
            PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
            showSettings(null);
        }
    }

    private void populateMainInfo() {
        MainInfoModel mainInfoModel = calculationService.getMainInfoModel();

        totalAmountField.setText(formatCurrencyByr(mainInfoModel.getTotalAmount()));

        spentToday.setText(formatCurrencyByr(mainInfoModel.getTodaySpending()));

        restForToday.setText(formatCurrencyByr(mainInfoModel.getRestForToday()));

        dailyAmount.setText(formatCurrencyByr(mainInfoModel.getDailyAmount()));

        Goal goal = mainInfoModel.getGoal();
        double goalAmountValue = goal.getAmount().doubleValue();
        String goalText = goal.getCurrencyType().equals(Currency.CurrencyType.BYR)
                ? formatCurrencyByr(goalAmountValue) : formatCurrencyUsd(goalAmountValue);
        goalAmount.setText(goalText);

        ExchangeRate lowestSellExchangeRate = exchangeRateService.getLowestExchangeRate(
                new UpdateExchangeRateListener() {
                    @Override
                    public void onUpdated(ExchangeRate exchangeRate) {
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
        return formatCurrency(totalAmount) + "р";
    }

    private String formatCurrencyUsd(Double totalAmount) {
        return formatCurrency(totalAmount) + "$";
    }

    private String formatCurrency(Double totalAmount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(totalAmount);
    }

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), SettingsFragmentActivity.class);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    this);
            boolean transactionPrecessed = sharedPreferences.getBoolean(
                    getStringResource(R.string.transactionsProcessed), false);

            if (!transactionPrecessed) {
                return;
            }

            int salaryDate = sharedPreferences.getInt(getStringResource(R.string.salaryDate), 9);
            int prepaidDate = sharedPreferences.getInt(getStringResource(R.string.prepaidDate), 23);
            UserSettings userSettings = new UserSettings(null, salaryDate, prepaidDate, true);

            Settings settings = Settings.getInstance();
            UserSettings currentModel = settings.getModel();
            if (currentModel == null || !currentModel.equals(userSettings)) {
                settings.updateSettings(userSettings);
                populateMainInfo();
            }
        }
    }

    public void onStatisticsUpdate(MenuItem item) {
        //noinspection unchecked
        new MainInfoUpdateTask().execute(new UpdateListener<Void>() {
            @Override
            public void onUpdated(Void model) {
                populateMainInfo();
                Looper.loop();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private String getStringResource(int id) {
        return getApplicationContext().getResources().getString(id);
    }

    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new Progress(this, "Please wait", "", null);
        }
        progressDialog.show();
    }

    private class MainInfoUpdateTask extends UpdateTask<Void> {
        @Override
        protected Void performInBackground() {
            Looper.prepare();
            showProgress();
            statisticsService.updateStatistics();
            return null;
        }
    }
 }
