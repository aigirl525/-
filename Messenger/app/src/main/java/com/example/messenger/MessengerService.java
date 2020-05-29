package com.example.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessengerService extends Service {

    private final Messenger messenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_CLIENT:
                    Log.e("123456","receive msg from Client:" + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message replayMessage = Message.obtain(null,MyConstants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","嗯，你的消息我已经收到，稍后会恢复你。");
                    replayMessage.setData(bundle);
                    try {
                        client.send(replayMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:super.handleMessage(msg);
            }

        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
