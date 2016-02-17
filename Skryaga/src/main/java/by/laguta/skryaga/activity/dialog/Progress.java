package by.laguta.skryaga.activity.dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Author : Anatoly
 * Created : 16.02.2016 20:42
 *
 * @author Anatoly
 */
public class Progress extends ProgressDialog {

    public Progress(
            Context context, String title, CharSequence message, OnDismissListener listener) {
        super(context);

        setTitle(title);
        setCancelable(false);
        setMessage(message);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (listener != null) {
            setOnDismissListener(listener);
        }
    }
}
