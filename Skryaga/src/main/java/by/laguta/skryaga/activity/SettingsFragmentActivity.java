package by.laguta.skryaga.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import by.laguta.skryaga.R;

/**
 * Author : Anatoly
 * Created : 14.02.2016 14:58
 *
 * @author Anatoly
 */
public class SettingsFragmentActivity extends Activity {

    private static final String TAG = SettingsFragmentActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }

    }
}
