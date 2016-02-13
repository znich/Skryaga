package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.UserSettingsDao;
import by.laguta.skryaga.dao.model.UserSettings;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Author : Anatoly
 * Created : 04.02.2016 22:18
 *
 * @author Anatoly
 */
public class UserSettingsDaoImpl extends OrmLiteBaseDAOImpl<UserSettings, Long> implements UserSettingsDao {


    public UserSettingsDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, UserSettings.class);
    }
}
