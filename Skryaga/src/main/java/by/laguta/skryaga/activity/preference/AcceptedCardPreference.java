package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import by.laguta.skryaga.R;
import by.laguta.skryaga.service.util.Settings;

import static org.jsoup.helper.StringUtil.isBlank;

public class AcceptedCardPreference extends EditTextPreference {

    private static final String FORMAT = "%s. %s: \"%s\"";

    public AcceptedCardPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        updateSummary(Settings.getInstance().getAcceptedCardNumber());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        updateSummary(getEditText().getText().toString());
    }

    private void updateSummary(String acceptedCardNumber) {
        CharSequence summary = getContext().getString(R.string.acceptedCardSummary);
        if (!isBlank(acceptedCardNumber)) {
            String currentMessage = getContext().getString(R.string.acceptedCardSummaryCurrent);
            summary = String.format(FORMAT, summary, currentMessage, acceptedCardNumber);
        }
        setSummary(summary);
    }
}
