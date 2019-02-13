package by.laguta.skryaga.service.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import by.laguta.skryaga.R;

/**
 * Revision Info : $Author$ $Date$
 * Author : Anatoly
 * Created : 26.12.2014 15:42
 *
 * @author Anatoly
 */
public class SmsMonitor extends BroadcastReceiver {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent != null
                && intent.getAction() != null
                && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {

            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            String from = messages[0].getDisplayOriginatingAddress();
            //TODO: AL add check from db bank account
            if (shouldBeChecked(from)) {
                StringBuilder bodyText = new StringBuilder();
                for (SmsMessage message : messages) {
                    bodyText.append(message.getMessageBody());
                }
                String body = bodyText.toString();
                Intent smsService = new Intent(context, SmsService.class);
                smsService.putExtra(SmsService.SMS_SENDER, from);
                smsService.putExtra(SmsService.SMS_BODY, body);
                smsService.putExtra(SmsService.SMS_TIMESTAMP, messages[0].getTimestampMillis());
                context.startService(smsService);
            }
        }
    }

    private boolean shouldBeChecked(String from) {
        String[] accounts = context.getResources().getStringArray(R.array.bank_accounts);
        for (String account : accounts) {
            if (account.equalsIgnoreCase(from)) {
                return true;
            }
        }
        return false;
    }
}
