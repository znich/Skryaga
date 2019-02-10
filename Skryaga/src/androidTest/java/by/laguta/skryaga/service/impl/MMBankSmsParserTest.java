package by.laguta.skryaga.service.impl;

import by.laguta.skryaga.dao.model.Balance;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.SmsParser;
import junit.framework.TestCase;
import org.joda.time.DateTime;

import java.text.ParseException;

import static by.laguta.skryaga.dao.model.Currency.CurrencyType;
import static by.laguta.skryaga.dao.model.Transaction.Type.INCOME;
import static by.laguta.skryaga.dao.model.Transaction.Type.SPENDING;

public class MMBankSmsParserTest extends TestCase {

    private SmsParser smsParser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        smsParser = new MMBankSmsParser();
    }

    public void testParseSmsFee() throws ParseException {
        // Given
        String sms = "Karta 4.9141 01.12.2014 23:31:15 Retail -10000 BYR FEE "
                + "FOR SMS-NOTIFICATION www.mmbank.by BLR OK. Dostupno 2369407 BYR";
        DateTime date = new DateTime(2014, 12, 1, 23, 31, 15);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                10000,
                SPENDING,
                "FEE FOR SMS-NOTIFICATION www.mmbank.by",
                false,
                true);
        Balance balance = new Balance(null, 2369407d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsSpending() throws ParseException {
        // Given
        String sms = "Karta 4.9141 08.11.2014 20:47:43 Retail -200000 BYR SHOP"
                + " \"SOSEDI\" MINSK BLR OK. Dostupno 9104880 BYR";
        DateTime date = new DateTime(2014, 11, 8, 20, 47, 43);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                200000,
                SPENDING,
                "SHOP \"SOSEDI\" MINSK",
                false,
                true);
        Balance balance = new Balance(null, 9104880d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsUsd() throws ParseException {
        // Given
        String sms = "Karta 4.9141 10.12.2014 19:35:15 ATM -100 USD ATMMMB HO05"
                + " ZERKALO MINSK BLR OK. Dostupno 8643682 BYR";
        DateTime date = new DateTime(2014, 12, 10, 19, 35, 15);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.USD,
                date,
                100,
                SPENDING,
                "ATMMMB HO05 ZERKALO MINSK",
                false,
                true);
        Balance balance = new Balance(null, 8643682d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsIncome() throws ParseException {
        // Given
        String sms = "Karta 4.9141 09.12.2014 11:56:05 Izmenenie ostatka +8800117 BYR "
                + "OK. Dostupno 9876232 BYR";
        DateTime date = new DateTime(2014, 12, 9, 11, 56, 5);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                8800117d,
                INCOME,
                "",
                false,
                true);
        Balance balance = new Balance(null, 9876232d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsIncomeDenomination() throws ParseException {
        // Given
        String sms = "Karta 4.9141 09.12.2014 11:56:05 Izmenenie ostatka +1600,24 BYN "
                + "OK. Dostupno 2600,58 BYN";
        DateTime date = new DateTime(2014, 12, 9, 11, 56, 5);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                1600.24,
                INCOME,
                "",
                false,
                true);
        Balance balance = new Balance(null, 2600.58d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsSpendingDenomination() throws ParseException {
        // Given
        String sms = "Karta 4.9141 07.07.2016 16:08:14 Retail -16,89 BYN UNIVERSAM \"SOSEDI\"" +
                " MINSK BLR OK. Dostupno 604,15 BYN";
        DateTime date = new DateTime(2016, 7, 7, 16, 8, 14);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                16.89,
                SPENDING,
                "UNIVERSAM \"SOSEDI\" MINSK",
                false,
                true);
        Balance balance = new Balance(null, 604.15d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsReversal() throws ParseException {
        // Given
        String sms = "Karta 4.9141 30.11.2014 15:12:06 Reversal +200000 BYR \"KOMAROV.RYNOK\" "
                + "BPSB ATM- MINSK BLR OK. Dostupno 2529407 BYR";
        DateTime date = new DateTime(2014, 11, 30, 15, 12, 6);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                200000d,
                INCOME,
                "\"KOMAROV.RYNOK\" BPSB ATM- MINSK",
                false,
                true);
        Balance balance = new Balance(null, 2529407d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsRejection() throws ParseException {
        // Given
        String sms = "Karta 4.9141 16.10.2014 19:53:06 Retail -61500 BYR "
                + "RESTORAN \"MAKDONALDS\" MINSK BLR OTKAZ. Dostupno 6857140 BYR";
        DateTime date = new DateTime(2014, 10, 16, 19, 53, 6);
        Transaction expected = new Transaction(
                null,
                null, null,
                "4.9141",
                CurrencyType.BYR,
                date,
                61500,
                SPENDING,
                "RESTORAN \"MAKDONALDS\" MINSK",
                false,
                false);
        Balance balance = new Balance(null, 6857140d, date);
        balance.setTransaction(expected);
        expected.setBalance(balance);

        // When
        // Then
        checkParseSms(sms, expected);
    }

    public void testParseSmsInternet() {
        // Given
        String sms = "Karta 4.9141 - Oplata Internet (BYFLY,ZALA,Maksifon) 150000 BYR "
                + "Kod: 76b44f2 Opr.N: 8578565";

        // When
        // Then
        try {
            checkParseSms(sms, null);
            fail();
        } catch (ParseException e) {
            //ok
        }
    }

    private void checkParseSms(String sms, Transaction expected) throws ParseException {
        Transaction actual = smsParser.parseToTransaction(sms, new DateTime());
        assertEquals(expected, actual);
    }
}