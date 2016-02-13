package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.model.Currency;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 20:47
 *
 * @author Anatoly
 */
public class CurrencyDAOImpl extends OrmLiteBaseDAOImpl<Currency, Long> {

    public CurrencyDAOImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Currency.class);
    }
}
