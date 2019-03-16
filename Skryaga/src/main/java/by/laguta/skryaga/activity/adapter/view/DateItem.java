package by.laguta.skryaga.activity.adapter.view;

public class DateItem extends ListItem {
    private String date;


    public DateItem(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
