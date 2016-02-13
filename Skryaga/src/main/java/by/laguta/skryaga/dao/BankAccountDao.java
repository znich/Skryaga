package by.laguta.skryaga.dao;

import by.laguta.skryaga.dao.model.BankAccount;

import java.sql.SQLException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 26.12.2014 16:46
 *
 * @author Anatoly
 */
public interface BankAccountDao {

    BankAccount getByNumber(String number) throws SQLException;
}
