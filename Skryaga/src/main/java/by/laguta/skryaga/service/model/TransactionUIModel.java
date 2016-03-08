package by.laguta.skryaga.service.model;

import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.Transaction;
import org.joda.time.DateTime;

/**
 * Author : Anatoly
 * Created : 08.03.2016 0:01
 *
 * @author Anatoly
 */
public class TransactionUIModel {

    private Long id;
    private Double amount;
    private DateTime messageDate;
    private DateTime transactionDate;
    private Transaction.Type type;
    private Currency.CurrencyType currencyType;
    private String message;
    private GoalTransactionUIModel goalTransaction;


    public TransactionUIModel(
            Long id,
            Double amount,
            DateTime messageDate,
            DateTime transactionDate,
            Transaction.Type type,
            Currency.CurrencyType currencyType,
            String message) {
        this(id, amount, messageDate, transactionDate, type, currencyType, message, null);
    }

    public TransactionUIModel(
            Long id,
            Double amount,
            DateTime messageDate,
            DateTime transactionDate,
            Transaction.Type type,
            Currency.CurrencyType currencyType,
            String message,
            GoalTransactionUIModel goalTransaction) {
        this.id = id;
        this.amount = amount;
        this.messageDate = messageDate;
        this.transactionDate = transactionDate;
        this.type = type;
        this.currencyType = currencyType;
        this.message = message;
        this.goalTransaction = goalTransaction;
    }

    public void populateWith(TransactionUIModel transaction) {
        setId(transaction.getId());
        setMessageDate(transaction.getMessageDate());
        setCurrencyType(transaction.getCurrencyType());
        setAmount(transaction.getAmount());
        setType(transaction.getType());
        setMessage(transaction.getMessage());
        setGoalTransaction(transaction.getGoalTransaction());
    }

    public TransactionUIModel getClone() {
        return new TransactionUIModel(
                id, amount, messageDate, transactionDate, type, currencyType, message);
    }

    public boolean isByrTransaction() {
        return Currency.CurrencyType.BYR.equals(getCurrencyType());
    }

    public boolean isUsdTransaction() {
        return Currency.CurrencyType.USD.equals(getCurrencyType());
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

    public DateTime getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(DateTime messageDate) {
        this.messageDate = messageDate;
    }

    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(DateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Transaction.Type getType() {
        return type;
    }

    public void setType(Transaction.Type type) {
        this.type = type;
    }

    public Currency.CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Currency.CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GoalTransactionUIModel getGoalTransaction() {
        return goalTransaction;
    }

    public void setGoalTransaction(GoalTransactionUIModel goalTransaction) {
        this.goalTransaction = goalTransaction;
    }

    public boolean isIncome() {
        return Transaction.Type.INCOME.equals(getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionUIModel that = (TransactionUIModel) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (currencyType != that.currencyType) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (messageDate != null ? !messageDate.equals(that.messageDate) : that.messageDate != null)
            return false;
        if (transactionDate != null
                ? !transactionDate.equals(that.transactionDate) : that.transactionDate != null)
            return false;
        //noinspection RedundantIfStatement
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (messageDate != null ? messageDate.hashCode() : 0);
        result = 31 * result + (transactionDate != null ? transactionDate.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (currencyType != null ? currencyType.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
