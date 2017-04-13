package com.example.well.ndemo.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.net.entity.resulte.ImagePush;
import com.example.well.ndemo.utils.SPUtils;
import com.example.well.ndemo.utils.SettingsUtils;
import com.google.gson.Gson;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData  处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class NDemoIntentService extends GTIntentService {

    private static final String TAG = "NDemoIntentService";

    public NDemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    /**
     * 处理透传消息<br>  三种情况 app处于前台,app处于后台,app处于关闭状态 也可以省略为两种状态 一种是处于关闭状态,一种是处于前台
     * <p>
     * 目前的话每天会同送一张图片用户侧边栏图片的显示 格式为--->
     * {
     * "type": "image",
     * "data": {
     * "url": "lalal"
     * }
     * }
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        if (BuildConfig.DEBUG) Log.e(TAG, "onReceiveMessageData");
        byte[] payload = msg.getPayload();
        String s = new String(payload);
        Gson gson = new Gson();
        ImagePush imagePush = gson.fromJson(s, ImagePush.class);

        String type = imagePush.type;
        if (TextUtils.equals(type, "image")) {//推送了一张图片
            String url = imagePush.data.url;

            SPUtils.getInstance(getApplication()).put(SettingsUtils.CATCH_IMAGE_PUSH,url);

            Intent intent = new Intent();
            intent.setAction(SettingsUtils.ACTION_IMAGE_PUSH);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }


    }

    /**
     * onReceiveClientId 接收 cid <br>
     *
     * @param context
     * @param clientid
     */
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    /**
     * cid 离线上线通知
     *
     * @param context
     * @param online
     */
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.e(TAG, "onReceiveOnlineState -> " + "online = " + online);
    }

    /**
     * 各种事件处理回执
     *
     * @param context
     * @param cmdMessage
     */
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e(TAG, "onReceiveCommandResult -> " + "cmdMessage = " + cmdMessage.toString());
    }

    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }
}
