package by.laguta.skryaga.service.impl;

import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.SmsParser;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by Anatoly on 09.02.2019.
 */
public class PriorSmsParser implements SmsParser {

    private static final String CARD_REGEXP = "\\d+\\*+\\d+";

    private static final String DOUBLE_REGEXP = "[-\\+]?\\d+(?:\\.\\d+|,\\d+)?";

    private static final String DATE_REGEXP =
            "(?:0[1-9]|[12][0-9]|3[01])[- /.//](?:0[1-9]|1[012])(?:[- /.](?:19|20)\\d\\d)?";

    private static final String TIME_REGEXP = "(?:[0-1]\\d|2[0-3])(?::[0-5]\\d)*";

    private static final String OPERATION_REGEXP = "[^\\d\\-\\+]+";

    private static final String DESCRIPTION_REGEXP = "(?:(?!\\s+Dost).)*";

    private static final String SMS_SPENDING_REGEXP = "^Priorbank\\. Karta"
            + "\\s+(" + CARD_REGEXP + ")"         // 1 - card
            + "\\s+(" + DATE_REGEXP + ")"       // 2 - date
            + "\\s+(" + TIME_REGEXP + ")\\."        // 3 - time
            + "\\s+(" + OPERATION_REGEXP + ")"   // 4 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 5 - amount
            + "\\s+(\\S{3})\\."                    // 6 - currency
            + "\\s+(" + DESCRIPTION_REGEXP + ")"  // 7 - description
            + "\\s+[^:]+:"
            + "(" + DOUBLE_REGEXP + ")"       // 8 - balance
            + "";

    private static final String SMS_INCOME_REGEXP = "^Priorbank"
            + "\\s+(" + DATE_REGEXP + ")"       // 1 - date
            + "\\s+(" + TIME_REGEXP + ")\\."        // 2 - time
            + "\\s+(" + OPERATION_REGEXP + ")"   // 3 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 4 - amount
            + "\\s+(\\S{3})\\."                    // 5 - currency
            + "\\s+[^:]+:"
            + "\\s+(" + DOUBLE_REGEXP + ")"       // 6 - balance
            + "";

    private static final String DATE_FORMAT_YEAR = "dd-MM-y HH:mm:ss";
    private static final String DATE_FORMAT_SHORT = "dd/MM HH:mm";

    private DateFormat dateFormatYear = new SimpleDateFormat(DATE_FORMAT_YEAR);
    private DateFormat dateFormatShort = new SimpleDateFormat(DATE_FORMAT_SHORT);

    public static void main(String[] args) {
        String message = "Priorbank 20/12 16:40. Na vashu kartu zachisleno 3659.41 BYN. Dostupnaja summa: 3692.29 BYN. Spravka: 80172899292";
        Matcher matcher = Pattern.compile(SMS_INCOME_REGEXP)
                .matcher(message);

        System.out.println(matcher.find());
    }

    public PriorSmsParser() {
    }

    @Override
    public Transaction parseToTransaction(String message, DateTime defaultDate) throws ParseException {

        Matcher matcher = Pattern.compile(SMS_SPENDING_REGEXP).matcher(message);
        if (matcher.find()) {
            return getCommonTransaction(matcher, defaultDate);
        } else {
            matcher = Pattern.compile(SMS_INCOME_REGEXP).matcher(message);
            if (matcher.find()) {
                return getIncomeTransaction(matcher, defaultDate);
            }
        }

        return null;
    }

    private Transaction getIncomeTransaction(Matcher matcher, DateTime defaultDate) throws ParseException {
        DateTime dateTime = getDateTimeShort(matcher, defaultDate);
        Balance balance = getBalance(matcher, dateTime, 6);
        Transaction transaction = new Transaction(
                null,
                null,
                null,
                null,
                getCurrencyType(matcher, 5),
                dateTime,
                getAmount(matcher, 4),
                getOperationType(matcher, 3),
                "",
                false,
                true);
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

// Priorbank. Karta 5***1451 09-02-2019 14:53:56. Oplata 61.96 BYN. BLR SOU INTERNETBANK.
// Dostupno:228.50 BYN. Spravka: 80172899292

//Priorbank. Karta 5***1451 31-01-2019 09:43:44. Nalichnye v bankomate 10.00 BYN. BLR GRUSHEVKA VB1 BPSB A.
// Dostupno:358.58 BYN. Spravka: 80172899292

    private Transaction getCommonTransaction(Matcher matcher, DateTime defaultDate)
            throws ParseException {
        DateTime dateTime = getDateTime(matcher, defaultDate);
        Balance balance = getBalance(matcher, dateTime, 8);
        Transaction transaction = new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 6),
                dateTime,
                getAmount(matcher, 5),
                getOperationType(matcher, 4),
                matcher.group(7),
                false,
                true);
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private DateTime getDateTime(Matcher matcher, DateTime defaultDate) {
        String dateTimeString = matcher.group(2) + " " + matcher.group(3);
        Date date;
        try {
            date = dateFormatYear.parse(dateTimeString);
        } catch (ParseException e) {
            return defaultDate;
        }
        return date != null ? new DateTime(date) : defaultDate;
    }

    private DateTime getDateTimeShort(Matcher matcher, DateTime defaultDate) {
        String dateTimeString = matcher.group(1) + " " + matcher.group(2);
        Date date;
        try {
            date = dateFormatShort.parse(dateTimeString);
        } catch (ParseException e) {
            return defaultDate;
        }
        return date != null ? new DateTime(date).withYear(defaultDate.getYear()) : defaultDate;
    }

    private Balance getBalance(Matcher matcher, DateTime dateTime, int group)
            throws ParseException {
        Double balanceAmount = parseDouble(matcher, group);
        return new Balance(null, balanceAmount, dateTime);
    }

    private String getCard(Matcher matcher) {
        return matcher.group(1);
    }

    private Currency.CurrencyType getCurrencyType(Matcher matcher, int group) {
        String currency = matcher.group(group);
        return Currency.CurrencyType.getByValue(currency);
    }

    private Double getAmount(Matcher matcher, int group) throws ParseException {
        Double parse = parseDouble(matcher, group);
        return Math.abs(parse);
    }

    private Transaction.Type getOperationType(Matcher matcher, int group) throws ParseException {
        String opeartion = matcher.group(group);
        if ("Oplata".equalsIgnoreCase(opeartion) || "Nalichnye v bankomate".equalsIgnoreCase(opeartion))
            return Transaction.Type.SPENDING;
        else {
            return Transaction.Type.INCOME;
        }
    }


    private Double parseDouble(Matcher matcher, int group) throws ParseException {
        String doubleString = matcher.group(group).replace(",", ".");
        return Double.parseDouble(doubleString);
    }


}
