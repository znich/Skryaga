package by.laguta.skryaga.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import by.laguta.skryaga.R;
import by.laguta.skryaga.activity.adapter.view.DateItem;
import by.laguta.skryaga.activity.adapter.view.TransactionItem;
import by.laguta.skryaga.activity.adapter.view.ListItem;
import by.laguta.skryaga.activity.dialog.GoalDialog;
import by.laguta.skryaga.dao.GoalTransactionDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.GoalTransaction;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.model.GoalTransactionUIModel;
import by.laguta.skryaga.service.model.TransactionUIModel;
import by.laguta.skryaga.service.util.ConvertUtil;
import by.laguta.skryaga.service.util.CurrencyUtil;
import by.laguta.skryaga.service.util.HelperFactory;
import com.joanzapata.iconify.widget.IconTextView;
import org.joda.time.DateTime;
import org.joda.time.format.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author : Anatoly
 * Created : 18.02.2016 0:23
 *
 * @author Anatoly
 */
public class TransactionsAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements TransactionsUpdateListener {


    public static final String DATE_FORMAT = "dd MMMM yy";
    private List<ListItem> consolidatedList;

    private Context context;

    public TransactionsAdapter(Map<DateTime, List<TransactionUIModel>> transactions, Context context) {
        this.context = context;
        populateConsolidatedList(transactions);
    }

    private void populateConsolidatedList(Map<DateTime, List<TransactionUIModel>> transactions) {
        consolidatedList = new ArrayList<>();
        for (DateTime dateTime : transactions.keySet()) {
            DateItem dateItem = new DateItem(formatDate(dateTime));
            consolidatedList.add(dateItem);

            for (TransactionUIModel transactionUIModel : transactions.get(dateTime)) {
                TransactionItem transactionItem = new TransactionItem(transactionUIModel);
                consolidatedList.add(transactionItem);
            }
        }
    }

    private String formatDate(DateTime dateTime) {
        return dateTime.toString(createDateFormatter(DATE_FORMAT));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction, parent, false);

