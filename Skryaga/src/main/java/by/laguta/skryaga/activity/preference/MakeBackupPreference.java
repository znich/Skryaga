package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Author : Anatoly
 * Created : 03.07.2016 13:08
 *
 * @author Anatoly
 */
public class MakeBackupPreference extends BackupPreference {

    private static final String TAG = MakeBackupPreference.class.getSimpleName();


    public MakeBackupPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void performBackupAction() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {

                String currentDBPath = getActualDatabase();

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(
                        getBackupFileDirectoryPath(), "//" + createBackupFileName());
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(
                        getContext(),
                        "Backup Successful! \n Saved in " + backupDB.getPath(),
                        Toast.LENGTH_SHORT).show();

            } else {
                showErrorToast();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating backup", e);
            showErrorToast();
        }
    }

}
