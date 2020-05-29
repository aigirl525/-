package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Messenger mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message message = Message.obtain(null,MyConstants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg","hello,this is client.");
            message.setData(data);
            message.replyTo = mGetReplymessenger;
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final Messenger mGetReplymessenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_SERVICE:
                    Log.e("123456","receive msg from Service:" + msg.getData().getString("reply"));
                    break;
                default:super.handleMessage(msg);
            }

        }
    }
    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
