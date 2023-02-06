package com.bgck.pc2mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.hwangjr.rxbus.RxBus;
/**
 * @version 版本号_ 2023/1/19 17:43
 * @since 2022302
 */
public class WifiConnectChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                RxBus.get().post(Constants.RxBusEventType.WIFI_CONNECT_CHANGE_EVENT, networkInfo.getState());
            }
        }
    }
}
