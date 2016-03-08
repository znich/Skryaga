package by.laguta.skryaga.service.util;

import by.laguta.skryaga.dao.model.GoalTransaction;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.model.GoalTransactionUIModel;
import by.laguta.skryaga.service.model.TransactionUIModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author : Anatoly
 * Created : 08.03.2016 0:25
 *
 * @author Anatoly
 */
public class ConvertUtil {

    public static TransactionUIModel convertToUIModel(Transaction transaction) {
        GoalTransaction goalTransaction = transaction.getGoalTransaction();
        TransactionUIModel transactionUIModel = new TransactionUIModel(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getMessageDate(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getCurrencyType(),
                transaction.getMessage());
        if (goalTransaction != null) {
            transactionUIModel.setGoalTransaction(convertToUIModel(goalTransaction));
        }
        return transactionUIModel;
    }

    public static List<TransactionUIModel> convertToUIModels(
            Collection<Transaction> transactions) {
        List<TransactionUIModel> result = new ArrayList<TransactionUIModel>();
        for (Transaction transaction : transactions) {
            result.add(convertToUIModel(transaction));
        }

        return result;
    }

    public static GoalTransactionUIModel convertToUIModel(GoalTransaction goalTransaction) {
        //TODO: AL
        return goalTransaction != null ? new GoalTransactionUIModel() : null;
    }
}
