package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import by.laguta.skryaga.R;

public class SecureModePreference extends SwitchPreference {
    public SecureModePreference(Context context) {
        super(context);
    }

    public SecureModePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (callChangeListener(isChecked())) {
            persistBoolean(isChecked());
            updateSummary();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        updateSummary();
    }

    private void updateSummary() {
        String enabledText = getContext().getString(R.string.secureModePreferenceEnabled);
        String disabledText = getContext().getString(R.string.secureModePreferenceDisabled);
        String summary = isChecked() ? enabledText : disabledText;
        setSummary(summary);
    }
}
