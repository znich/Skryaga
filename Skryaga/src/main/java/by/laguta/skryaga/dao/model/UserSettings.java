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

    public UserSettings() {
    }

    public UserSettings(Long id, Integer salaryDate, Integer prepaidDate) {
        this.id = id;
        this.salaryDate = salaryDate;
        this.prepaidDate = prepaidDate;
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

}
