package by.laguta.skryaga.activity;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceFragment;
import android.util.Log;
import by.laguta.skryaga.R;
import by.laguta.skryaga.service.ISkryaga;
import by.laguta.skryaga.service.impl.SmsService;

/**
 * Author : Anatoly
 * Created : 14.02.2016 14:58
 *
 * @author Anatoly
 */
public class SettingsFragmentActivity extends Activity {

    private static final String TAG = SettingsFragmentActivity.class.getSimpleName();

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();

        context = getApplicationContext();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }


        private void updateSmsTransactions() {
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
    }
}
