package rbccps.kmc.ui.administration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import rbccps.kmc.R;
import rbccps.kmc.timer.TimerThreadService;

public class CheckstatusActivity extends AppCompatActivity implements View.OnClickListener{

    String wearableSync = "Not Connected";
    String wearableSyncTime = "HH.MM.SS";
    String wearableSyncDate = "YYYY-MM-DD";
    FileInputStream fs;
    String serverSync = "Not Connected";
    String serverSyncTime = "HH.MM.SS";
    String serverSyncDate = "YYYY-MM-DD";
    FileInputStream fs_Server;

    TextView wearableTimeTextView;
    TextView wearableDateTextView;
    TextView wearableNameTextView;

    TextView serverTimeTextView;
    TextView serverDateTextView;

    Button backButton;
    public static SharedPreferences WearebleIDpref;
    public static SharedPreferences.Editor WearebleIDeditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkstatus);

        WearebleIDpref = this.getSharedPreferences("WearebleIDSharedPreference", MODE_PRIVATE);
        String wearableid = WearebleIDpref.getString("WearebleID", "");

         wearableTimeTextView = (TextView) findViewById(R.id.wearableSyncTime);
         wearableDateTextView = (TextView) findViewById(R.id.wearableSyncDate);
         wearableNameTextView = (TextView) findViewById(R.id.wearableID);

         serverTimeTextView = (TextView) findViewById(R.id.serversyncTime);
         serverDateTextView = (TextView) findViewById(R.id.serverSyncDate);

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        readFileDataforUpdatingNotification();


        if(!wearableSync.contains("Not Connected")) {

            String tempwearable[] = wearableSync.split("~");
            wearableSyncDate = tempwearable[0];
            wearableSyncTime = tempwearable[1];
        }

        if(!serverSync.contains("Not Connected")) {

            String tempserver[] = serverSync.split("~");
            serverSyncDate = tempserver[0];
            serverSyncTime = tempserver[1];
        }



        Log.e("Wearable", wearableSyncTime +" and " +wearableSyncDate);
        Log.e("Server", serverSyncTime +" and " +serverSyncDate);

        wearableTimeTextView.setText(wearableSyncTime);
        wearableDateTextView.setText(wearableSyncDate);
        wearableNameTextView.setText(wearableid);

        serverTimeTextView.setText(serverSyncTime);
        serverDateTextView.setText(serverSyncDate);

    }


    private void readFileDataforUpdatingNotification(){
        Log.e("KMCAppWidget", "Update");
        File mainfolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + ("KMC").trim());


        File file = new File(mainfolder, "WearableLog.csv");



        if (!file.exists()) {
            wearableSync = "Not Connected";
        }
        else {
            try {
                fs = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                wearableSync = br.readLine().trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File fileServer = new File(mainfolder, "ServerLog.csv");



        if (!fileServer.exists()) {
            serverSync = "Not Connected";
        }
        else {
            try {
                fs_Server = new FileInputStream(fileServer);
                BufferedReader br = new BufferedReader(new InputStreamReader(fs_Server));
                serverSync = br.readLine().trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(CheckstatusActivity.this, ConfigureWearableActivity.class));
        finish();
    }
}
