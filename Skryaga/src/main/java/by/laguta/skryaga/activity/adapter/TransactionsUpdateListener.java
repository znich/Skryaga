package by.laguta.skryaga.activity.adapter;

import by.laguta.skryaga.service.model.TransactionUIModel;

/**
 * Author : Anatoly
 * Created : 07.03.2016 22:10
 *
 * @author Anatoly
 */
public interface TransactionsUpdateListener {

    void onTransactionsUpdated(TransactionUIModel transaction);

    void onTransactionAdded(TransactionUIModel transaction, TransactionUIModel parentTransaction);
}
