package by.laguta.skryaga.service.model;

import by.laguta.skryaga.dao.model.Currency;
import org.joda.time.DateTime;

import java.util.List;

public class TransactionDayListModel {

    private DateTime date;
    private Double amount;
    private Currency.CurrencyType currency;
    private List<TransactionUIModel> transactions;

    public TransactionDayListModel() {
    }

    public TransactionDayListModel(DateTime date, Double amount, Currency.CurrencyType currency, List<TransactionUIModel> transactions) {
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.transactions = transactions;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Currency.CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(Currency.CurrencyType currency) {
        this.currency = currency;
    }

    public List<TransactionUIModel> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionUIModel> transactions) {
        this.transactions = transactions;
    }
}
