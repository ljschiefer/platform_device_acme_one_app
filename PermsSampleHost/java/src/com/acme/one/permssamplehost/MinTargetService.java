package com.acme.one.permssamplehost;

import android.app.IntentService;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

public class MinTargetService extends IntentService {
    private static final String TAG = "MinTargetService";
    private static final String ACTION_GET = "com.acme.one.service.GET";
    private static final String ACTION_NOTIFY = "com.acme.one.service.NOTIFY";
    private static final String ACTION_NOTIFY_RESP = "com.acme.one.service.NOTIFY_RESP";
    private static final String EXTRA_MESSENGER = "com.acme.one.service.EXTRA_MESSENGER";
    private static final String EXTRA_MIN_TGT_SDK = "com.acme.one.service.EXTRA_MIN_TGT_SDK";
    private static final String PERMISSION_NOTIFY = "com.acme.one.permission.NOTIFY_MIN_TGT_SDK";
    private static final String SYSTEM_PROP_MIN_TGT_SDK = "ro.build.version.min_supported_target_sdk";
    private static final int MESSAGE_MIN_TGT_SDK = 1000;

    public MinTargetService() {
        super("MinTargetService");
    }

    public int getMinTargetSdk() {
        return SystemProperties.getInt(SYSTEM_PROP_MIN_TGT_SDK, -1);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (action == null) {
            Log.e(TAG, "No action provided");
        }

        if (TextUtils.equals(action, ACTION_GET)) {
            Messenger msgr = intent.getParcelableExtra(EXTRA_MESSENGER);
            if (msgr != null) {
                Message resp = Message.obtain();
                resp.what = MESSAGE_MIN_TGT_SDK;
                resp.arg1 = getMinTargetSdk();
                try {
                    msgr.send(resp);
                } catch (RemoteException e) {
                    //  Ignore, client has died
                }
            } else {
                Log.e(TAG, "No Messenger provided for response");
            }
        } else if (TextUtils.equals(action, ACTION_NOTIFY)) {
            Intent respIntent = new Intent(ACTION_NOTIFY_RESP);
            respIntent.putExtra(EXTRA_MIN_TGT_SDK, getMinTargetSdk());
            sendBroadcast(respIntent, PERMISSION_NOTIFY);
        } else {
            Log.e(TAG, "Invalid action: " + action);
        }
    }
}