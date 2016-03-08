package by.laguta.skryaga;

import android.app.Application;
import by.laguta.skryaga.service.util.HelperFactory;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;

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
        Iconify.with(new MaterialCommunityModule());
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseConnector();
        super.onTerminate();
    }
}
