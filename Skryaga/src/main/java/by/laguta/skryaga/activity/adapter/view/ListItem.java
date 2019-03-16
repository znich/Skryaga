package by.laguta.skryaga.activity.adapter.view;

public abstract class ListItem {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_TRANSACTION = 1;

    abstract public int getType();
}
