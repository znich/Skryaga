package by.laguta.skryaga.service.impl;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.test.ServiceTestCase;
import android.util.Log;
import by.laguta.skryaga.service.ISkryaga;

public class SmsServiceTest extends ServiceTestCase<SmsService> {

    private static final String TAG = SmsServiceTest.class.getName();

    private SmsService smsService;
    private ISkryaga iSkryaga = null;

    public SmsServiceTest() {
        super(SmsService.class);
    }

    /**
     * Constructor
     *
     * @param serviceClass The type of the service under test.
     */
    public SmsServiceTest(Class<SmsService> serviceClass) {
        super(SmsService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent service = new Intent(getContext(), SmsService.class);
        iSkryaga = (ISkryaga) bindService(service);
        Log.d(TAG, "bind result: " + iSkryaga);
    }

    public void testUpdateFromReceived() throws RemoteException {
        iSkryaga.updateTransactions();
    }

    private Context getTestContext() {
        try {
            return getContext();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}