package by.laguta.skryaga.service.impl;

import android.test.AndroidTestCase;
import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.SmsParser;
import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static by.laguta.skryaga.dao.model.Transaction.Type.INCOME;
import static by.laguta.skryaga.dao.model.Transaction.Type.SPENDING;

@RunWith(JUnit4.class)
public class PriorSmsParserTest extends AndroidTestCase {
    private SmsParser smsParser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        smsParser = new PriorSmsParser(mContext);
    }

    public void testParseSmsOplata() throws ParseException {
        // Given
        String sms = "Priorbank. Karta 5***1451 09-02-2019 14:53:56. Oplata 61.96 BYN. BLR SOU INTERNETBANK. Dostupno:228.50 BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 2, 9, 14, 53, 56);
        Transaction expected = new Transaction(
                null,
                null, null,
                "5***1451",
                Currency.CurrencyType.BYR,
                date,
                61.96,
                SPENDING,
                "BLR SOU INTERNETBANK.",
                false,
                true);
        Balance balance = new Balance(null, 228.5d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsSpendingEmptyBalance() throws ParseException {
        // Given
        String sms = "Priorbank. Karta 5***1451 19-01-2019 09:30:11. Oplata 42.27 BYN. BLR SOU INTERNETBANK.  . Spravka: 80172899292";
        DateTime date = new DateTime(2019, 1, 19, 9, 30, 11);
        Transaction expected = new Transaction(
                null,
                null, null,
                "5***1451",
                Currency.CurrencyType.BYR,
                date,
                42.27,
                SPENDING,
                "BLR SOU INTERNETBANK. ",
                false,
                true);
        Balance balance = new Balance(null, null, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsCash() throws ParseException {
        // Given
        String sms = "Priorbank. Karta 5***1451 31-01-2019 09:43:44. Nalichnye v bankomate 10.00 BYN. BLR GRUSHEVKA VB1 BPSB A. Dostupno:358.58 BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 1, 31, 9, 43, 44);
        Transaction expected = new Transaction(
                null,
                null, null,
                "5***1451",
                Currency.CurrencyType.BYR,
                date,
                10.00,
                SPENDING,
                "BLR GRUSHEVKA VB1 BPSB A.",
                false,
                true);
        Balance balance = new Balance(null, 358.58, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsSpendingUsd() throws ParseException {
        // Given
        String sms = "Priorbank. Karta 5***1451 31-01-2019 09:43:44. Oplata 17.13 USD. GBR WWW.ALIEXPRESS.COM. Dostupno:938.62 BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 1, 31, 9, 43, 44);
        Transaction expected = new Transaction(
                null,
                null, null,
                "5***1451",
                Currency.CurrencyType.USD,
                date,
                17.13,
                SPENDING,
                "GBR WWW.ALIEXPRESS.COM.",
                false,
                true);
        Balance balance = new Balance(null, 938.62, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsIncome() throws ParseException {
        // Given
        String sms = "Priorbank 20/01 16:40. Na vashu kartu zachisleno 659.41 BYN. Dostupnaja summa: 692.29 BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 1, 20, 16, 40, 0);
        Transaction expected = new Transaction(
                null,
                null, null,
                null,
                Currency.CurrencyType.BYR,
                date,
                659.41,
                INCOME,
                "Income operation",
                false,
                true);
        Balance balance = new Balance(null, 692.29, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected, new DateTime(2019, 2, 13, 16, 40, 0));
    }

    public void testParseSmsIncomeEmptyBalance() throws ParseException {
        // Given
        String sms = "Priorbank 28/02 13:50. Na vashu kartu zachisleno 1111.55 BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 2, 28, 13, 50, 0);
        Transaction expected = new Transaction(
                null,
                null, null,
                null,
                Currency.CurrencyType.BYR,
                date,
                1111.55,
                INCOME,
                "Income operation",
                false,
                true);
        Balance balance = new Balance(null, null, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected, new DateTime(2019, 3, 13, 16, 40, 0));
    }

    public void testParseSmsRefund() throws ParseException {
        // Given
        String sms = "Priorbank. Na kartu 5***1451 proizveden vozvrat v summe 0.06 BYN po operacii v NLD UBER. Dostupno:904.37BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 2, 10, 15, 21, 11);
        Transaction expected = new Transaction(
                null,
                null, null,
                "5***1451",
                Currency.CurrencyType.BYR,
                date,
                0.06,
                INCOME,
                "po operacii v NLD UBER.",
                false,
                true);
        Balance balance = new Balance(null, 904.37, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected, date);
    }

    public void testParseEmptyBalance() throws ParseException {
        // Given
        String sms = "Priorbank 21/09 11:44. Informiruem o zachislenii na vashu kartu. Spravka: 8017289929";
        DateTime date = new DateTime(2018, 9, 21, 11, 44, 0);
        Transaction expected = new Transaction(
                null,
                null, null,
                null,
                null,
                date,
                null,
                INCOME,
                "Income operation",
                false,
                true);
        Balance balance = new Balance(null, null, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected, new DateTime(2019, 2, 13, 0, 44, 0));
    }

    private void checkParseSms(String sms, Transaction expected, DateTime defaultDate) throws ParseException {
        Transaction actual = smsParser.parseToTransaction(sms, defaultDate);
        assertEquals(expected, actual);
    }

    private void checkParseSms(String sms, Transaction expected) throws ParseException {
        checkParseSms(sms, expected, new DateTime());
    }
}