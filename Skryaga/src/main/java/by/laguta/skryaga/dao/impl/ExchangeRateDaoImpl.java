package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.ExchangeRateDao;
import by.laguta.skryaga.dao.model.ExchangeRate;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.joda.time.DateTime;

import java.sql.SQLException;

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

        return queryBuilder.queryForFirst();
    }

    @Override
    public ExchangeRate getLastExchangeRate() throws SQLException {
        QueryBuilder<ExchangeRate, Long> queryBuilder = queryBuilder();
        queryBuilder.limit(1L).orderBy(ExchangeRate.RATE_DATE, false);

        return queryBuilder.queryForFirst();
    }

    @Override
    public ExchangeRate getNearestExchangeRate(DateTime date, boolean greater) throws SQLException {
        QueryBuilder<ExchangeRate, Long> queryBuilder = queryBuilder();
        if (greater) {
            queryBuilder.where().ge(ExchangeRate.RATE_DATE, date);
        } else {
            queryBuilder.where().le(ExchangeRate.RATE_DATE, date);
        }

        queryBuilder.limit(1L).orderBy(ExchangeRate.RATE_DATE, greater);

        return queryBuilder.queryForFirst();
    }
}
