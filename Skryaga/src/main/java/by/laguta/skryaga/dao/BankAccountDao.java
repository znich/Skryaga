package by.laguta.skryaga.dao;

import by.laguta.skryaga.dao.model.BankAccount;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 26.12.2014 16:46
 *
 * @author Anatoly
 */
public interface BankAccountDao extends Dao<BankAccount, Long> {

    BankAccount getByNumber(String number) throws SQLException;
}
