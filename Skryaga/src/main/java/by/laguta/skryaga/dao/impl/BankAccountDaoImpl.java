package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.BankAccountDao;
import by.laguta.skryaga.dao.model.BankAccount;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 20:45
 *
 * @author Anatoly
 */
public class BankAccountDaoImpl extends OrmLiteBaseDAOImpl<BankAccount, Long>
        implements BankAccountDao {


    public BankAccountDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, BankAccount.class);
    }

    @Override
    public BankAccount getByNumber(String number) throws SQLException {
        QueryBuilder<BankAccount, Long> queryBuilder = queryBuilder();
        queryBuilder.where().like(BankAccount.PHONE_NUMBER, formatLike(number));
        PreparedQuery<BankAccount> preparedQuery = queryBuilder.prepare();
        List<BankAccount> bankAccounts = query(preparedQuery);
        return bankAccounts.get(0);
    }
}
