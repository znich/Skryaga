package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

public class ShowDailySpendingPreference extends SwitchPreference {
    public ShowDailySpendingPreference(Context context) {
        super(context);
    }

    public ShowDailySpendingPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (callChangeListener(isChecked())) {
            persistBoolean(isChecked());
        }
    }
}
