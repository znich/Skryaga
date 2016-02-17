package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:58
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "currency")
public class Currency {

    public static enum CurrencyType {
        BYR("BYR", "BLR"),
        USD("USD"),
        RUB("RUB"),
        EUR("EUR");

        private List<String> values;

        private CurrencyType(String... values) {
             this.values = Arrays.asList(values);
        }

        public static CurrencyType getByValue(String value) {
            for (CurrencyType type : CurrencyType.values()) {
                 if (type.getValues().contains(value)) {
                     return type;
                 }
            }
            return null;
        }

        private List<String> getValues() {
            return values;
        }
    }

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = "currencyType")
    private CurrencyType currencyType;

    @DatabaseField(canBeNull = false, width = 100, columnName = "name")
    private String name;

    public Currency() {
    }

    public Currency(Long id, CurrencyType currencyType, String name) {
        this.id = id;
        this.currencyType = currencyType;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        if (currencyType != currency.currencyType) return false;
        if (id != null ? !id.equals(currency.id) : currency.id != null) return false;
        if (name != null ? !name.equals(currency.name) : currency.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (currencyType != null ? currencyType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", currencyType=" + currencyType +
                ", name='" + name + '\'' +
                '}';
    }
}
