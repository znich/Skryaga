package by.laguta.skryaga.service;

import by.laguta.skryaga.dao.model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Author : Anatoly
 * Created : 06.02.2016 13:49
 *
 * @author Anatoly
 */
public interface StatisticsService {

    void updateStatistics();

    BigDecimal getTransactionAmount(Transaction transaction) throws SQLException;
}
