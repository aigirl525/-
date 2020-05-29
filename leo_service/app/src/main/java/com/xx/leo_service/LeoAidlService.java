package com.xx.leo_service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeoAidlService extends Service {
    //支持并发读/写
    private CopyOnWriteArrayList<Person> persons = new CopyOnWriteArrayList<>();
   //系统专门提供的用于删除跨进程listener的接口
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();
    //private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
    private final static String TAG = "LeoAidlService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "success onBind");
        //使用权限验证功能
        int check = checkCallingOrSelfPermission("com.xx.leo_service.permission.ACCESS_BOOK_SERVICE");
        Log.e(TAG, check+"123");

//        if (check == PackageManager.PERMISSION_DENIED){
//            return null;
//        }
        return iBinder;
    }

    private IBinder iBinder = new ILeoAidl.Stub() {
        @Override
        public void addPerson(Person person) throws RemoteException {
            persons.add(person);
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            return persons;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            Log.e(TAG,"registerListener,current size:" + mListenerList.beginBroadcast());
            mListenerList.finishBroadcast();

//            if (!mListenerList.contains(listener)){
//                mListenerList.add(listener);
//            }else {
//                Log.e(TAG,"already exists.");
//            }
//            Log.e(TAG,"registerListener,size:" + mListenerList.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
        mListenerList.unregister(listener);
        Log.e(TAG,"unregisterListener,current size:" + mListenerList.beginBroadcast());
        mListenerList.finishBroadcast();


//            if (mListenerList.contains(listener)){
//                mListenerList.remove(listener);
//                Log.e(TAG,"unregister Listener succeed.");
//            }else {
//                Log.e(TAG,"not found,can not unregister.");
//            }
//            Log.e(TAG,"unregisterListener,current size:" + mListenerList.size());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("LeoAidlService", "onCreate: success");
        persons.add(new Person("luo",18));
        new Thread(new ServiceWorker()).start();
    }

    private void onNewBookArrived(Person person) throws RemoteException{
        persons.add(person);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0 ; i < N ; i++){
            IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
            if (l != null){
                try {
                    l.OnNewBookArrived(person);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();

//        Log.e(TAG,"onNewBookArrived,notify listeners:" + mListenerList.size());
//        for (int i = 0; i < mListenerList.size();i++){
//            IOnNewBookArrivedListener listener = mListenerList.get(i);
//            Log.e(TAG,"onNewBookArrived,notify listeners:" + listener);
//            listener.OnNewBookArrived(person);
//        }
    }
    private class ServiceWorker implements Runnable{

        @Override
        public void run() {
            //do background processing here.......
            while (!mIsServiceDestoryed.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int grade = persons.size() + 1;
                Person person = new Person("new person" + grade,grade);
                try {
                    onNewBookArrived(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestoryed.set(true);
    }
}



