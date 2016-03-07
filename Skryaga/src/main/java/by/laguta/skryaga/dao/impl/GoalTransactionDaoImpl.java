package by.laguta.skryaga.dao.impl;

import by.laguta.skryaga.dao.GoalTransactionDao;
import by.laguta.skryaga.dao.model.GoalTransaction;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Author : Anatoly
 * Created : 07.03.2016 21:57
 *
 * @author Anatoly
 */
public class GoalTransactionDaoImpl extends OrmLiteBaseDAOImpl<GoalTransaction, Long>
        implements GoalTransactionDao {

    public GoalTransactionDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, GoalTransaction.class);
    }
}
