package by.laguta.skryaga.service.model;

/**
 * Author : Anatoly
 * Created : 03.02.2016 0:12
 *
 * @author Anatoly
 */
public class MainInfoModel {

    private Double totalAmount;

    private Double dailyAmount;

    private Double todaySpending;

    private Double restForToday;

    private Goal goal;

    public MainInfoModel() {
    }

    public MainInfoModel(
            Double totalAmount, Double dailyAmount, Double todaySpending,
            Double restForToday, Goal goal) {

        this.totalAmount = totalAmount;
        this.dailyAmount = dailyAmount;
        this.todaySpending = todaySpending;
        this.restForToday = restForToday;
        this.goal = goal;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getDailyAmount() {
        return dailyAmount;
    }

    public void setDailyAmount(Double dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    public Double getTodaySpending() {
        return todaySpending;
    }

    public void setTodaySpending(Double todaySpending) {
        this.todaySpending = todaySpending;
    }

    public Double getRestForToday() {
        return restForToday;
    }

    public void setRestForToday(Double restForToday) {
        this.restForToday = restForToday;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
