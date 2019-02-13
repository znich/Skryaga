package by.laguta.skryaga.activity.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.model.Currency;
import by.laguta.skryaga.dao.model.ExchangeRate;
import by.laguta.skryaga.service.ExchangeRateService;
import by.laguta.skryaga.service.model.GoalTransactionUIModel;
import by.laguta.skryaga.service.model.TransactionUIModel;
import by.laguta.skryaga.service.util.CurrencyUtil;
import by.laguta.skryaga.service.util.HelperFactory;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Author : Anatoly
 * Created : 07.03.2016 13:27
 *
 * @author Anatoly
 */
public class GoalDialog extends AlertDialog {

    private static GoalDialog instance;

    private ExchangeRateService exchangeRateService;

    private EditText amount;
    private EditText rate;
    private EditText goal;

    private boolean valuesRecalculating;
    private TransactionUIModel transaction;
    protected GoalDialogListener goalDialogListener;

    protected GoalDialog(Context context) {
        super(context);
        setTitle(context.getString(R.string.goalDialogTitle));

        setCanceledOnTouchOutside(false);

        View layout = LayoutInflater.from(context).inflate(R.layout.goal_dialog, null);
        setView(layout);

        setButton(
                BUTTON_POSITIVE,
                context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onOk();
                    }
                });
        setButton(
                BUTTON_NEGATIVE,
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        exchangeRateService = HelperFactory.getServiceHelper().getExchangeRateService();

        amount = (EditText) layout.findViewById(R.id.goal_dialog_amount);
        amount.addTextChangedListener(new ExchangeRateTextWatcher(amount));
        rate = (EditText) layout.findViewById(R.id.goal_dialog_rate);
        rate.addTextChangedListener(new ExchangeRateTextWatcher(rate));
        goal = (EditText) layout.findViewById(R.id.goal_dialog_goal);
        goal.addTextChangedListener(new ExchangeRateTextWatcher(goal));
    }

    public static GoalDialog getInstance(Context context) {
        if (instance == null) {
            instance = new GoalDialog(context);
        }
        return instance;
    }


    private void onOk() {
        TransactionUIModel changedTransaction = transaction.getClone();
        GoalTransactionUIModel goalTransaction = new GoalTransactionUIModel(changedTransaction);

        Currency.CurrencyType currencyType = transaction.getCurrencyType();

        Double transactionAmount = Currency.CurrencyType.BYR.equals(currencyType)
                ? getByrAmountValue() : getGoalValue();
        changedTransaction.setAmount(transactionAmount);
        changedTransaction.setGoalTransaction(goalTransaction);
        goalDialogListener.onGoalTransactionSaving(goalTransaction);
    }

    public void populate(TransactionUIModel transaction) {
        this.transaction = transaction;
        ExchangeRate nearestExchangeRate = exchangeRateService.getNearestExchangeRate(
                this.transaction.getTransactionDate());
        Currency.CurrencyType currencyType = transaction.getCurrencyType();
        switch (currencyType) {
            case BYR:
                updateRowsVisibility(true, true, true);
                if (nearestExchangeRate != null) {
                    populateRate(nearestExchangeRate.getSellingRate());
                } else {
                    populateRate(0d);
                }
                populateInput(transaction, amount);
                break;
            case USD:
                updateRowsVisibility(false, false, true);
                populateInput(transaction, goal);
                break;
        }
    }

    private void updateRowsVisibility(
            boolean amountVisibility, boolean rateVisibility, boolean goalVisibility) {
        setRowVisibility(amount, amountVisibility);
        setRowVisibility(rate, rateVisibility);
        setRowVisibility(goal, goalVisibility);
    }

    private void populateInput(TransactionUIModel transaction, EditText editText) {
        editText.setText(CurrencyUtil.formatCurrency(
                transaction.getAmount(), transaction.getCurrencyType(), false));
        editText.setSelection(editText.getText().length());
    }

    private void setRowVisibility(View view, boolean visible) {
        TableRow parent = (TableRow) view.getParent();
        parent.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private Double getByrAmountValue() {
        Double currency = CurrencyUtil.parseCurrency(amount.getText().toString());
        return currency != null ? currency : 0d;
    }

    private Double getRateValue() {
        Double currency = CurrencyUtil.parseCurrency(rate.getText().toString());
        return currency != null ? currency : 0d;
    }

    private Double getMainInputValue() {
        if (transaction.isByrTransaction()) {
            return getByrAmountValue();
        } else {
            return getGoalValue();
        }
    }

    private Double getGoalValue() {
        Double currency = CurrencyUtil.parseCurrency(goal.getText().toString());
        return currency != null ? currency : 0d;
    }

    private void populateAmount(Double byr) {
        amount.setText(CurrencyUtil.formatCurrency(byr, Currency.CurrencyType.BYR, false));
    }

    private void populateRate(Double byrRate) {
        rate.setText(CurrencyUtil.formatCurrency(byrRate, Currency.CurrencyType.BYR, false));
    }

    private void populateGoal(Double usd) {
        goal.setText(CurrencyUtil.formatCurrency(usd, Currency.CurrencyType.USD, false));
    }

    private void setOkEnable(boolean enabled) {
        getButton(BUTTON_POSITIVE).setEnabled(enabled);
    }


    public void setGoalDialogListener(GoalDialogListener goalDialogListener) {
        this.goalDialogListener = goalDialogListener;
    }

    private class ExchangeRateTextWatcher implements TextWatcher {

        private EditText editText;

        public ExchangeRateTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!valuesRecalculating) {
                valuesRecalculating = true;
                recalculateValues(s);
                valuesRecalculating = false;

                validateDialog();
            }
        }

        private void recalculateValues(Editable s) {
            Double editedFieldValue = CurrencyUtil.parseCurrency(s.toString());
            if (editedFieldValue == null) {
                return;
            }

            switch (editText.getId()) {
                case R.id.goal_dialog_amount:
                   /* populateAmount(editedFieldValue);*/
                    Double exchangeRate = getRateValue();
                    populateGoal(CurrencyUtil.convertByrToUsd(
                            new BigDecimal(editedFieldValue), exchangeRate).doubleValue());
                    break;
                case R.id.goal_dialog_rate:
                   /* populateRate(editedFieldValue);*/
                    populateGoal(CurrencyUtil.convertByrToUsd(
                            new BigDecimal(getByrAmountValue()), editedFieldValue).doubleValue());
                    break;
                case R.id.goal_dialog_goal:
                  /*  populateGoal(editedFieldValue);*/
                    Double rateValue = CurrencyUtil.calculateRate(
                            new BigDecimal(getByrAmountValue()), new BigDecimal(editedFieldValue));
                    populateRate(rateValue);
                    break;
            }
        }

        private void validateDialog() {
            if (transaction.isByrTransaction()) {
                validateInput(amount);
            }
            if (transaction.isUsdTransaction()) {
                validateInput(goal);
            }
        }

        private void validateInput(EditText editText) {
            Double mainInputValue = getMainInputValue();
            Double transactionAmount = transaction.getAmount();
            if (mainInputValue > transactionAmount) {
                populateInput(transaction, editText);
            }
            if (mainInputValue <= 0) {
                setOkEnable(false);
            } else {
                setOkEnable(true);
            }
        }
    }

    public static interface GoalDialogListener {
        void onGoalTransactionSaving(GoalTransactionUIModel goalTransaction);
    }
}
