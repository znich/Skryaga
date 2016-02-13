package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:57
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "bank_account")
public class BankAccount {

    @DatabaseField(generatedId = true)
    private long id;

    public static final String PHONE_NUMBER = "phoneNumber";
    @DatabaseField(canBeNull = false, width = 3000, columnName = PHONE_NUMBER)
    private String phoneNumber;

    @DatabaseField(canBeNull = false, width = 500, columnName = "label")
    private String label;

    public BankAccount() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
