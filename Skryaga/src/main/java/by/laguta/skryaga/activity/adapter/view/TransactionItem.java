package by.laguta.skryaga.activity.adapter.view;

import by.laguta.skryaga.service.model.TransactionUIModel;

public class TransactionItem extends ListItem {

    private TransactionUIModel model;

    public TransactionItem(TransactionUIModel model) {
        this.model = model;
    }

    public TransactionUIModel getModel() {
        return model;
    }

    public void setModel(TransactionUIModel model) {
        this.model = model;
    }

    @Override
    public int getType() {
        return TYPE_TRANSACTION;
    }
}
