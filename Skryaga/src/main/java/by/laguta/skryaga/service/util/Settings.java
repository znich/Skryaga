package by.laguta.skryaga.service.util;

import android.util.Log;
import by.laguta.skryaga.dao.UserSettingsDao;
import by.laguta.skryaga.dao.model.UserSettings;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.List;

/**
 * Author : Anatoly
 * Created : 04.02.2016 22:24
 *
 * @author Anatoly
 */
public class Settings {

    private static final String TAG = Settings.class.getSimpleName();

    private static UserSettingsDao userSettingsDao;

    private static UserSettings model;

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
            userSettingsDao = HelperFactory.getDaoHelper().getUserSettingsDao();
            initializeSettings();
        }

        return instance;
    }

    public static void initializeSettings() {
        try {
            List<UserSettings> userSettingsList = userSettingsDao.queryForAll();
            model = userSettingsList.isEmpty() ? null : userSettingsList.get(0);
        } catch (SQLException e) {
            Log.e(TAG, "Error get user settings", e);
        }
    }

    public void updateSettings(UserSettings userSettings) {
        try {
            userSettingsDao.createOrUpdate(userSettings);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating user settings", e);
        }
    }

    public DateTime getSalaryDate() {
        return model.getSalaryDate();
    }

    public DateTime getPrepaidDate() {
        return model.getPrepaidDate();
    }

    public UserSettings getModel() {
        return model;
    }
}
