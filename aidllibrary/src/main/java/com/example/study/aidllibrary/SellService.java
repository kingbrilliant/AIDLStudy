package com.example.study.aidllibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * description:
 * <p></p>
 *
 * @author wangjie
 * @since 2018/8/8
 */
public class SellService extends Service {

    private ILibraryAIDLInterface.Stub binder = new ILibraryAIDLInterface.Stub() {

        @Override
        public int sellYan(int money) throws RemoteException {
            return 100;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
