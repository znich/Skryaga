package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

import static by.laguta.skryaga.dao.model.Currency.CurrencyType;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 19.12.2014 22:03
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "exchange_rate")
public class ExchangeRate implements Comparable<ExchangeRate> {

    @DatabaseField(generatedId = true)
    private Long id;

    public final static String RATE_DATE = "date";
    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = RATE_DATE)
    private DateTime date;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_INTEGER, columnName = "currencyType")
    private CurrencyType currencyType;

    @DatabaseField(canBeNull = false, width = 300, columnName = "bankName")
    private String bankName;

    @DatabaseField(canBeNull = true, width = 2000, columnName = "bankAddress")
    private String bankAddress;

    public final static String BUY_RATE = "buyRate";
    @DatabaseField(canBeNull = false, columnName = BUY_RATE)
    private Double buyRate;

    public final static String SELLING_RATE = "sellRate";
    @DatabaseField(canBeNull = false, columnName = SELLING_RATE)
    private Double sellingRate;


    public ExchangeRate() {
    }

    public ExchangeRate(
            Long id,
            DateTime date,
            CurrencyType currencyType,
            String bankName,
            String bankAddress,
            Double buyRate,
            Double sellingRate) {
        this.id = id;
        this.date = date;
        this.currencyType = currencyType;
        this.bankName = bankName;
        this.bankAddress = bankAddress;
        this.buyRate = buyRate;
        this.sellingRate = sellingRate;
    }

    public void populateWith(ExchangeRate exchangeRate) {
        setBankAddress(exchangeRate.getBankAddress());
        setBankName(exchangeRate.getBankName());
        setBuyRate(exchangeRate.getBuyRate());
        setSellingRate(exchangeRate.getSellingRate());
        setCurrencyType(exchangeRate.getCurrencyType());
        setDate(exchangeRate.getDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public Double getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(Double buyRate) {
        this.buyRate = buyRate;
    }

    public Double getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(Double sellingRate) {
        this.sellingRate = sellingRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ExchangeRate.class != o.getClass()) {
            return false;
        }

        ExchangeRate that = (ExchangeRate) o;

        if (bankAddress != null ? !bankAddress.equals(that.bankAddress) : that.bankAddress != null)
            return false;
        if (bankName != null ? !bankName.equals(that.bankName) : that.bankName != null)
            return false;
        if (buyRate != null ? !buyRate.equals(that.buyRate) : that.buyRate != null)
            return false;
        if (currencyType != that.currencyType) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        //noinspection RedundantIfStatement
        if (sellingRate != null ? !sellingRate.equals(that.sellingRate) : that.sellingRate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (currencyType != null ? currencyType.hashCode() : 0);
        result = 31 * result + (bankName != null ? bankName.hashCode() : 0);
        result = 31 * result + (bankAddress != null ? bankAddress.hashCode() : 0);
        result = 31 * result + (buyRate != null ? buyRate.hashCode() : 0);
        result = 31 * result + (sellingRate != null ? sellingRate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", date=" + date +
                ", currencyType=" + currencyType +
                ", bankName='" + bankName + '\'' +
                ", bankAddress='" + bankAddress + '\'' +
                ", buyRate=" + buyRate +
                ", sellingRate=" + sellingRate +
                '}';
    }

    @Override
    public int compareTo(ExchangeRate another) {
        return sellingRate.compareTo(another.sellingRate);
    }
}
