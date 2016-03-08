package by.laguta.skryaga.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import by.laguta.skryaga.R;
import by.laguta.skryaga.activity.dialog.GoalDialog;
import by.laguta.skryaga.activity.dialog.GoalDialogListener;
import by.laguta.skryaga.dao.GoalTransactionDao;
import by.laguta.skryaga.dao.TransactionDao;
import by.laguta.skryaga.dao.model.GoalTransaction;
import by.laguta.skryaga.dao.model.Transaction;
import by.laguta.skryaga.service.model.GoalTransactionUIModel;
import by.laguta.skryaga.service.model.TransactionUIModel;
import by.laguta.skryaga.service.util.ConvertUtil;
import by.laguta.skryaga.service.util.CurrencyUtil;
import by.laguta.skryaga.service.util.HelperFactory;
import org.joda.time.format.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Author : Anatoly
 * Created : 18.02.2016 0:23
 *
 * @author Anatoly
 */
public class TransactionsAdapter
        extends RecyclerView.Adapter<TransactionsAdapter.TransactionListItemHolder>
        implements TransactionsUpdateListener {

    private List<TransactionUIModel> transactions;

    private Context context;

    public TransactionsAdapter(List<TransactionUIModel> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @Override
    public TransactionListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction, parent, false);

        return new TransactionListItemHolder(v, this);
    }

    @Override
    public void onBindViewHolder(TransactionListItemHolder holder, int position) {
        holder.populate(transactions.get(position), context);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onTransactionsUpdated(TransactionUIModel transaction) {
        int index = findTransactionIndex(transaction.getId());
        if (index > 0) {
            transactions.get(index).populateWith(transaction);
            notifyItemChanged(index);
        }
    }

    private int findTransactionIndex(Long id) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onTransactionAdded(
            TransactionUIModel transaction, TransactionUIModel parentTransaction) {
        int index = findTransactionIndex(parentTransaction.getId());
        transactions.add(++index, transaction);
        notifyItemInserted(index);
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

        public void populate(final TransactionUIModel transaction, Context context) {
            this.context = context;
            initializeDate(transaction);

            initializeAmount(transaction);

            initializeMassage(transaction);

            initializeImageButton(transaction);
        }

        private void initializeDate(TransactionUIModel transaction) {
            TextView date = (TextView) itemView.findViewById(R.id.transactionDate);
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
            DateTimeParser parser = dateTimeFormatter.getParser();
            DateTimePrinter printer = dateTimeFormatter.getPrinter();
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .append(printer, parser).toFormatter();
            date.setText(transaction.getMessageDate().toString(formatter));
        }

        private void initializeAmount(TransactionUIModel transaction) {
            ImageView transactionImage = (ImageView) itemView.findViewById(
                    R.id.transactionTypeImage);
            int drawable = Transaction.Type.INCOME.equals(transaction.getType())
                    ? R.drawable.ic_arrow_downward_white_36dp
                    : R.drawable.ic_arrow_upward_white_36dp;
            transactionImage.setBackgroundResource(drawable);

            TextView amountView = (TextView) itemView.findViewById(R.id.transactionAmount);
            amountView.setText(CurrencyUtil.formatCurrency(
                    transaction.getAmount(), transaction.getCurrencyType()));

        }

        private void initializeMassage(TransactionUIModel transaction) {
            TextView message = (TextView) itemView.findViewById(R.id.transactionMessage);
            message.setText(transaction.getMessage());
        }

        private void initializeImageButton(final TransactionUIModel transaction) {
            ImageButton goalImage = (ImageButton) itemView.findViewById(R.id.goalTransactionButton);

            if (transaction.getGoalTransaction() != null
                    || Transaction.Type.INCOME.equals(transaction.getType())) {
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

        private GoalDialogListener createGoalDialogListener(final TransactionUIModel transaction) {
            return new GoalDialogListener() {
                @Override
                public void onGoalTransactionSaving(GoalTransactionUIModel goalTransaction) {
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

}
