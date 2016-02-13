package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.ExchangeRateDao;
import by.laguta.skryaga.dao.model.ExchangeRate;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 19.12.2014 22:12
 *
 * @author Anatoly
 */
public class ExchangeRateDaoImpl extends OrmLiteBaseDAOImpl<ExchangeRate, Long>
        implements ExchangeRateDao {

    public ExchangeRateDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ExchangeRate.class);
    }

    @Override
    public ExchangeRate getExchangeRate(DateTime date) throws SQLException {
        QueryBuilder<ExchangeRate, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(ExchangeRate.RATE_DATE, date);
        queryBuilder.limit(1L);
        PreparedQuery<ExchangeRate> preparedQuery = queryBuilder.prepare();
        List<ExchangeRate> exchangeRates = query(preparedQuery);

        return exchangeRates.isEmpty() ? null : exchangeRates.get(0);
    }
}
