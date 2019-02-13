package by.laguta.skryaga.service.impl;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 26.12.2014 16:25
 *
 * @author Anatoly
 */
public class UssdReceiveService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String text = event.getText().toString();
        if (event.getClassName().equals("android.app.AlertDialog") && BalanceServiceImpl.isBalanceRetrievingActive()) {
            performGlobalAction(GLOBAL_ACTION_BACK);
            Intent intent = new Intent(BalanceServiceImpl.BALANCE_UPDATE);
            intent.putExtra("message", text);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}
