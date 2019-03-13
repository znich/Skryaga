package by.laguta.skryaga.activity.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.model.UserSettings;
import by.laguta.skryaga.service.util.Settings;

public class UssdBalanceDialog extends AlertDialog {

    private static final int OK_BUTTON = -1;
    private static final int NO_BUTTON = -2;
    private EditText input;

    private boolean initialButtonState = false;
    private final View layout;

    public UssdBalanceDialog(Context context, final OnClickListener listener) {
        super(context);

        setTitle(context.getString(R.string.dialog_balance_title));
        setMessage(context.getString(R.string.dialog_balance_message));
        setIcon(android.R.drawable.ic_menu_compass);

        addShowListener();

        layout = LayoutInflater.from(context).inflate(R.layout.ussd_dialog, null);
        setView(layout);

        initCardNumberInput(context);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cardValue = input.getText().toString();
                if (!isCardNumberValid(cardValue)) {
                    return;
                }

                UserSettings userSettings = Settings.getInstance().getModel();

                userSettings.setCardNumber(cardValue);

                Settings.getInstance().updateSettings(userSettings);

                listener.onClick(dialog, which);
            }
        };

        setButton(OK_BUTTON, context.getString(android.R.string.yes), onClickListener);
        setButton(NO_BUTTON, context.getString(android.R.string.no), (OnClickListener) null);
    }

    private void initCardNumberInput(Context context) {
        input = (EditText) layout.findViewById(R.id.dialog_balance_input);

        UserSettings model = Settings.getInstance().getModel();
        if (model != null) {
            String cardNumber = model.getCardNumber();
            if (cardNumber != null && !cardNumber.isEmpty()) {
                input.setText(cardNumber);
                initialButtonState = true;
            } else {
                initialButtonState = false;
            }
        }

        input.addTextChangedListener(new CardNumberTextWatcher());
    }

    private void addShowListener() {
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                changeOkButtonState(initialButtonState);
            }
        });
    }

    private void changeOkButtonState(boolean enabled) {
        getButton(OK_BUTTON).setEnabled(enabled);
    }

    private boolean isCardNumberValid(String value) {
        return value != null && value.length() == 4;
    }

    private class CardNumberTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String value = editable.toString();
            if (isCardNumberValid(value)) {
                changeOkButtonState(true);
            } else {
                changeOkButtonState(false);
            }
        }
    }

}







