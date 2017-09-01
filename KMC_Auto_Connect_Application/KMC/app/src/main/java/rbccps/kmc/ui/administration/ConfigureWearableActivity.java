package rbccps.kmc.ui.administration;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import rbccps.kmc.R;
import rbccps.kmc.timer.TimerThreadService;

public class ConfigureWearableActivity extends Activity {


    public static String tagName;

    public static Typeface typeBold;
    public static Typeface typelight;
    public static Typeface typeRegular;
    public static boolean disconnectButtonPressed = false;
    private BluetoothAdapter mBLEAdapter;
    private BluetoothManager bmanager;
    Button setButton;
    Button checkButton;
    Button startButton;
    Button exitButton;
    TextView healthcareWorker;
    TextView aliasName;
    TextView serverIP;
    String healthcareWorkerID;
    String wearableID;
    String wearableAliasID;
    String serverIPAddress;
    Spinner wearableIDSpinner;
    String[] wearableDevices;
    RadioButton http;
    RadioButton https;
    RadioGroup connectivityGroup;

    public static SharedPreferences HealthcareWorkerIDpref;
    public static SharedPreferences WearebleAliasIDpref;
    public static SharedPreferences WearebleIDpref;
    public static SharedPreferences ServerdataCountpref;
    public static SharedPreferences FiledataCountpref;
    public static SharedPreferences connectionTypepref;
    public static SharedPreferences serverIPAddresspref;


    public static SharedPreferences.Editor HealthcareWorkerIDeditor;
    public static SharedPreferences.Editor WearebleAliasIDeditor;
    public static SharedPreferences.Editor WearebleIDeditor;
    public static SharedPreferences.Editor ServerdataCounteditor;
    public static SharedPreferences.Editor FiledataCounteditor;
    public static SharedPreferences.Editor connectionTypeeditor;
    public static SharedPreferences.Editor serverIPAddresseditor;


    public static String HealthcareWorkerID = "";
    public static String WearebleID = "";
    public static String WearebleAliasID = "";
    public static String ServerIPAddress = "";
    public static Context context;

    public static int ServerdataCount = 0;
    public static int FiledataCount = 0;
    public static String connectionType = "";
    public static String connectionTypeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        // Add FLAG to keep Screen ON //
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = getApplicationContext();
        setButton = (Button) findViewById(R.id.setbutton);
        checkButton = (Button) findViewById(R.id.checkbutton);
        startButton = (Button) findViewById(R.id.startbutton);
        exitButton = (Button) findViewById(R.id.exitbutton);
        healthcareWorker = (TextView) findViewById(R.id.healthCareWorkerIDeditText);
        aliasName = (TextView) findViewById(R.id.aliasIDeditText);
        serverIP = (TextView) findViewById(R.id.serverIPAddresseditText);
        wearableIDSpinner = (Spinner) findViewById(R.id.wearablespinner);
        http = (RadioButton) findViewById(R.id.httpradioButton);
        https = (RadioButton) findViewById(R.id.httpsradioButton);
        connectivityGroup = (RadioGroup) findViewById(R.id.radiogroup);

        wearableDevices = new String[]{"KMC100","KMC101","KMC102","KMC103","KMC104","KMC105","KMC106","KMC107","KMC108","KMC109", "KMC110"};
        ArrayAdapter<String> wearableDeviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wearableDevices);
        wearableIDSpinner.setAdapter(wearableDeviceAdapter);

        HealthcareWorkerIDpref = context.getSharedPreferences("HealthcareWorkerIDSharedPreference", MODE_PRIVATE);
        HealthcareWorkerIDeditor = HealthcareWorkerIDpref.edit();

        HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID", "");
        Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + HealthcareWorkerID);

        healthcareWorker.setText(HealthcareWorkerID);

        WearebleIDpref = context.getSharedPreferences("WearebleIDSharedPreference", MODE_PRIVATE);
        WearebleIDeditor = WearebleIDpref.edit();

        WearebleID = WearebleIDpref.getString("WearebleID", wearableID);
        Log.e("WearebleID", "WearebleID is " + WearebleID);

        WearebleAliasIDpref = context.getSharedPreferences("WearebleAliasIDSharedPreference", MODE_PRIVATE);
        WearebleAliasIDeditor = WearebleAliasIDpref.edit();

        WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID", wearableAliasID);
        Log.e("WearebleAliasID", "WearebleAliasID is " + WearebleAliasID);

        aliasName.setText(WearebleAliasID);


        ServerdataCountpref = context.getSharedPreferences("ServerdataCountSharedPreference", MODE_PRIVATE);
        ServerdataCounteditor = ServerdataCountpref.edit();
        ServerdataCount = ServerdataCountpref.getInt("ServerdataCount", 0);

        FiledataCountpref = context.getSharedPreferences("FiledataCountSharedPreference", MODE_PRIVATE);
        FiledataCounteditor = FiledataCountpref.edit();
        FiledataCount = FiledataCountpref.getInt("FiledataCount", 0);

        connectionTypepref = context.getSharedPreferences("connectionTypepref", MODE_PRIVATE);
        connectionTypeeditor = connectionTypepref.edit();
        connectionType = connectionTypepref.getString("connectionTypepref", "");

        serverIPAddresspref = context.getSharedPreferences("serverIPAddresspref", MODE_PRIVATE);
        serverIPAddresseditor = serverIPAddresspref.edit();
        serverIPAddress = serverIPAddresspref.getString("serverIPAddresspref", "");
        serverIP.setText(serverIPAddress);

     if(WearebleID != null) {
            if (WearebleID.contains("KMC")) {

                int spinnerPosition = wearableDeviceAdapter.getPosition(WearebleID);

                //set the default according to value
                wearableIDSpinner.setSelection(spinnerPosition);
            }
        }

        // Get the value in preferences
            if(connectionType.trim().contains("http")){
                Log.e(connectionType,connectionType);
                connectivityGroup.check(R.id.httpradioButton);
            } else if(connectionType.trim().contains("https")){
                connectivityGroup.check(R.id.httpsradioButton);
            }

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


