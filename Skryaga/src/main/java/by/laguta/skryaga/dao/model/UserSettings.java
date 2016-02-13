package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

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

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = "salaryDate")
    private DateTime salaryDate;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = "prepaidDate")
    private DateTime prepaidDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getSalaryDate() {
        return salaryDate;
    }

    public void setSalaryDate(DateTime salaryDate) {
        this.salaryDate = salaryDate;
    }

    public DateTime getPrepaidDate() {
        return prepaidDate;
    }

    public void setPrepaidDate(DateTime prepaidDate) {
        this.prepaidDate = prepaidDate;
    }

    public UserSettings createDefaultSettings() {
        new UserSettings();
        setPrepaidDate(new DateTime().withTimeAtStartOfDay());
        setSalaryDate(new DateTime().withTimeAtStartOfDay());
        return this;
    }
}
