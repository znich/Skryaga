package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "goal_transaction")
public class GoalTransaction {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "transaction")
    private Transaction transaction;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "exchange_rate")
    private ExchangeRate exchangeRate;

    public GoalTransaction() {
    }

    public GoalTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoalTransaction that = (GoalTransaction) o;

        if (exchangeRate != null ? !exchangeRate.equals(that.exchangeRate)
                : that.exchangeRate != null)
            return false;
        if (!id.equals(that.id)) return false;
        //noinspection RedundantIfStatement
        if (!transaction.equals(that.transaction)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + transaction.hashCode();
        result = 31 * result + (exchangeRate != null ? exchangeRate.hashCode() : 0);
        return result;
    }
}
