package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.BankAccountDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.BankAccount;
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

    private BankAccountDao bankAccountDao;

    public TransactionDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Transaction.class);
    }

    public void setBankAccountDao(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    @Override
    public Transaction getLastTransaction() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.orderBy(Transaction.MESSAGE_DATE, false).limit(1L);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        return queryForFirst(preparedQuery);
    }

    @Override
    public Transaction getLastTransaction(String account) throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        QueryBuilder<BankAccount, Long> bankAccountQueryBuilder = bankAccountDao.queryBuilder();
        bankAccountQueryBuilder.where().like(BankAccount.PHONE_NUMBER, formatLike(account));
        queryBuilder.join(bankAccountQueryBuilder);
        queryBuilder.orderBy(Transaction.MESSAGE_DATE, false).limit(1L);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        return queryForFirst(preparedQuery);
    }

    @Override
    public  List<Transaction> getTodaySpendingTransactions() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where().gt(Transaction.MESSAGE_DATE, new DateTime().withTimeAtStartOfDay())
                .and().eq(Transaction.TYPE, Transaction.Type.SPENDING)
                .and().isNull(Transaction.GOAL_TRANSACTION)
                .and().eq(Transaction.APPROVED_COLUMN, true);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    private BigDecimal getSumAmount(List<Transaction> transactions) {
        BigDecimal result = new BigDecimal(0);
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() != null) {
                result = result.add(new BigDecimal(transaction.getAmount()));
            }
        }
        return result;
    }

    @Override
    public List<Transaction> getSpendingTransactionsBetween(
            DateTime date, DateTime toDate) throws SQLException {

        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
                .isNull(Transaction.GOAL_TRANSACTION)
                .and()
                .ge(Transaction.DATE_COLUMN, date)
                .and()
                .lt(Transaction.DATE_COLUMN, toDate)
                .and()
                .eq(Transaction.TYPE, Transaction.Type.SPENDING)
                .and()
                .eq(Transaction.APPROVED_COLUMN, true);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    @Override
    public List<Transaction> getFirstDaySpendingTransactions() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
                .eq(Transaction.TYPE, Transaction.Type.SPENDING)
                .and()
                .isNull(Transaction.GOAL_TRANSACTION)
                .and()
                .eq(Transaction.APPROVED_COLUMN, true);
        queryBuilder.orderBy(Transaction.DATE_COLUMN, true);

        Transaction firstTransaction = queryBuilder.queryForFirst();
        if (firstTransaction != null) {
            DateTime start = firstTransaction.getDate().withTimeAtStartOfDay();
            return getSpendingTransactionsBetween(start, start.plusDays(1));
        }

        return null;
    }

    @Override
    public BigDecimal getIncomeAmount(DateTime date) throws SQLException {
        DateTime start = date.withTimeAtStartOfDay();
        DateTime end = start.plusDays(1);
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder.where().ge(Transaction.DATE_COLUMN, start)
                .and()
                .lt(Transaction.DATE_COLUMN, end)
                .and()
                .eq(Transaction.TYPE, Transaction.Type.INCOME)
                .and()
                .eq(Transaction.APPROVED_COLUMN, true);
        return getSumAmount(query(queryBuilder.prepare()));
    }

    @Override
    public List<Transaction> getTransactionsList() throws SQLException {
        QueryBuilder<Transaction, Long> queryBuilder = queryBuilder();
        queryBuilder();
        queryBuilder.orderBy(Transaction.DATE_COLUMN, false);
        return query(queryBuilder.prepare());
    }

}
