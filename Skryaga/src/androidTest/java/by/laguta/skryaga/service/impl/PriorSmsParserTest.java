package by.laguta.skryaga.service.impl;

import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.SmsParser;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static by.laguta.skryaga.dao.model.Transaction.Type.INCOME;
import static by.laguta.skryaga.dao.model.Transaction.Type.SPENDING;

@RunWith(JUnit4.class)
public class PriorSmsParserTest extends TestCase {
    private SmsParser smsParser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        smsParser = new PriorSmsParser();
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
        String sms = "Priorbank 20/12 16:40. Na vashu kartu zachisleno 3659.41 BYN. Dostupnaja summa: 3692.29 BYN. Spravka: 80172899292";
        DateTime date = new DateTime(2019, 12, 20, 16, 40, 00);
        Transaction expected = new Transaction(
                null,
                null, null,
                null,
                Currency.CurrencyType.BYR,
                date,
                3659.41,
                INCOME,
                "",
                false,
                true);
        Balance balance = new Balance(null, 3692.29, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    private void checkParseSms(String sms, Transaction expected) throws ParseException {
        Transaction actual = smsParser.parseToTransaction(sms, new DateTime());
        assertEquals(expected, actual);
    }
}