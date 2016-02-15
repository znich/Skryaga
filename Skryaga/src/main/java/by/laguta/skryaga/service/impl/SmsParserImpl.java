package by.laguta.skryaga.service.impl;

import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.SmsParser;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static by.laguta.skryaga.dao.model.Currency.CurrencyType;
import static by.laguta.skryaga.dao.model.Transaction.Type;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 15.12.2014 22:28
 *
 * @author Anatoly
 */
public class SmsParserImpl implements SmsParser {

    private static final String DOUBLE_REGEXP = "[-\\+]?\\d+(?:\\.\\d+)?";

    private static final String DATE_REGEXP =
            "(?:0[1-9]|[12][0-9]|3[01])[- /.](?:0[1-9]|1[012])[- /.](?:19|20)\\d\\d";

    private static final String TIME_REGEXP = "(?:[0-1]\\d|2[0-3])(?::[0-5]\\d){2}";

    private static final String OPERATION_REGEXP = "[^\\d\\-\\+]+";

    private static final String DESCRIPTION_REGEXP = "(?:(?!\\s+BLR\\s+|\\s+BYR\\s+).)*";

    private static final String SMS_REGEXP = "^Karta"
            + "\\s+(" + DOUBLE_REGEXP + ")"         // 1 - card
            + "\\s+(" + DATE_REGEXP + ")"       // 2 - date
            + "\\s+(" + TIME_REGEXP + ")"        // 3 - time
            + "\\s+(" + OPERATION_REGEXP + ")"   // 4 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 5 - amount
            + "\\s+(\\S{3})"                    // 6 - currency
            + "\\s+(" + DESCRIPTION_REGEXP + ")"  // 7 - description
            + "\\s+(?:BLR|BYR)"
            + "\\s+([^\\.]*)"                      // 8 - result
            + "\\.\\s+Dostupno"
            + "\\s+(" + DOUBLE_REGEXP + ")"       // 9 - balance
            + "";

    private static final String SMS_REGEXP_NO_DESCRIPTION = "^Karta"
            + "\\s+(" + DOUBLE_REGEXP + ")"         // 1 - card
            + "\\s+(" + DATE_REGEXP + ")"       // 2 - date
            + "\\s+(" + TIME_REGEXP + ")"        // 3 - time
            + "\\s+(" + OPERATION_REGEXP + ")"   // 4 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 5 - amount
            + "\\s+(\\S{3})"                    // 6 - currency
            + "\\s+([^\\.]*)"                      // 7 - result
            + "\\.\\s+Dostupno"
            + "\\s+(" + DOUBLE_REGEXP + ")"       // 8 - balance
            + "";

    private static final String SMS_REGEXP_PAYMENT = "^Karta"
            + "\\s+(" + DOUBLE_REGEXP + ")"         // 1 - card
            + "\\s+-"
            + "\\s+Oplata"
            + "\\s+(" + OPERATION_REGEXP + ")"   // 2 - description
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 3 - amount
            + "\\s+(\\S{3})"                    // 4 - currency
            + "";

    private static final String DATE_FORMAT = "dd.MM.y HH:mm:ss";

    private static final String TRANSACTION_RESULT_OK = "OK";

    private DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


    public Transaction parseToTransaction44(String message) throws ParseException {
        Matcher matcher = Pattern.compile(SMS_REGEXP).matcher(message);
        String match = "";
        while (matcher.find()) {
            match = matcher.group();
        }
        return null;
    }

    public Transaction parseToTransaction(String message) throws ParseException {
        Matcher matcher = Pattern.compile(SMS_REGEXP).matcher(message);
        if (matcher.find()) {
            return getCommonTransaction(matcher);
        }
        matcher = Pattern.compile(SMS_REGEXP_NO_DESCRIPTION).matcher(message);
        if (matcher.find()) {
            return getTransactionWithoutDescription(matcher);
        }
        matcher = Pattern.compile(SMS_REGEXP_PAYMENT).matcher(message);
        if (matcher.find()) {
            return getPaymentTransaction(matcher);
        }
        return null;
    }

    private Transaction getCommonTransaction(Matcher matcher) throws ParseException {
        DateTime dateTime = getDateTime(matcher);
        Balance balance = getBalance(matcher, dateTime, 9);
        Transaction transaction = new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 6),
                dateTime,
                getAmount(matcher, 5),
                getOperationType(matcher),
                matcher.group(7),
                false,
                getResult(matcher, 8));
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private Transaction getTransactionWithoutDescription(Matcher matcher) throws ParseException {
        DateTime dateTime = getDateTime(matcher);
        Balance balance = getBalance(matcher, dateTime, 8);
        Transaction transaction = new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 6),
                dateTime,
                getAmount(matcher, 5),
                getOperationType(matcher),
                "",
                false,
                getResult(matcher, 7));
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private Transaction getPaymentTransaction(Matcher matcher) {
        return new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 4),
                null,
                getAmount(matcher, 3),
                Type.SPENDING,
                matcher.group(2),
                false,
                true);
    }

    private String getCard(Matcher matcher) {
        return matcher.group(1);
    }

    private DateTime getDateTime(Matcher matcher) throws ParseException {
        String dateTimeString = matcher.group(2) + " " + matcher.group(3);
        Date date = dateFormat.parse(dateTimeString);
        return new DateTime(date);
    }

    private Type getOperationType(Matcher matcher) {
        String operation = matcher.group(4).trim();
        return Type.getByValue(operation);
    }

    private Double getAmount(Matcher matcher, int group) {
        return Double.valueOf(matcher.group(group));
    }

    private CurrencyType getCurrencyType(Matcher matcher, int group) {
        String currency = matcher.group(group);
        return CurrencyType.getByValue(currency);
    }

    private boolean getResult(Matcher matcher, int group) {
        String result = matcher.group(group);
        return result.equals(TRANSACTION_RESULT_OK);
    }

    private Balance getBalance(Matcher matcher, DateTime dateTime, int group) {
        Double balanceAmount = Double.valueOf(matcher.group(group));
        return new Balance(null, balanceAmount, dateTime);
    }


}