package by.laguta.skryaga.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import by.laguta.skryaga.R;
import by.laguta.skryaga.activity.adapter.TransactionsAdapter;
import by.laguta.skryaga.activity.dialog.Progress;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.dao.model.UserSettings;
import by.laguta.skryaga.service.*;
import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.util.CurrencyUtil;
import by.laguta.skryaga.service.util.HelperFactory;
import by.laguta.skryaga.service.util.Settings;
import by.laguta.skryaga.service.util.UpdateTask;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialCommunityIcons;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int SETTINGS_REQUEST_CODE = 1;
    public static final int CALL_PHONE_REQUEST_CODE = 2;
    public static final int RECEIVE_SMS_REQUEST_CODE = 3;
    public static final int READ_SMS_REQUEST_CODE = 4;
    public static final int INTERNET_REQUEST_CODE = 5;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 6;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 7;
    public static final int READ_LOGS_REQUEST_CODE = 8;
    public static final int BIND_ACCESSIBILITY_SERVICE_REQUEST_CODE = 9;

    private CalculationService calculationService = HelperFactory.getServiceHelper()
            .getCalculationService();

    private ExchangeRateService exchangeRateService = HelperFactory.getServiceHelper()
            .getExchangeRateService();

    private StatisticsService statisticsService = HelperFactory.getServiceHelper()
            .getStatisticsService();

    private BalanceService balanceService = HelperFactory.getServiceHelper().getBalanceService();

    private TextView totalAmountField;
    private TextView spentToday;
    private TextView exchangeRate;
    private TextView restForToday;
    private TextView dailyAmount;
    private Toolbar toolbar;
    private TextView goalAmount;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView transactionsList;
    private Drawer drawer;

    private Queue<String> requestedPermissions;
    private static final Map<String, Integer> permissionOperationMap = new HashMap<String, Integer>() {{
        put(Manifest.permission.CALL_PHONE, CALL_PHONE_REQUEST_CODE);
        put(Manifest.permission.RECEIVE_SMS, RECEIVE_SMS_REQUEST_CODE);
        put(Manifest.permission.READ_SMS, READ_SMS_REQUEST_CODE);
        put(Manifest.permission.INTERNET, INTERNET_REQUEST_CODE);
        put(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        put(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        //put(Manifest.permission.READ_LOGS, READ_LOGS_REQUEST_CODE);
        //put(Manifest.permission.BIND_ACCESSIBILITY_SERVICE, BIND_ACCESSIBILITY_SERVICE_REQUEST_CODE);
    }};
    private Integer currentPermissionRequestedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        initToolbar();

        initNavigationDrawer();

        initTransactionsList();

        initLogsWriter();
    }

    private void bindViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        totalAmountField = (TextView) findViewById(R.id.total_amount_field);
        spentToday = (TextView) findViewById(R.id.spent_amount_field);
        restForToday = (TextView) findViewById(R.id.rest_amount_field);
        dailyAmount = (TextView) findViewById(R.id.daily_amount_field);
        goalAmount = (TextView) findViewById(R.id.goal_amount_field);
        exchangeRate = (TextView) findViewById(R.id.exchange_amount_field);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.transactionsRefresh);
        transactionsList = (RecyclerView) findViewById(R.id.transactions_view);
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu_main);
        Menu menu = toolbar.getMenu();
        menu.findItem(R.id.refresh).setIcon(
                new IconDrawable(this, MaterialCommunityIcons.mdi_reload)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, MaterialCommunityIcons.mdi_settings)
                        .colorRes(R.color.white)
                        .actionBarSize());
        toolbar.setTitle(getString(R.string.toolbarTitle));
        toolbar.setTitleTextAppearance(this, R.style.toolbarStyle);
    }

    private void initNavigationDrawer() {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return true;
                    }
                })
                .build();
    }

    private void initTransactionsList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        transactionsList.setLayoutManager(layoutManager);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                updateTransactionList();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateTransactionList() {
        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(
                calculationService.getAllTransactions(), this);
        transactionsList.setAdapter(transactionsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
        if (Settings.getInstance().isTransactionsProcessed()) {
            onStatisticsUpdate(null);
        }
    }

    private void checkPermissions() {
        requestedPermissions = new ArrayDeque<String>();
        requestedPermissions.addAll(Arrays.asList(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                //Manifest.permission.READ_LOGS,
                //Manifest.permission.BIND_ACCESSIBILITY_SERVICE
        ));
        requestPermission();
    }

    private void requestPermission() {
        String permission = requestedPermissions.poll();
        Integer requestCode = permissionOperationMap.get(permission);
        if (permission != null && requestCode != null) {
            if (!checkPermissionGranted(permission)) {
                currentPermissionRequestedCode = requestCode;
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                requestPermission();
            }
        } else {
            currentPermissionRequestedCode = null;
        }
    }

    private boolean checkPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == currentPermissionRequestedCode) {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFirstStart();
    }

    private void checkFirstStart() {
        UserSettings model = Settings.getInstance().getModel();

        if (model == null && currentPermissionRequestedCode == null) {
            PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
            showSettings(null);
        }
    }

    private void populateMainInfo() {
        MainInfoModel mainInfoModel = calculationService.getMainInfoModel();

        populateTotalAmount(mainInfoModel);

        spentToday.setText(CurrencyUtil.formatCurrencyByn(mainInfoModel.getTodaySpending(), true));

        restForToday.setText(CurrencyUtil.formatCurrencyByn(mainInfoModel.getRestForToday(), true));

        dailyAmount.setText(CurrencyUtil.formatCurrencyByn(mainInfoModel.getDailyAmount(), true));

        Goal goal = mainInfoModel.getGoal();
        double goalAmountValue = goal.getAmount().doubleValue();
        String goalText = CurrencyUtil.formatCurrency(goalAmountValue, goal.getCurrencyType());

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

    private void populateTotalAmount(MainInfoModel mainInfoModel) {
        Double totalAmount = mainInfoModel.getTotalAmount();
        CharSequence text;
        if (totalAmount != null) {
            text = CurrencyUtil.formatCurrencyByn(totalAmount, true);
        } else {
            text = getText(R.string.undefined_balance);
        }
        totalAmountField.setText(text);
    }

    private void populateExchangeRate(ExchangeRate lowestSellExchangeRate) {
        if (lowestSellExchangeRate != null) {
            Double sellingRate = lowestSellExchangeRate.getSellingRate();
            exchangeRate.setText(CurrencyUtil.formatCurrencyByn(sellingRate, true, 3));
        } else {
            exchangeRate.setText(getString(R.string.error_loading_rates));
        }
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
                    getString(R.string.transactionsProcessed), false);

            if (!transactionPrecessed) {
                return;
            }

            int salaryDate = sharedPreferences.getInt(getString(R.string.salaryDate), 9);
            int prepaidDate = sharedPreferences.getInt(getString(R.string.prepaidDate), 23);
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
        new MainInfoUpdateTask(this).execute(new UpdateListener<Void>() {
            @Override
            public void onUpdated(Void model) {
                populateMainInfo();
                updateTransactionList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void updateBalance(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_balance_title))
                .setMessage(getString(R.string.dialog_balance_message))
                .setIcon(android.R.drawable.ic_menu_compass)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        updateBalance();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void updateBalance() {
        //noinspection unchecked
        new BalanceUpdateTask(this, new UpdateListener<Void>() {
            @Override
            public void onUpdated(Void model) {
                populateMainInfo();
                updateTransactionList();
            }
        }).execute();
    }

    private class MainInfoUpdateTask extends UpdateTask<Void> {

        private Progress dialog;

        public MainInfoUpdateTask(Activity activity) {
            this.dialog = new Progress(
                    activity,
                    getString(R.string.progressTitle),
                    getString(R.string.statisticsUpdating),
                    null);
        }

        @Override
        protected Void performInBackground() {
            statisticsService.updateStatistics();
            return null;
        }

        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class BalanceUpdateTask extends UpdateTask<Void> {

        private UpdateListener<Void> updateListener;
        private Progress dialog;

        public BalanceUpdateTask(Activity activity, UpdateListener<Void> updateListener) {
            this.dialog = new Progress(
                    activity,
                    getString(R.string.progressTitle),
                    getString(R.string.balanceUpdating),
                    null);
            this.updateListener = updateListener;
        }

        @Override
        protected Void performInBackground() {
            balanceService.updateBalance(updateListener);
            return null;
        }

        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initLogsWriter() {
        if (isExternalStorageWritable()) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "//" + getString(R.string.application_directory));
            File logDirectory = new File( appDirectory + "//" + getString(R.string.log_directory));
            File logFile = new File( logDirectory, "logcat" + new DateTime() + ".txt");

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }
            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile + " *:I");
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
