package by.laguta.skryaga.service;

import by.laguta.skryaga.service.model.Goal;
import by.laguta.skryaga.service.model.MainInfoModel;

/**
 * Created by Anatoly on 30.01.2016.
 */
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
}
