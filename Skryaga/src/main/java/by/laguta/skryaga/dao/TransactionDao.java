package by.laguta.skryaga.dao;

import by.laguta.skryaga.dao.model.Transaction;
import com.j256.ormlite.dao.Dao;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 26.12.2014 17:03
 *
 * @author Anatoly
 */
public interface TransactionDao extends Dao<Transaction, Long> {

    Transaction getLastTransaction() throws SQLException;

    BigDecimal getTodaySpending() throws  SQLException;

    List<Transaction> getSpendingTransactionsBetween(DateTime date, DateTime toDate) throws SQLException;

    List<Transaction> getFirstDaySpendingTransactions() throws SQLException;

    BigDecimal getIncomeAmount(DateTime date) throws SQLException;

    List<Transaction> getTransactionsList() throws  SQLException;

}
