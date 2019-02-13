package by.laguta.skryaga.service.impl;

import android.content.Context;
import by.laguta.skryaga.R;
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
 * Prior parser
 * <p>
 * Created by Anatoly on 09.02.2019.
 */
public class PriorSmsParser extends BaseParser implements SmsParser {

    private static final String CARD_REGEXP = "\\d+\\*+\\d+";

    private static final String OPERATION_REGEXP = "[^\\d\\-\\+]+";

    private static final String DESCRIPTION_REGEXP = "(?:(?!\\s+Dost| . Spravka).)*";

    private static final String SMS_SPENDING_REGEXP = "^Priorbank\\. Karta"
            + "\\s+(" + CARD_REGEXP + ")\\.?"         // 1 - card
            + "\\s+(" + DATE_REGEXP + ")"       // 2 - date
            + "\\s+(" + TIME_REGEXP + ")\\."    // 3 - time
            + "\\s+(" + OPERATION_REGEXP + ")"  // 4 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 5 - amount
            + "\\s+(\\S{3})\\."                 // 6 - currency
            + "\\s+(" + DESCRIPTION_REGEXP + ")"// 7 - description
            + "\\s+[^:]+:"
            + "(" + DOUBLE_REGEXP + ")?"       // 8 - balance
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

    private static final String SMS_REFUND_REGEXP = "^Priorbank. Na kartu"
            + "\\s+(" + CARD_REGEXP + ")"         // 1 - card
            + "\\s+(" + OPERATION_REGEXP + ")"   // 2 - operation
            + "\\s+(" + DOUBLE_REGEXP + ")"     // 3 - amount
            + "\\s+(\\S{3})"                    // 4 - currency
            + "\\s+(" + DESCRIPTION_REGEXP + ")"  // 5 - description
            + "\\s+[^:]+:"
            + "(" + DOUBLE_REGEXP + ")"       // 6 - balance
            + "";

    private static final String SMS_INCOME_EMPTY_REGEXP = "^Priorbank"
            + "\\s+(" + DATE_REGEXP + ")"       // 1 - date
            + "\\s+(" + TIME_REGEXP + ")\\."        // 2 - time
            + "\\s+Informiruem o zachislenii na vashu kartu\\.";

    private static final String DATE_FORMAT_YEAR = "dd-MM-y HH:mm:ss";
    private static final String DATE_FORMAT_SHORT = "dd/MM HH:mm";

    private DateFormat dateFormatYear = new SimpleDateFormat(DATE_FORMAT_YEAR);
    private DateFormat dateFormatShort = new SimpleDateFormat(DATE_FORMAT_SHORT);

    private Context context;

    public PriorSmsParser(Context context) {
        this.context = context;
    }

    @Override
    public Transaction parseToTransaction(String message, DateTime defaultDate) throws ParseException {
        Matcher matcher = Pattern.compile(SMS_SPENDING_REGEXP).matcher(message);
        if (matcher.find()) {
            return getSpendingTransaction(matcher, defaultDate);
        }
        matcher = Pattern.compile(SMS_INCOME_REGEXP).matcher(message);
        if (matcher.find()) {
            return getIncomeTransaction(matcher, defaultDate);
        }
        matcher = Pattern.compile(SMS_REFUND_REGEXP).matcher(message);
        if (matcher.find()) {
            return getRefundTransaction(matcher, defaultDate);
        }
        matcher = Pattern.compile(SMS_INCOME_EMPTY_REGEXP).matcher(message);
        if (matcher.find()) {
            return getIncomeEmptyAmountTransaction(matcher, defaultDate);
        }
        return null;
    }

    private Transaction getSpendingTransaction(Matcher matcher, DateTime defaultDate)
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
                Transaction.Type.SPENDING,
                matcher.group(7),
                false,
                true);
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private Transaction getIncomeEmptyAmountTransaction(Matcher matcher, DateTime defaultDate) throws ParseException {
        DateTime dateTime = getDateTimeShort(matcher, defaultDate);
        Balance balance = new Balance(null, null, dateTime);
        Transaction transaction = new Transaction(
                null,
                null,
                null,
                null,
                null,
                dateTime,
                null,
                Transaction.Type.INCOME,
                context.getString(R.string.unknown_income_transaction),
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
                Transaction.Type.INCOME,
                context.getString(R.string.unknown_income_transaction),
                false,
                true);
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
    }

    private DateTime getDateTimeShort(Matcher matcher, DateTime defaultDate) {
        String dateTimeString = matcher.group(1) + " " + matcher.group(2);
        Date date;
        try {
            date = dateFormatShort.parse(dateTimeString);
        } catch (ParseException e) {
            return defaultDate;
        }
        if (date != null) {
            DateTime dateTime = new DateTime(date).withYear(defaultDate.getYear());

            if (dateTime.compareTo(defaultDate) > 0) {
                dateTime = dateTime.withYear(defaultDate.getYear() - 1);
            }
            return dateTime;
        }
        return defaultDate;
    }

    private Transaction getRefundTransaction(Matcher matcher, DateTime defaultDate) throws ParseException {
        Balance balance = getBalance(matcher, defaultDate, 6);
        Transaction transaction = new Transaction(
                null,
                null, null,
                getCard(matcher),
                getCurrencyType(matcher, 4),
                defaultDate,
                getAmount(matcher, 3),
                Transaction.Type.INCOME,
                matcher.group(5),
                false,
                true);
        transaction.setBalance(balance);
        balance.setTransaction(transaction);
        return transaction;
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
        Double amount = parseDouble(matcher, group);
        return amount != null ?  Math.abs(amount) : 0d;
    }

}
