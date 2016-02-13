package by.laguta.skryaga.service.model;

import by.laguta.skryaga.dao.model.Currency;

import java.math.BigDecimal;

/**
 * Author : Anatoly
 * Created : 01.02.2016 23:14
 *
 * @author Anatoly
 */
public class Goal {

    private BigDecimal amount;

    private Currency.CurrencyType currencyType;

    public Goal(BigDecimal amount, Currency.CurrencyType currencyType) {
        this.amount = amount;
        this.currencyType = currencyType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency.CurrencyType getCurrencyType() {
        return currencyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goal goal = (Goal) o;

        if (amount != null ? !amount.equals(goal.amount) : goal.amount != null) return false;
        if (currencyType != goal.currencyType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (currencyType != null ? currencyType.hashCode() : 0);
        return result;
    }
}