/*

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
*/

                healthcareWorkerID = healthcareWorker.getText().toString().trim();
                wearableAliasID = aliasName.getText().toString().trim();
                wearableID = wearableIDSpinner.getSelectedItem().toString().trim();
                serverIPAddress = serverIP.getText().toString().trim();

                if(http.isChecked()){

                    Log.e("HTTP","HTTP Checked");
                    connectionTypeeditor.putString("connectionTypepref", "http");
                    connectionTypeeditor.commit();

                } else if(https.isChecked()){

                    Log.e("HTTPS","HTTPS Checked");
                    connectionTypeeditor.putString("connectionTypepref", "https");
                    connectionTypeeditor.commit();
                }


                Log.e("Set Button Clicked", healthcareWorkerID + "," + wearableAliasID + "," + wearableID+","+serverIPAddress);

                String _HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID", "");
                Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + _HealthcareWorkerID);

                String _WearebleID = WearebleIDpref.getString("WearebleID",wearableID);
                Log.e("WearebleID", "WearebleID is " + _WearebleID);

                String _WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID", wearableAliasID);
                Log.e("WearebleAliasID", "WearebleAliasID is " + _WearebleAliasID);

                String _ServerIPAddress = serverIPAddresspref.getString("serverIPAddresspref", serverIPAddress);
                Log.e("serverIPAddress", "serverIPAddress is " + _ServerIPAddress);

                HealthcareWorkerIDeditor.putString("HealthcareWorkerID", healthcareWorkerID);
                boolean a = HealthcareWorkerIDeditor.commit();
                HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID","");
                Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + HealthcareWorkerID);

                WearebleIDeditor.putString("WearebleID", wearableID);
                boolean b = WearebleIDeditor.commit();
                WearebleID = WearebleIDpref.getString("WearebleID",wearableID);
                Log.e("WearebleID", "WearebleID is " + WearebleID);

                WearebleAliasIDeditor.putString("WearebleAliasID", wearableAliasID);
                boolean c = WearebleAliasIDeditor.commit();
                WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID",wearableAliasID);
                Log.e("WearebleAliasID", "WearebleAliasID is " + WearebleAliasID);

                serverIPAddresseditor.putString("serverIPAddresspref", serverIPAddress);
                boolean d = serverIPAddresseditor.commit();
                ServerIPAddress = serverIPAddresspref.getString("serverIPAddresspref",serverIPAddress);
                Log.e("serverIPAddress", "serverIPAddress is " + ServerIPAddress);

                if(!(_WearebleAliasID.equals(WearebleAliasID))) {

                    updateFileSharedPreference();
                    Toast.makeText(ConfigureWearableActivity.this, "SAVED DATA SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    String root = Environment.getExternalStorageDirectory() +
                            File.separator + ("KMC").trim();

                    String fileName = "ServerLog" + ".csv";
                    fileName = fileName.replaceAll("\\s", "").trim();

                    File file = new File(root, fileName);

                    if (file.exists()) {
                        file.delete();
                        Log.e("ServerLog", "Deleted");
                    }

                    String wearablefileName = "WearableLog" + ".csv";
                    wearablefileName = wearablefileName.replaceAll("\\s", "").trim();

                    File wearablefile = new File(root, wearablefileName);

                    if (wearablefile.exists()) {
                        wearablefile.delete();
                        Log.e("wearablefile", "Deleted");
                    }
                }

                if(!(_WearebleID.equals(WearebleID))) {

                    updateFileSharedPreference();
                    Toast.makeText(ConfigureWearableActivity.this, "SAVED DATA SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                    String root = Environment.getExternalStorageDirectory() +
                            File.separator + ("KMC").trim();

                    String fileName = "ServerLog" + ".csv";
                    fileName = fileName.replaceAll("\\s", "").trim();

                    File file = new File(root, fileName);

                    if (file.exists()) {
                        file.delete();
                        Log.e("ServerLog", "Deleted");
                    }

                    String wearablefileName = "WearableLog" + ".csv";
                    wearablefileName = wearablefileName.replaceAll("\\s", "").trim();

                    File wearablefile = new File(root, wearablefileName);

                    if (wearablefile.exists()) {
                        wearablefile.delete();
                        Log.e("wearablefile", "Deleted");
                    }
                }


                /*boolean success = TimerThreadService.updateUserSharedPreference(healthcareWorkerID,wearableAliasID,wearableID);

                if(success){
                Toast.makeText(ConfigureWearableActivity.this, "Saved !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ConfigureWearableActivity.this, "Failed Try Again !", Toast.LENGTH_SHORT).show();
                }*/
                }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfigureWearableActivity.this, CheckstatusActivity.class));
                finish();
                // Start the application with UI //

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                healthcareWorkerID = healthcareWorker.getText().toString().trim();
                wearableAliasID = aliasName.getText().toString().trim();
                wearableID = wearableIDSpinner.getSelectedItem().toString().trim();

                if(http.isChecked()){

                    Log.e("HTTP","HTTP Checked");
                    connectionTypeSelected = "http";

                } else if(https.isChecked()){

                    Log.e("HTTPS", "HTTPS Checked");
                    connectionTypeSelected = "https";
                }


                Log.e("Start Button Clicked", healthcareWorkerID + "," + wearableAliasID + "," + wearableID);


                HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID", "");
                Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + HealthcareWorkerID);

                WearebleID = WearebleIDpref.getString("WearebleID",wearableID);
                Log.e("WearebleID", "WearebleID is " + WearebleID);

                WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID",wearableAliasID);
                Log.e("WearebleAliasID", "WearebleAliasID is " + WearebleAliasID);

                connectionTypepref = context.getSharedPreferences("connectionTypepref", MODE_PRIVATE);
                connectionTypeeditor = connectionTypepref.edit();
                connectionType = connectionTypepref.getString("connectionTypepref", "");

                if(healthcareWorkerID.trim().equals(HealthcareWorkerID.trim()) && wearableID.trim().equals(WearebleID.trim()) && wearableAliasID.trim().equals(WearebleAliasID.trim()) && connectionTypeSelected.equals(connectionType)){

                    Toast.makeText(ConfigureWearableActivity.this, "SERVICE STARTED", Toast.LENGTH_SHORT).show();
                    startService(new Intent(ConfigureWearableActivity.this, TimerThreadService.class));
                    finish();

                } else {

                    Toast.makeText(ConfigureWearableActivity.this, "PRESS SET BUTTON TO SAVE CHANGES", Toast.LENGTH_SHORT).show();

                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(ConfigureWearableActivity.this, TimerThreadService.class));
                finish();
            }
        });

        // finish();
    }


    public static void updateFileSharedPreference() {

        FiledataCountpref = context.getSharedPreferences("FiledataCountSharedPreference", MODE_PRIVATE);
        FiledataCounteditor = FiledataCountpref.edit();
        FiledataCounteditor.putInt("FiledataCount", 0);
        FiledataCounteditor.commit();

        ServerdataCountpref = context.getSharedPreferences("ServerdataCountSharedPreference", MODE_PRIVATE);
        ServerdataCounteditor = ServerdataCountpref.edit();
        ServerdataCounteditor.putInt("ServerdataCount", 0);
        ServerdataCounteditor.commit();

        Log.e("FiledataCount", "FiledataCount is " + FiledataCount);
        Log.e("ServerdataCount", "ServerdataCount is " + ServerdataCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ConfigureActivity", "onResume");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void turnoffBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.disable();
        Log.e("AdminScreen", "Turning Bluetooth off");
        Toast.makeText(getApplicationContext(), "Turning OFF Bluetooth",
                Toast.LENGTH_SHORT).show();
    }

    private void turnonBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.enable();
        Log.e("AdminScreen", "Turning Bluetooth On");
        Toast.makeText(getApplicationContext(), "Turning ON Bluetooth ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //stopService(new Intent(ConfigureWearableActivity.this, BluetoothService.class));
        //stopService(new Intent(AdminScreen.this,TimerThreadService.class));
        //turnoffBLE();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 1000);*/
    }
}
