package sjri.iisc.ac.in.nicuapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vasanth on 8/9/15.
 */

public class NICUHome extends ActionBarActivity {

    public static boolean NICUactivityStarted = false;
    public static boolean writing_archiving;
    public static ProgressBar progressBar;
    private BluetoothAdapter mBLEAdapter;
    private BluetoothManager bmanager;
    Intent startAdminScreen;


    @Override
    protected void onResume() {
        super.onResume();
        NICUactivityStarted = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        NICUactivityStarted = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        NICUactivityStarted = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NICUactivityStarted = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nicuhome2);

        // Add FLAG to keep Screen ON //
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        NICUactivityStarted = true;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        writing_archiving = true;

        final ImageView babytempiv = (ImageView) findViewById(R.id.babyTempImageView);
        final ImageView momtempiv = (ImageView) findViewById(R.id.momtempimageView);//
        final ImageView babyormotherImageView = (ImageView) findViewById(R.id.babyormotherImageView);
        final TextView babytemp = (TextView) findViewById(R.id.babyTempTV);//babyTempTV
        final TextView momtemp = (TextView) findViewById(R.id.momTemperatureTV);
        final ImageView touchBabySideIV = (ImageView) findViewById(R.id.babyTouchImageView);
        final ImageView touchMotherSideIV = (ImageView) findViewById(R.id.momTouchImageView);
        final ImageButton stopButton = (ImageButton) findViewById(R.id.disconnectimageButton);
        final TextView babyTitle = (TextView) findViewById(R.id.babyTitleTV);
        final TextView momorroomTitle = (TextView) findViewById(R.id.momorroomTitleTV);
        final TextView sensorName = (TextView) findViewById(R.id.sensorName);
        final TextView disconnectTV = (TextView) findViewById(R.id.disconnectTV);
        final TextView tiltAngleTV = (TextView) findViewById(R.id.tiltAngle);
        final TextView sensorBatteryVoltage = (TextView) findViewById(R.id.sensorBattery);

        Typeface typeBold = Typeface.createFromAsset(getAssets(),"Aller_Bd.ttf");
        Typeface typelight = Typeface.createFromAsset(getAssets(),"Aller_Lt.ttf");
        Typeface typeRegular = Typeface.createFromAsset(getAssets(),"Aller_Rg.ttf");

