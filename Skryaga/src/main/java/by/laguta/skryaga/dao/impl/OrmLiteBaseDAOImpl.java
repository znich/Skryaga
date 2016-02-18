package by.laguta.skryaga.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:52
 *
 * @author Anatoly
 */
public abstract class OrmLiteBaseDAOImpl<T, ID> extends BaseDaoImpl<T, ID> {

    public static final String ID = "id";

    public OrmLiteBaseDAOImpl(ConnectionSource connectionSource, Class<T> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    protected String formatLike(String value) {
        return "%" + value + "%";
    }

}
