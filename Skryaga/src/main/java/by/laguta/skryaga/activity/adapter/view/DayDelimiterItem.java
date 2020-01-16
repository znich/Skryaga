package by.laguta.skryaga.activity.adapter.view;

public class DayDelimiterItem extends ListItem {
    private String date;
    private String amount;

    public DayDelimiterItem(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int getType() {
        return TYPE_DAY_DELIMITER;
    }
}