        babyTitle.setTypeface(typelight);
        momorroomTitle.setTypeface(typelight);
        sensorName.setTypeface(typeBold);
        babytemp.setTypeface(typeRegular);
        momtemp.setTypeface(typeRegular);
        disconnectTV.setTypeface(typeBold);
        tiltAngleTV.setTypeface(typeRegular);


        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.enable();
        Toast.makeText(getApplicationContext(), "Turning ON Bluetooth",
                Toast.LENGTH_SHORT).show();

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothService.disconnect = true;
                AdminScreen.disconnectButtonPressed = true;
                stopService(new Intent(NICUHome.this,BluetoothService.class));
                turnoffBLE();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAdminScreen = new Intent(NICUHome.this, AdminScreen.class);
                        startAdminScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NICUHome.this.startActivity(startAdminScreen);
                        finish();
                    }
                }, 1000);
            }
        });

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(writing_archiving){
                                    progressBar.setVisibility(View.VISIBLE);
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }


                                if(BluetoothService.disconnect && AdminScreen.disconnectButtonPressed){

                                    stopService(new Intent(NICUHome.this,BluetoothService.class));
                                    turnoffBLE();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startAdminScreen = new Intent(NICUHome.this, AdminScreen.class);
                                            startAdminScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            NICUHome.this.startActivity(startAdminScreen);
                                            BluetoothService.disconnect = false;
                                            AdminScreen.disconnectButtonPressed = false;
                                            finish();
                                        }
                                    }, 1000);
                                }


                                if(BluetoothService.t1Value != null && BluetoothService.livedata) {

                                    float t1Value = Float.parseFloat(BluetoothService.t1Value);
                                    float t2Value = Float.parseFloat(BluetoothService.t2Value);

                                    babytemp.setText(BluetoothService.t1Value);
                                    momtemp.setText(BluetoothService.t2Value);
                                    sensorName.setText(BluetoothService.sensorName);

                                    tiltAngleTV.setText(String.format("%.02f",BluetoothService.degree_y)+" "+ (char) 0x00B0);

                                    if(BluetoothService.vBat < 2.4){
                                        sensorBatteryVoltage.setTextColor(Color.RED);
                                    } else if(BluetoothService.vBat > 2.4 && BluetoothService.vBat < 2.45){
                                        sensorBatteryVoltage.setTextColor(Color.RED);
                                    } else if(BluetoothService.vBat > 2.45){
                                        sensorBatteryVoltage.setTextColor(Color.BLUE);
                                    } else if(BluetoothService.vBat > 2.5){
                                        sensorBatteryVoltage.setTextColor(Color.GREEN);
                                    }

                                    sensorBatteryVoltage.setText(String.format("%.02f",BluetoothService.vBat)+" V");
                                    if(BluetoothService.touch1 && BluetoothService.touch2){
                                        babyormotherImageView.setImageResource(R.drawable.momandchild);
                                    } else{
                                        babyormotherImageView.setImageResource(R.drawable.onlytemp);
                                    }

                                    if(BluetoothService.touch1){
                                        babyTitle.setText("BABY");
                                        touchBabySideIV.setImageResource(R.drawable.babyandtemp);

                                        if(BluetoothService.touch2){
                                            momorroomTitle.setText("MOTHER");
                                            touchMotherSideIV.setImageResource(R.drawable.momandtemp);
                                            babyormotherImageView.setImageResource(R.drawable.momandchild);
                                        } else {
                                            momorroomTitle.setText("ROOM");
                                            touchMotherSideIV.setImageResource(R.drawable.onlytemp);
                                            babyormotherImageView.setImageResource(R.drawable.babylogo);
                                        }
                                    } else {
                                        babyTitle.setText("ROOM");
                                        touchBabySideIV.setImageResource(R.drawable.onlytemp);

                                        if(BluetoothService.touch2){
                                            momorroomTitle.setText("MOTHER");
                                            touchMotherSideIV.setImageResource(R.drawable.momandtemp);
                                            babyormotherImageView.setImageResource(R.drawable.mom);
                                        } else {
                                            momorroomTitle.setText("ROOM");
                                            touchMotherSideIV.setImageResource(R.drawable.onlytemp);
                                            babyormotherImageView.setImageResource(R.drawable.onlytemp);
                                        }
                                    }

                                    if(t1Value >=20 && t1Value <=21){
                                        babytempiv.setImageResource(R.drawable.temp1);
                                    }

                                    if(t1Value >=21.1 && t1Value <=23){
                                        babytempiv.setImageResource(R.drawable.temp2);
                                    }

                                    if(t1Value >=23.1 && t1Value <=25){

                                        babytempiv.setImageResource(R.drawable.temp3);
                                    }

                                    if(t1Value >=25.1 && t1Value <=27){

                                        babytempiv.setImageResource(R.drawable.temp4);
                                    }

                                    if(t1Value >=27.1 && t1Value <=29){

                                        babytempiv.setImageResource(R.drawable.temp5);
                                    }

                                    if(t1Value >=29.1 && t1Value <=31){

                                        babytempiv.setImageResource(R.drawable.temp6);
                                    }

                                    if(t1Value >=31.1 && t1Value <=33){

                                        babytempiv.setImageResource(R.drawable.temp7);
                                    }

                                    if(t1Value >=33.1){
                                        babytempiv.setImageResource(R.drawable.temp8);
                                    }

                                    if(t2Value >=20 && t2Value <=21){
                                        momtempiv.setImageResource(R.drawable.temp1);
                                    }

                                    if(t2Value >=21.1 && t2Value <=23){
                                        momtempiv.setImageResource(R.drawable.temp2);
                                    }

                                    if(t2Value >=23.1 && t2Value <=25){
                                        momtempiv.setImageResource(R.drawable.temp3);
                                    }

                                    if(t2Value >=25.1 && t2Value <=27){
                                        momtempiv.setImageResource(R.drawable.temp4);
                                    }

                                    if(t2Value >=27.1 && t2Value <=29){
                                        momtempiv.setImageResource(R.drawable.temp5);
                                    }

                                    if(t2Value >=29.1 && t2Value <=31){
                                        momtempiv.setImageResource(R.drawable.temp6);
                                    }

                                    if(t2Value >=31.1 && t2Value <=33){
                                        momtempiv.setImageResource(R.drawable.temp7);
                                    }

                                    if(t2Value >=33.1){
                                        momtempiv.setImageResource(R.drawable.temp8);
                                    }
                                }
                            }
                        });
                        Thread.sleep(5000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void turnoffBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turning OFF Bluetooth",
                Toast.LENGTH_SHORT).show();
    }
}
