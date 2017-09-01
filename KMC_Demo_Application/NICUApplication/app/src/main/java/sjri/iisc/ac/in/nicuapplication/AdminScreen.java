package sjri.iisc.ac.in.nicuapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AdminScreen extends Activity {

    public static String tagName;

    public static Typeface typeBold;
    public static Typeface typelight;
    public static Typeface typeRegular;
    public static boolean disconnectButtonPressed = false;
    private BluetoothAdapter mBLEAdapter;
    private BluetoothManager bmanager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearable_select_activity);
        // Add FLAG to keep Screen ON //
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        turnonBLE();

/*

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {

                                                  Log.e("Admin Screen Thread", "Running");

                                              if (BluetoothService.disconnect) {

                                                      Log.e("Admin Screen Disconnect", "Running");
                                                      stopService(new Intent(AdminScreen.this, BluetoothService.class));
                                                      Log.e("Admin Screen","Service Stopped");
                                                      turnoffBLE();

                                                      new Handler().postDelayed(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              turnonBLE();
                                                              startService(new Intent(AdminScreen.this, BluetoothService.class));
                                                          }
                                                      },1000);

                                                  } else if(!BluetoothService.bluetoothScanning){
                                                      //stopService(new Intent(AdminScreen.this,BluetoothService.class));
                                                      //turnoffBLE();
                                                      Toast.makeText(getApplicationContext(), "...Scanning Error, Try Restarting...",
                                                              Toast.LENGTH_LONG).show();
                                                  }
                                          }
                                      });
                        Thread.sleep(1000);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
*/


        ImageButton confirmButton = (ImageButton) findViewById(R.id.confirmimageButton);
        //TextView confirmText = (TextView) findViewById(R.id.confirmTextView);
        //confirmText.setTypeface(typeBold);


        typeBold = Typeface.createFromAsset(getAssets(),"Aller_Bd.ttf");
        typelight = Typeface.createFromAsset(getAssets(),"Aller_Lt.ttf");
        typeRegular = Typeface.createFromAsset(getAssets(),"Aller_Rg.ttf");

        Spinner staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.brew_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);


        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));

                tagName = (String) parent.getItemAtPosition(position);

                TextView selectedText = (TextView) findViewById(R.id.selectedTag);
                selectedText.setTypeface(typeBold);
                selectedText.setText(tagName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Another Service - Handle Bluetooth Start Stop through Thread

                //startService(new Intent(AdminScreen.this, TimerThreadService.class));
                startService(new Intent(AdminScreen.this, BluetoothService.class));
                Intent startMonitor = new Intent(AdminScreen.this, NICUHome.class);
                startMonitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startMonitor);
                //finish();
            }
        });
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
        stopService(new Intent(AdminScreen.this, BluetoothService.class));
        //stopService(new Intent(AdminScreen.this,TimerThreadService.class));
        turnoffBLE();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        },1000);
    }
}