package by.laguta.skryaga.dao;

import by.laguta.skryaga.dao.model.Balance;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface BalanceDao extends Dao<Balance, Long> {

    Balance getCurrentBalance() throws SQLException;

    Balance getPreviousBalance(Long balanceId) throws SQLException;
}
