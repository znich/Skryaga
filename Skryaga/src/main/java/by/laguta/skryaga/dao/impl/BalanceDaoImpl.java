package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.BalanceDao;
import by.laguta.skryaga.dao.model.Balance;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Anatoly on 01.02.2016.
 */
public class BalanceDaoImpl extends OrmLiteBaseDAOImpl<Balance, Long> implements BalanceDao {

    public BalanceDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Balance.class);
    }

    @Override
    public Balance getCurrentBalance() throws SQLException {

        QueryBuilder<Balance, Long> queryBuilder = queryBuilder();
        queryBuilder.orderBy(Balance.DATE, false);
        queryBuilder.limit(1L);
        List<Balance> query = query(queryBuilder.prepare());
        return query.isEmpty() ? null : query.get(0);
    }
}
