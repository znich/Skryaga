package by.laguta.skryaga.activity.preference;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Author : Anatoly
 * Created : 03.07.2016 13:06
 *
 * @author Anatoly
 */
public class RestoreDBPreference extends BackupPreference {

    public RestoreDBPreference(Context context, AttributeSet attrs) {
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

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getContext(), "Import Successful!", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            showErrorToast();
        }
    }

    private String getBackupFileName() {
        //TODO: AL add choosing backup file from dialog
        return null;
    }
}
