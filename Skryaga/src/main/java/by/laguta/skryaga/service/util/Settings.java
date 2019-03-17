package by.laguta.skryaga.service.util;

import android.util.Log;
import by.laguta.skryaga.dao.UserSettingsDao;
import by.laguta.skryaga.dao.model.UserSettings;

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
            if (model != null) {
                userSettings.setId(model.getId());
                if (userSettings.getCardNumber() == null && model.getCardNumber() != null) {
                    userSettings.setCardNumber(model.getCardNumber());
                }
            }
            userSettingsDao.createOrUpdate(userSettings);
            model = userSettings;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating user settings", e);
        }
    }

    public Integer getSalaryDate() {
        return model.getSalaryDate();
    }

    public Integer getPrepaidDate() {
        return model.getPrepaidDate();
    }

    public boolean isTransactionsProcessed() {
        return model != null && model.isTransactionsProcessed();
    }

    public UserSettings getModel() {
        return model;
    }
}
