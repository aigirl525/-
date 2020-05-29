package com.xx.leo_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xx.leo_service.ILeoAidl;
import com.xx.leo_service.IOnNewBookArrivedListener;
import com.xx.leo_service.Person;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private ILeoAidl iLeoAidl;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        bindService();
    }

    private void initView() {
        btn = (Button) findViewById(R.id.but_click);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iLeoAidl.addPerson(new Person("leo", 3));
                    List<Person> persons = iLeoAidl.getPersonList();
                    Log.e(TAG, persons.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xx.leo_service", "com.xx.leo_service.LeoAidlService"));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: success");
            iLeoAidl = ILeoAidl.Stub.asInterface(service);
            try {
                iLeoAidl.registerListener(onNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: success");
            iLeoAidl = null;
        }
    };

    @Override
    protected void onDestroy() {

        if (iLeoAidl != null && iLeoAidl.asBinder().isBinderAlive()){
            try {
                Log.e(TAG, "unregister Listener:" + onNewBookArrivedListener);
                iLeoAidl.unregisterListener(onNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(connection);
        super.onDestroy();
    }

    private IOnNewBookArrivedListener onNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void OnNewBookArrived(Person newBook) throws RemoteException {
            handler.obtainMessage(MESSAGE_NEW_BOOK_ARRICED,newBook).sendToTarget();
        }
    };
    private static final int MESSAGE_NEW_BOOK_ARRICED = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_NEW_BOOK_ARRICED:
                    Log.e(TAG, "receive new book:" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
