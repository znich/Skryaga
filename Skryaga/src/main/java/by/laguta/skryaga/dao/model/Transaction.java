package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

import static by.laguta.skryaga.dao.model.Currency.CurrencyType;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:55
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "transaction")
public class Transaction {

    public static enum Type {
        INCOME("Credit", "Izmenenie ostatka", "Reversal"),
        SPENDING("Retail", "ATM", "Cash", "Oplata"),
        UNKNOWN;

        private List<String> values;

        private Type(String... values) {
            this.values = Arrays.asList(values);
        }

        public static Type getByValue(String value) {
            for (Type type : Type.values()) {
                if (type.getValues().contains(value)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        private List<String> getValues() {
            return values;
        }
    }

    @DatabaseField(generatedId = true)
    private Long id;

    public static final String MESSAGE_DATE = "message_date";
    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = MESSAGE_DATE)
    private DateTime messageDate;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "bank_account")
    private BankAccount bankAccount;

    @DatabaseField(canBeNull = false, width = 30, columnName = "cardNumber")
    private String cardNumber;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = "currencyType")
    private CurrencyType currencyType;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = "date")
    private DateTime date;

    @DatabaseField(canBeNull = false, columnName = "amount")
    private double amount;

    public static final String TYPE = "type";
    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = TYPE)
    private Type type;

    @DatabaseField(canBeNull = false, width = 300, columnName = "message")
    private String message;

    @DatabaseField(canBeNull = true, width = 3000, columnName = "comment")
    private String comment;

    @DatabaseField(canBeNull = false, defaultValue = "false", columnName = "accumulation")
    private boolean accumulation;

    @DatabaseField(canBeNull = false, defaultValue = "false", columnName = "approved")
    private boolean approved;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "balance")
    private Balance balance;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "goal_transaction")
    private GoalTransaction goalTransaction;

    public Transaction() {
    }

    public Transaction(
            Long id,
            DateTime messageId,
            BankAccount bankAccount,
            String cardNumber,
            CurrencyType currency,
            DateTime date,
            double amount, Type type,
            String message,
            boolean accumulation,
            boolean approved) {
        this(id, messageId, bankAccount, cardNumber, currency, date, amount, type, message, null,
                accumulation, approved, null);
    }

    public Transaction(
            Long id,
            DateTime messageId,
            BankAccount bankAccount,
            String cardNumber,
            CurrencyType currency,
            DateTime date,
            double amount,
            Type type,
            String message,
            String comment,
            boolean accumulation,
            boolean approved,
            Balance balance) {
        this.id = id;
        this.messageDate = messageId;
        this.bankAccount = bankAccount;
        this.cardNumber = cardNumber;
        this.currencyType = currency;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.message = message;
        this.comment = comment;
        this.accumulation = accumulation;
        this.approved = approved;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public DateTime getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(DateTime messageDate) {
        this.messageDate = messageDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAccumulation() {
        return accumulation;
    }

    public void setAccumulation(boolean accumulation) {
        this.accumulation = accumulation;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public GoalTransaction getGoalTransaction() {
        return goalTransaction;
    }

    public void setGoalTransaction(GoalTransaction goalTransaction) {
        this.goalTransaction = goalTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (accumulation != that.accumulation) return false;
        if (Double.compare(that.amount, amount) != 0) return false;
        if (approved != that.approved) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        if (bankAccount != null ? !bankAccount.equals(that.bankAccount) : that.bankAccount != null)
            return false;
        if (cardNumber != null ? !cardNumber.equals(that.cardNumber) : that.cardNumber != null)
            return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (currencyType != that.currencyType) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (goalTransaction != null ? !goalTransaction.equals(that.goalTransaction)
                : that.goalTransaction != null)
            return false;
        if (!id.equals(that.id)) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (messageDate != null ? !messageDate.equals(that.messageDate) : that.messageDate != null)
            return false;
        //noinspection RedundantIfStatement
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id.hashCode();
        result = 31 * result + (messageDate != null ? messageDate.hashCode() : 0);
        result = 31 * result + (bankAccount != null ? bankAccount.hashCode() : 0);
        result = 31 * result + (cardNumber != null ? cardNumber.hashCode() : 0);
        result = 31 * result + (currencyType != null ? currencyType.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (accumulation ? 1 : 0);
        result = 31 * result + (approved ? 1 : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (goalTransaction != null ? goalTransaction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", messageDate=" + messageDate +
                ", bankAccount=" + bankAccount +
                ", cardNumber='" + cardNumber + '\'' +
                ", currencyType=" + currencyType +
                ", date=" + date +
                ", amount=" + amount +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", comment='" + comment + '\'' +
                ", accumulation=" + accumulation +
                ", approved=" + approved +
                ", balance=" + balance +
                '}';
    }
}
