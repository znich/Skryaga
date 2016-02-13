package by.laguta.skryaga.service.util;

import android.content.Context;
import by.laguta.skryaga.dao.util.DBConnector;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:25
 *
 * @author Anatoly
 */
public class HelperFactory {

    private static DBConnector dbConnector;
    private static ServiceHelper serviceHelper;

    public static void initialize(Context context) {
        if (dbConnector == null) {
            dbConnector = OpenHelperManager.getHelper(context, DBConnector.class);
        }
        if (serviceHelper == null) {
            serviceHelper = new ServiceHelper(context);
        }
    }

    public static void releaseConnector() {
        OpenHelperManager.releaseHelper();
        dbConnector = null;
    }

    public static DBConnector getDaoHelper() {
        return dbConnector;
    }

    public static ServiceHelper getServiceHelper() {
        return serviceHelper;
    }
}
