package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.SpendingStatisticsDao;
import by.laguta.skryaga.dao.model.SpendingStatistics;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.List;

/**
 * Author : Anatoly
 * Created : 01.02.2016 22:50
 *
 * @author Anatoly
 */
public class SpendingStatisticsDaoImpl extends OrmLiteBaseDAOImpl<SpendingStatistics, Long>
        implements SpendingStatisticsDao {


    public SpendingStatisticsDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, SpendingStatistics.class);
    }

    @Override
    public SpendingStatistics getLastStatistics() throws SQLException {
        QueryBuilder<SpendingStatistics, Long> queryBuilder = queryBuilder();
        queryBuilder.orderBy(SpendingStatistics.DATE, false);
        queryBuilder.limit(1L);

        List<SpendingStatistics> statisticsList = query(queryBuilder.prepare());

        return statisticsList.isEmpty() ? null : statisticsList.get(0);
    }

    @Override
    public void updateDenomination(DateTime denominationDate, int denominationValue)
            throws SQLException {
        QueryBuilder<SpendingStatistics, Long> queryBuilder = queryBuilder();
        queryBuilder.where().lt(SpendingStatistics.DATE, denominationDate);

        List<SpendingStatistics> statisticsList = query(queryBuilder.prepare());


        for (SpendingStatistics statistics : statisticsList) {
            statistics.setMedian(statistics.getMedian() / denominationValue);
            statistics.setAverage(statistics.getAverage() / denominationValue);
            statistics.setRelative(statistics.getRelative() / denominationValue);
        }

        for (SpendingStatistics statistics : statisticsList) {
            update(statistics);
        }
    }
}
