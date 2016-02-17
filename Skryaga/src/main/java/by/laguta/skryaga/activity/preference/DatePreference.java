package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import by.laguta.skryaga.R;
import org.joda.time.DateTime;

/**
 * Author : Anatoly
 * Created : 14.02.2016 17:39
 *
 * @author Anatoly
 */
public class DatePreference extends DialogPreference {

    private DatePicker datePicker;

    private Integer dayOfMonth = 1;

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(context.getText(R.string.ok));
        setNegativeButtonText(context.getText(R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        datePicker = new DatePicker(getContext(), null);
        datePicker.setCalendarViewShown(false);
        return datePicker;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        DateTime dateTime = new DateTime();

        datePicker.updateDate(
                dateTime.getYear(), dateTime.getMonthOfYear() - 1, dayOfMonth);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            dayOfMonth = datePicker.getDayOfMonth();
            if (callChangeListener(dayOfMonth)) {
                persistInt(dayOfMonth);
                updateSummary();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 1);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        dayOfMonth = restoreValue ? getPersistedInt(dayOfMonth) : (Integer) defaultValue;
        updateSummary();
    }

    private void updateSummary() {
        setSummary("" + dayOfMonth);
    }
}
