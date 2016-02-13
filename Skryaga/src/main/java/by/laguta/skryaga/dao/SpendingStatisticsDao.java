package by.laguta.skryaga.dao;

import by.laguta.skryaga.dao.model.SpendingStatistics;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Author : Anatoly
 * Created : 01.02.2016 22:49
 *
 * @author Anatoly
 */
public interface SpendingStatisticsDao extends Dao<SpendingStatistics, Long> {

    SpendingStatistics getLastStatistics() throws SQLException;
}
