package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 16.12.2014 23:34
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "balance")
public class Balance {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false, columnName = "amount")
    private Double amount;

    public final static String DATE = "date";
    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = DATE)
    private DateTime date;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "transaction")
    private Transaction transaction;

    public Balance() {
    }

    public Balance(Long id, Double amount, DateTime date) {
        this(id, amount, date, null);
    }

    public Balance(Long id, Double amount, DateTime date, Transaction transaction) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.transaction = transaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Balance balance = (Balance) o;

        if (amount != null ? !amount.equals(balance.amount) : balance.amount != null) return false;
        if (date != null ? !date.equals(balance.date) : balance.date != null) return false;
        //noinspection RedundantIfStatement
        if (id != null ? !id.equals(balance.id) : balance.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
