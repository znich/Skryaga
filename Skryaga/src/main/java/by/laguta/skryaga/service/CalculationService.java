package by.laguta.skryaga.service;

import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;
import by.laguta.skryaga.service.model.TransactionUIModel;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

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

    Map<DateTime, List<TransactionUIModel>> getGroupedTransactions();
}
