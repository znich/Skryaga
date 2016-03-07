package by.laguta.skryaga.service.model;

import by.laguta.skryaga.dao.model.ExchangeRate;

/**
 * Author : Anatoly
 * Created : 08.03.2016 0:08
 *
 * @author Anatoly
 */
public class GoalTransactionUIModel {

    private TransactionUIModel transaction;
    private ExchangeRate exchangeRate; //TODO: AL

    public GoalTransactionUIModel() {
    }

    public GoalTransactionUIModel(TransactionUIModel transaction) {
        this.transaction = transaction;
    }

    public TransactionUIModel getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionUIModel transaction) {
        this.transaction = transaction;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
