package by.laguta.skryaga.service.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.telephony.TelephonyManager.*;

public class Connectivity {

    private Context context;

    public Connectivity(Context context) {
        this.context = context;
    }

    /**
     * Get the network info
     *
     * @return
     */
    public NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @return
     */
    public boolean isConnected() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @return
     */
    public boolean isConnectedWifi() {
        NetworkInfo info = getNetworkInfo();
        return info != null
                && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @return
     */
    public boolean isConnectedMobile() {
        NetworkInfo info = getNetworkInfo();
        return info != null
                && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Check if there is fast connectivity
     *
     * @return
     */
    public boolean isConnectedFast() {
        NetworkInfo info = getNetworkInfo();
        return (info != null
                && info.isConnected()
                && isConnectionFast(info.getType(), info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                case NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

}
