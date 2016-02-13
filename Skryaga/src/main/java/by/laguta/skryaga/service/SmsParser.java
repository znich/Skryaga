package by.laguta.skryaga.service;

import by.laguta.skryaga.dao.model.Transaction;

import java.text.ParseException;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 15.12.2014 22:27
 *
 * @author Anatoly
 */
public interface SmsParser {

    Transaction parseToTransaction(String message) throws ParseException;
}
