package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author : Anatoly
 * Created : 04.02.2016 22:16
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "user_settings")
public class UserSettings {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false, columnName = "salaryDate")
    private Integer salaryDate;

    @DatabaseField(canBeNull = false, columnName = "prepaidDate")
    private Integer prepaidDate;

    @DatabaseField(canBeNull = false, columnName = "transactionsProcessed", defaultValue = "false")
    private Boolean transactionsProcessed;

    @DatabaseField(canBeNull = true, columnName = "cardNumber")
    private String cardNumber;

    @DatabaseField(canBeNull = true, columnName = "secureModeEnabled", defaultValue = "false")
    private Boolean secureModeEnabled;

    public UserSettings() {
    }

    public UserSettings(Long id, Integer salaryDate, Integer prepaidDate) {
        this.id = id;
        this.salaryDate = salaryDate;
        this.prepaidDate = prepaidDate;
    }

    public UserSettings(
            Long id, Integer salaryDate, Integer prepaidDate, boolean secureModeEnabled, boolean transactionsProcessed) {
        this.id = id;
        this.salaryDate = salaryDate;
        this.prepaidDate = prepaidDate;
        this.secureModeEnabled = secureModeEnabled;
        this.transactionsProcessed = transactionsProcessed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSalaryDate() {
        return salaryDate;
    }

    public void setSalaryDate(Integer salaryDate) {
        this.salaryDate = salaryDate;
    }

    public Integer getPrepaidDate() {
        return prepaidDate;
    }

    public void setPrepaidDate(Integer prepaidDate) {
        this.prepaidDate = prepaidDate;
    }

    public Boolean isTransactionsProcessed() {
        return transactionsProcessed;
    }

    public void setTransactionsProcessed(Boolean transactionsProcessed) {
        this.transactionsProcessed = transactionsProcessed;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Boolean getSecureModeEnabled() {
        return secureModeEnabled;
    }

    public void setSecureModeEnabled(Boolean secureModeEnabled) {
        this.secureModeEnabled = secureModeEnabled;
    }

    public boolean isSecureModeEnabled() {
        return secureModeEnabled != null && secureModeEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSettings that = (UserSettings) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (prepaidDate != null ? !prepaidDate.equals(that.prepaidDate) : that.prepaidDate != null) {
            return false;
        }
        if (salaryDate != null ? !salaryDate.equals(that.salaryDate) : that.salaryDate != null) {
            return false;
        }

        if (cardNumber != null ? !cardNumber.equals(that.cardNumber) : that.cardNumber != null) {
            return false;
        }

        if (secureModeEnabled != null ? !secureModeEnabled.equals(that.secureModeEnabled) : that.secureModeEnabled != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (transactionsProcessed != null
                ? !transactionsProcessed.equals(that.transactionsProcessed)
                : that.transactionsProcessed != null) {
            return false;
        }



        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (salaryDate != null ? salaryDate.hashCode() : 0);
        result = 31 * result + (prepaidDate != null ? prepaidDate.hashCode() : 0);
        result = 31 * result + (transactionsProcessed != null ? transactionsProcessed.hashCode() : 0);
        return result;
    }
}