        //return new TransactionListItemHolder(v, this);


        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case ListItem.TYPE_TRANSACTION:
                View v1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.transaction, parent, false);
                viewHolder = new TransactionListItemHolder(v1, this);
                break;

            case ListItem.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.date_list_item, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case ListItem.TYPE_TRANSACTION:
                TransactionItem transactionItem = (TransactionItem) consolidatedList.get(position);
                TransactionListItemHolder transactionListItemHolder = (TransactionListItemHolder) holder;
                transactionListItemHolder.populate(transactionItem.getModel(), context);
                break;
            case ListItem.TYPE_DATE:
                DateItem dateItem = (DateItem) consolidatedList.get(position);
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                dateViewHolder.populate(dateItem.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    @Override
    public void onTransactionsUpdated(TransactionUIModel transaction) {
        int index = findTransactionIndex(transaction.getId());
        if (index > 0) {
            ((TransactionItem) consolidatedList.get(index)).getModel().populateWith(transaction);
            notifyItemChanged(index);
        }
    }

    private int findTransactionIndex(Long id) {
        for (int i = 0; i < consolidatedList.size(); i++) {
            ListItem listItem = consolidatedList.get(i);
            if (listItem.getType() == ListItem.TYPE_TRANSACTION) {
                if (((TransactionItem) listItem).getModel().getId().equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void onTransactionAdded(TransactionUIModel transaction, TransactionUIModel parentTransaction) {
        int index = findTransactionIndex(parentTransaction.getId());
        consolidatedList.add(++index, new TransactionItem(transaction));
        notifyItemInserted(index);
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    private static DateTimeFormatter createDateFormatter(String dateFormat) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
        DateTimeParser parser = dateTimeFormatter.getParser();
        DateTimePrinter printer = dateTimeFormatter.getPrinter();
        return new DateTimeFormatterBuilder().append(printer, parser).toFormatter();
    }


    public static class TransactionListItemHolder extends RecyclerView.ViewHolder {

        private static final String TAG = TransactionListItemHolder.class.getSimpleName();
        private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";

        private TransactionDao transactionDao;
        private GoalTransactionDao goalTransactionDao;

        private Context context;
        private TransactionsUpdateListener transactionsUpdateListener;

        public TransactionListItemHolder(
                View itemView, TransactionsUpdateListener transactionsUpdateListener) {
            super(itemView);
            this.transactionDao = HelperFactory.getDaoHelper().getTransactionDao();
            this.goalTransactionDao = HelperFactory.getDaoHelper().getGoalTransactionDao();
            this.transactionsUpdateListener = transactionsUpdateListener;
        }

        public void populate(TransactionUIModel transaction, Context context) {
            this.context = context;
            initializeDate(transaction);

            initializeAmount(transaction);

            initializeMassage(transaction);

            initializeImageButton(transaction);
        }

        private void initializeDate(TransactionUIModel transaction) {
            TextView date = (TextView) itemView.findViewById(R.id.transactionDate);
            DateTimeFormatter formatter = createDateFormatter(DATE_FORMAT);
            date.setText(transaction.getMessageDate().toString(formatter));
        }

        private void initializeAmount(TransactionUIModel transaction) {
            IconTextView transactionIcon = (IconTextView) itemView.findViewById(
                    R.id.transactionTypeImage);

            transactionIcon.setText(transaction.isIncome()
                    ? context.getText(R.string.basketFill)
                    : context.getText(R.string.basketUnFill));

            TextView amountView = (TextView) itemView.findViewById(R.id.transactionAmount);
            Double amount = transaction.getAmount();

            String amountText;
            if (amount != null) {
                amountText = CurrencyUtil.formatCurrency(amount, transaction.getCurrencyType());
            } else {
                amountText = context.getString(R.string.undefined_balance);
            }
            amountView.setText(amountText);

        }

        private void initializeMassage(TransactionUIModel transaction) {
            TextView message = (TextView) itemView.findViewById(R.id.transactionMessage);
            message.setText(transaction.getMessage());
        }

        private void initializeImageButton(final TransactionUIModel transaction) {
            IconTextView goalImage = (IconTextView) itemView.findViewById(
                    R.id.goalTransactionButton);

            if (transaction.getGoalTransaction() != null || transaction.isIncome()) {
                goalImage.setVisibility(View.GONE);
            } else {
                goalImage.setVisibility(View.VISIBLE);
            }

            goalImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGoalTransactionDialog(transaction);
                }
            });
        }

        private void showGoalTransactionDialog(final TransactionUIModel transaction) {
            GoalDialog goalDialog = GoalDialog.getInstance(context);
            goalDialog.show();
            goalDialog.populate(transaction);
            goalDialog.setGoalDialogListener(createGoalDialogListener(transaction));
        }

        private GoalDialog.GoalDialogListener createGoalDialogListener(final TransactionUIModel transaction) {
            return new GoalDialog.GoalDialogListener() {
                @Override
                public void onGoalTransactionSaving(GoalTransactionUIModel goalTransaction) {
                    //TODO: AL  add saving exchange rate
                    TransactionUIModel newTransaction = goalTransaction.getTransaction();
                    if (transaction.getAmount().equals(newTransaction.getAmount())) {
                        saveGoalTransaction(newTransaction.getGoalTransaction());
                    } else {
                        splitTransaction(transaction, goalTransaction);
                    }
                }
            };
        }

        private void saveGoalTransaction(GoalTransactionUIModel goalTransactionUIModel) {
            try {
                TransactionUIModel transactionUIModel = goalTransactionUIModel.getTransaction();
                Transaction transaction = transactionDao.queryForId(transactionUIModel.getId());

                GoalTransaction goalTransaction = new GoalTransaction(transaction);
                goalTransactionDao.create(goalTransaction);

                transaction.setGoalTransaction(goalTransaction);
                transaction.setAmount(transactionUIModel.getAmount());
                transactionDao.update(transaction);

                transactionsUpdateListener.onTransactionsUpdated(transactionUIModel);
            } catch (SQLException e) {
                Log.e(TAG, "Error updating goal transaction", e);
            }
        }

        private void splitTransaction(
                TransactionUIModel transaction, GoalTransactionUIModel goalTransaction) {
            try {
                TransactionUIModel parentTransaction = goalTransaction.getTransaction();
                Double rest = new BigDecimal(transaction.getAmount())
                        .add(new BigDecimal(parentTransaction.getAmount()).negate())
                        .doubleValue();

                saveGoalTransaction(goalTransaction);

                Transaction clone = transactionDao.queryForId(transaction.getId()).getClone();
                clone.setAmount(rest);
                transactionDao.create(clone);
                transactionsUpdateListener.onTransactionAdded(
                        ConvertUtil.convertToUIModel(clone), parentTransaction);
            } catch (SQLException e) {
                Log.e(TAG, "Error splitting transaction", e);
            }
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        public DateViewHolder(View itemView) {
            super(itemView);
        }

        public void populate(String date) {
            TextView dateView = (TextView) itemView.findViewById(R.id.groupTransactionDate);
            dateView.setText(date);
        }
    }

}
