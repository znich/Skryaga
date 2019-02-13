package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.os.Environment;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.util.DBConnector;

import java.io.File;

/**
 * Author : Anatoly
 * Created : 03.07.2016 12:06
 *
 * @author Anatoly
 */
public abstract class BackupPreference extends Preference {

    public final String DATE_FILE_NAME_PATTERN = "dd.MM.yyyy-HH.mm.ss";

    public BackupPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        performBackupAction();
    }

    protected abstract void performBackupAction();


    protected String getActualDatabase() {
        return "//data//" + getContext().getPackageName()
                + "//databases//" + DBConnector.DATABASE_NAME;
    }


    protected String getBackupFileDirectoryPath() {
        File file = new File(
                Environment.getExternalStorageDirectory(),
                "//" + getContext().getString(R.string.application_directory)
                        + "//" + getContext().getString(R.string.backupDirectory));
        //noinspection ResultOfMethodCallIgnored
        file.mkdir();
        return file.getPath();
    }

    protected void showErrorToast() {
        Toast.makeText(getContext(), "Backup Failed!", Toast.LENGTH_SHORT).show();
    }

    protected String createBackupFileName() {
       /* DateTime dateTime = new DateTime();
        String datePart = dateTime.toString(DATE_FILE_NAME_PATTERN);
        return "backup-" + datePart + ".db";*/
        return "backup.db";
    }
}
