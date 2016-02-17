package by.laguta.skryaga.activity.preference;

import android.content.*;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import by.laguta.skryaga.R;
import by.laguta.skryaga.activity.dialog.Progress;
import by.laguta.skryaga.service.ISkryaga;
import by.laguta.skryaga.service.impl.SmsService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author : Anatoly
 * Created : 15.02.2016 21:11
 *
 * @author Anatoly
 */
public class TransactionsUpdatePreference extends Preference {

    private static final String TAG = TransactionsUpdatePreference.class.getSimpleName();

    private Boolean updated;
    private Progress progressDialog;
    private Context context;
    private ServiceConnection serviceConnection;

    public TransactionsUpdatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.updated = false;
    }

    @Override
    protected void onClick() {
        if (progressDialog == null) {
            progressDialog = createDialog();
        }

        progressDialog.show();
        updateSmsTransactions();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (SmsService.isUpdateComplete()) {
                    timer.cancel();
                    context.unbindService(serviceConnection);
                    persistBoolean(true);
                    updated = true;
                    progressDialog.dismiss();
                }
            }
        }, 500, 500);
    }

    private Progress createDialog() {
        Context currentContext = getContext();
        return new Progress(
                currentContext,
                currentContext.getString(R.string.progressTitle),
                currentContext.getText(R.string.parsing),
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateSummary();
                    }
                });
    }

    private void updateSmsTransactions() {
        Intent service = new Intent(context, SmsService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ISkryaga iSkryaga = ISkryaga.Stub.asInterface(service);
                try {
                    iSkryaga.updateTransactions();
                } catch (RemoteException e) {
                    Log.e(TAG, "Error updating sms transactions");
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        context.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, false);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        updated = restorePersistedValue ? getPersistedBoolean(updated) : (Boolean) defaultValue;
        updateSummary();
    }

    private void updateSummary() {
        setSummary(updated
                ? context.getString(R.string.updated) : context.getString(R.string.needUpdated));
    }
}
