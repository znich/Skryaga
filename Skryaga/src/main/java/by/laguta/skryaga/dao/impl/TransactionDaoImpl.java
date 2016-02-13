package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.Transaction;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 20:39
 *
 * @author Anatoly
 */
public class TransactionDaoImpl extends OrmLiteBaseDAOImpl<Transaction, Long>
        implements TransactionDao {

    public TransactionDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Transaction.class);
    }

    @Override
    public Transaction getLastTransaction() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.orderBy(Transaction.MESSAGE_DATE, false).limit(1L);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> transactions = query(preparedQuery);
        return transactions.isEmpty() ? null : transactions.get(0);
    }

    @Override
    public Double getTodaySpending() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where().gt(Transaction.MESSAGE_DATE, new DateTime().withTimeAtStartOfDay())
                .and().eq(Transaction.TYPE, Transaction.Type.SPENDING);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> transactions = query(preparedQuery);

        return getSumAmount(transactions);
    }

    private double getSumAmount(List<Transaction> transactions) {
        BigDecimal result = new BigDecimal(0);
        for (Transaction transaction : transactions) {
            result = result.add(new BigDecimal(transaction.getAmount()));
        }
        return result.doubleValue();
    }

    @Override
    public List<Transaction> getSpendingTransactionsBetween(
            DateTime date, DateTime toDate) throws SQLException {

        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
                .ge(Transaction.MESSAGE_DATE, date)
                .and()
                .le(Transaction.MESSAGE_DATE, toDate)
                .and().eq(Transaction.TYPE, Transaction.Type.SPENDING);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    @Override
    public List<Transaction> getFirstDaySpendingTransactions() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(Transaction.TYPE, Transaction.Type.SPENDING);
        queryBuilder.orderBy(Transaction.MESSAGE_DATE, true);
        Transaction firstTransaction = queryBuilder.queryForFirst();
        if (firstTransaction != null) {
            QueryBuilder<Transaction, Long> firstDayTransactions = queryBuilder();
            firstDayTransactions.where().eq(Transaction.MESSAGE_DATE, firstTransaction.getDate())
                    .and()
                    .eq(Transaction.TYPE, Transaction.Type.SPENDING);
            return query(firstDayTransactions.prepare());
        }

        return null;
    }
}
