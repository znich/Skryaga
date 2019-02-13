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
    private Long id;

    public static final String PHONE_NUMBER = "phoneNumber";
    @DatabaseField(canBeNull = false, width = 3000, columnName = PHONE_NUMBER)
    private String phoneNumber;

    @DatabaseField(canBeNull = false, width = 500, columnName = "label")
    private String label;

    @DatabaseField(canBeNull = true, width = 500, columnName = "default_card")
    private String defaultCard;

    public BankAccount() {
    }

    public BankAccount(Long id, String phoneNumber, String label) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(String defaultCard) {
        this.defaultCard = defaultCard;
    }
}
