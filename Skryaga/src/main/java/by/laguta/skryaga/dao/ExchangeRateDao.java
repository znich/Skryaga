package by.laguta.skryaga.dao;

import by.laguta.skryaga.dao.model.ExchangeRate;
import com.j256.ormlite.dao.Dao;
import org.joda.time.DateTime;

import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 19.12.2014 23:24
 *
 * @author Anatoly
 */
public interface ExchangeRateDao extends Dao<ExchangeRate, Long> {

    ExchangeRate getExchangeRate(DateTime date) throws SQLException;

    ExchangeRate getLastExchangeRate() throws SQLException;
}
