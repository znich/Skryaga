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
public class MMBankSmsParser implements SmsParser {

    private static final String DOUBLE_REGEXP = "[-\\+]?\\d+(?:\\.\\d+|,\\d+)?";

    private static final String DATE_REGEXP =
            "(?:0[1-9]|[12][0-9]|3[01])[- /.](?:0[1-9]|1[012])[- /.](?:19|20)\\d\\d";

    private static final String TIME_REGEXP = "(?:[0-1]\\d|2[0-3])(?::[0-5]\\d){2}";

    private static final String OPERATION_REGEXP = "[^\\d\\-\\+]+";

    private static final String DESCRIPTION_REGEXP
            = "(?:(?!\\s+BLR\\s+|\\s+BYR\\s+|\\s+BYN\\s+).)*";

    private static final String SMS_REGEXP = "^Karta"
            + "\\s+(" + DOUBLE_REGEXP + ")"         // 1 - card
            + "\\s+(" + DATE_REGEXP + ")"       // 2 - date
            + "\\s+(" + TIME_REGEXP + ")"        // 3 - time
            + "\\s+(" + OPERATION_REGEXP + ")"   // 4 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 5 - amount
            + "\\s+(\\S{3})"                    // 6 - currency
            + "\\s+(" + DESCRIPTION_REGEXP + ")"  // 7 - description
            + "\\s+(?:BLR|BYR|BYN)"
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

    private static final String DATE_FORMAT = "dd.MM.y HH:mm:ss";

    private static final String TRANSACTION_RESULT_OK = "OK";

    private DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public MMBankSmsParser() {
    }

    public Transaction parseToTransaction44(String message) throws ParseException {
        Matcher matcher = Pattern.compile(SMS_REGEXP).matcher(message);
        String match = "";
        while (matcher.find()) {
            match = matcher.group();
        }
        return null;
    }

    @Override
    public Transaction parseToTransaction(String message, DateTime defaultDate)
            throws ParseException {

        Matcher matcher = Pattern.compile(SMS_REGEXP).matcher(message);
        if (matcher.find()) {
            return getCommonTransaction(matcher, defaultDate);
        }
        matcher = Pattern.compile(SMS_REGEXP_NO_DESCRIPTION).matcher(message);
        if (matcher.find()) {
            return getTransactionWithoutDescription(matcher, defaultDate);
        }
        throw new ParseException("Could not parse message : \n" + message, 0);
    }

    private Transaction getCommonTransaction(Matcher matcher, DateTime defaultDate)
            throws ParseException {
        DateTime dateTime = getDateTime(matcher, defaultDate);
        Balance balance = getBalance(matcher, dateTime, 9);
        Transaction transaction = new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 6),
                dateTime,
                getAmount(matcher, 5),
                getOperationType(matcher, 5),
                matcher.group(7),
                false,
                getResult(matcher, 8));
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private Transaction getTransactionWithoutDescription(Matcher matcher, DateTime defaultDate) throws ParseException {
        DateTime dateTime = getDateTime(matcher, defaultDate);
        Balance balance = getBalance(matcher, dateTime, 8);
        Transaction transaction = new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 6),
                dateTime,
                getAmount(matcher, 5),
                getOperationType(matcher, 5),
                "",
                false,
                getResult(matcher, 7));
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private String getCard(Matcher matcher) {
        return matcher.group(1);
    }

    private DateTime getDateTime(Matcher matcher, DateTime defaultDate) throws ParseException {
        String dateTimeString = matcher.group(2) + " " + matcher.group(3);
        Date date = dateFormat.parse(dateTimeString);
        return date != null ? new DateTime(date) : defaultDate;
    }

    private Double getAmount(Matcher matcher, int group) throws ParseException {
        Double parse = parseDouble(matcher, group);
        return Math.abs(parse);
    }

    private Double parseDouble(Matcher matcher, int group) throws ParseException {
        String doubleString = matcher.group(group).replace(",", ".");
        return Double.parseDouble(doubleString);
    }

    private Type getOperationType(Matcher matcher, int group) throws ParseException {
        return parseDouble(matcher, group) > 0 ? Type.INCOME : Type.SPENDING;
    }

    private CurrencyType getCurrencyType(Matcher matcher, int group) {
        String currency = matcher.group(group);
        return CurrencyType.getByValue(currency);
    }

    private boolean getResult(Matcher matcher, int group) {
        String result = matcher.group(group);
        return result.equals(TRANSACTION_RESULT_OK);
    }

    private Balance getBalance(Matcher matcher, DateTime dateTime, int group)
            throws ParseException {
        Double balanceAmount = parseDouble(matcher, group);
        return new Balance(null, balanceAmount, dateTime);
    }


}
