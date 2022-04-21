package com.example.gpstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    LinearLayout change,sum;
    Button bt_start,bt_stop;

    String distance;
//    LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        change = findViewById(R.id.change);
        sum = findViewById(R.id.sum);

        bt_start = findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23){

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
//                        requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},2);

                    }else {
                        startService();
                    }

                }else{
                    startService();
                }
            }
        });

        bt_stop = findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"sum = "+ distance,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                stopService(intent);

            }
        });

        registerReceiver(broadcastReceiver, new IntentFilter(LocationService.BROADCAST_ACTION));

        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled ) {
            // notify user
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("กรุณาเปิดใช้งานตำแหน่ง")
                    .setPositiveButton("ไปที่การตั้งค่า", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            MainActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("ยกเลิก",null)
                    .show();
        }
        if (!network_enabled) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("กรุณาเปิดใช้งานอินเตอร์เน็ต")
                    .setPositiveButton("ไปที่การตั้งค่า", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            MainActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .setNegativeButton("ยกเลิก",null)
                    .show();

        }




    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Update Your UI here..
            Bundle bundle = intent.getExtras();
            distance = bundle.getString("distance");
            String change = bundle.getString("change");
            setLn(change,distance);
        }
    };


    public void setLn(String ch,String distance){

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 100;
        TextView textView = new TextView(getApplicationContext());
        textView.setText(ch);
        textView.setLayoutParams(params);

        TextView textView2 = new TextView(getApplicationContext());
        textView2.setText(distance);
        textView2.setLayoutParams(params);


        change.addView(textView);
        sum.addView(textView2);




    }



    void startService() {
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startService();
                }else {
                    Toast.makeText(getApplicationContext(),"Permission",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
}