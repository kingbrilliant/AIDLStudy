package com.example.study.aidlstudy;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.study.aidllibrary.ILibraryAIDLInterface;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection serviceConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ILibraryAIDLInterface libraryAIDLInterface = ILibraryAIDLInterface.Stub.asInterface(service);
            try {
                int yan = libraryAIDLInterface.sellYan(10);
                Toast.makeText(MainActivity.this, "" + yan, Toast.LENGTH_LONG).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.add.action");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.example.study.server");
                bindService(intent, serviceConnection1, BIND_AUTO_CREATE);
            }
        });
    }
}
