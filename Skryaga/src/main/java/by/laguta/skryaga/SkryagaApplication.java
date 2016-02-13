package by.laguta.skryaga;

import android.app.Application;
import by.laguta.skryaga.service.util.HelperFactory;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 14.12.2014 19:20
 *
 * @author Anatoly
 */
public class SkryagaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.initialize(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        HelperFactory.releaseConnector();
        super.onTerminate();
    }
}
