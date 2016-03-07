package by.laguta.skryaga.service;

import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.model.TransactionUIModel;

import java.util.List;

public interface CalculationService {


    MainInfoModel getMainInfoModel();

    Double getTotalAmount();

    /**
     *  Spend today amount
     *
     * @return
     */
    Double getTodaySpending();

    Goal calculateGoal();

    List<TransactionUIModel> getAllTransactions();
}
