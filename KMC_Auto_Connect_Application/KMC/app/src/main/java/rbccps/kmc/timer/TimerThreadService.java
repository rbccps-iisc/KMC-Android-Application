package rbccps.kmc.timer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import rbccps.kmc.R;
import rbccps.kmc.connectivity.bluetooth.BluetoothService;
import rbccps.kmc.connectivity.network.NetworkSyncOperation;
import rbccps.kmc.ui.administration.ConfigureWearableActivity;

@TargetApi(22)
public class TimerThreadService extends Service {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static long currentTimeinMilliSeconds;
    public static long startTimeinMilliSeconds;
    public static boolean firstAttempt = true;

    public static BluetoothAdapter mBLEAdapter;
    public static BluetoothManager bmanager;
    public static BluetoothLeScanner mLeScanner;
    public static Intent startAdminScreen;
    public static BufferedWriter bw = null;

    public static int ServerdataCount = 0;
    public static int FiledataCount = 0;
    public static Context context;
    public static boolean isBLEConnectionDisabled = false;
    public static String root;
    public static File file;
    BufferedReader br;
    FileInputStream fs;
    NetworkSyncOperation networkOperation;
    String networkOperationResponse;
    public static SharedPreferences ServerdataCountpref;
    public static SharedPreferences FiledataCountpref;
    public static SharedPreferences HealthcareWorkerIDpref;
    public static SharedPreferences WearebleAliasIDpref;
    public static SharedPreferences WearebleIDpref;
    public static SharedPreferences kmctotalpref;
    public static SharedPreferences kmcCountpref;
    public static SharedPreferences connectionTypepref;
    public static SharedPreferences serverIPAddresspref;

    public static SharedPreferences.Editor ServerdataCounteditor;
    public static SharedPreferences.Editor FiledataCounteditor;
    public static SharedPreferences.Editor HealthcareWorkerIDeditor;
    public static SharedPreferences.Editor WearebleAliasIDeditor;
    public static SharedPreferences.Editor WearebleIDeditor;
    public static SharedPreferences.Editor kmctotaleditor;
    public static SharedPreferences.Editor kmcCounteditor;
    public static SharedPreferences.Editor connectionTypeeditor;
    public static SharedPreferences.Editor serverIPAddresseditor;

    public static String HealthcareWorkerID;
    public static String WearebleID;
    public static String WearebleAliasID;
    public static String connectionType;
    public static String serverIPAddress;

    public static int minutes = 60;
    public static int millisecond = 1000;

    public static int connectioninterval = 30 * minutes * millisecond;
    public static Calendar cal;

    TelephonyManager tel;
    String networkOperator;
    Boolean error;
    String latitude;
    String longitude;
    String strURLSent;
    GsmCellLocation cellLocation;

    HttpClient httpClient;
    HttpGet httpGet;
    HttpPost httpPost;
    HttpResponse response;
    BufferedReader reader;
    String serverresponse = "";

    static String mcc;  //Mobile Country Code
    StringBuilder builder;
    String json;
    JSONObject obj;
    String token;
    static String mnc;  //mobile network code
    static int cellid; //Cell ID
    static int lac;  //Location Area Code
    String GetOpenCellID_fullresult;

    public static String [] KMCViolation;

    public static int kmc_count = 0;
    public static int kmc_total = 0;

    static String wearableSyncTime="";
    static String serverSyncTime="";
    //static FileInputStream fs = null;
    static FileInputStream fs_Server = null;

    /**
     * @return WearebleID which is the Device ID to which the application is currently connected.
     */
    public static String getWearebleID() {
        WearebleID = WearebleIDpref.getString("WearebleID", "");
        Log.e("WearebleID", "WearebleID is " + WearebleID);
        return WearebleID;
    }


    /**
     * @return WearebleAliasID which is the Device Alias ID to which the application is currently connected.
     */
    public static String getWearebleAliasID() {
        WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID", "");
        Log.e("WearebleAliasID", "WearebleAliasID is " + WearebleAliasID);
        return WearebleAliasID;
    }


    /**
     * @return HealthcareWorkerID which is the ID of the person incharge for the Baby.
     */
    public static String getHealthcareWorkerID() {
        HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID", "");
        Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + HealthcareWorkerID);
        return HealthcareWorkerID;
    }

    public TimerThreadService() {
    }

    /**
     * This method is used to disable Bluetooth.
     */
    private void turnoffBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.disable();
    }

    /**
     * This method is used to enable Bluetooth
     */
    private void turnonBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        // mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.enable();
        Log.e("BLE Service", "Turning Bluetooth On");
    }

    /**
     * @return True if there is a network connection available. False is there is no connection.
     * This helps the auto-sync function to trigger the Data-Sync functionality.
     */
    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        //For 3G check
        boolean is3g = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();

        //For WiFi Check
        boolean isWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();


        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void updateFileSharedPreference() {
        FiledataCount += 1;
        FiledataCountpref = context.getSharedPreferences("FiledataCountSharedPreference", MODE_PRIVATE);
        FiledataCounteditor = FiledataCountpref.edit();
        FiledataCounteditor.putInt("FiledataCount", FiledataCount);
        FiledataCounteditor.commit();
        Log.e("FiledataCount", "FiledataCount is " + FiledataCount);
    }

    public static void updateKMCFlag(int count, int total){

        KMCViolation[count]=""+total;
        kmctotalpref = context.getSharedPreferences("kmctotalSharedPreference", MODE_PRIVATE);
        kmctotaleditor = kmctotalpref.edit();

        int kmctotal = kmctotalpref.getInt("kmc_total", 0);
        kmc_total = kmc_total+total;


        kmctotaleditor.putInt("kmc_total", kmc_total);
        boolean a = kmctotaleditor.commit();
        Log.e("kmctotal", "kmctotal is " + kmc_total);
        kmctotal = kmctotalpref.getInt("kmc_total", 0);
        Log.e("kmctotal", "kmctotal is " + kmctotal);

        kmcCountpref = context.getSharedPreferences("kmcCountSharedPreference", MODE_PRIVATE);
        kmcCounteditor = kmcCountpref.edit();

        int kmcCount = kmcCountpref.getInt("kmc_count", 0);
        kmc_count = kmc_count+count;

        kmcCounteditor.putInt("kmc_count", kmc_count);
        boolean b = kmcCounteditor.commit();
        Log.e("kmcCount", "kmcCount is " + ""+kmc_count);
        kmcCount = kmcCountpref.getInt("kmc_count", 0);
        Log.e("kmcCount", "kmcCount is " + kmcCount);
    }

    public static boolean updateUserSharedPreference(String healthcareWorkerID, String wearableID, String wearableAliasID) {
        HealthcareWorkerIDpref = context.getSharedPreferences("HealthcareWorkerIDSharedPreference", MODE_PRIVATE);
        HealthcareWorkerIDeditor = HealthcareWorkerIDpref.edit();
        HealthcareWorkerIDeditor.putString("HealthcareWorkerID", healthcareWorkerID);
        boolean a = HealthcareWorkerIDeditor.commit();
        Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + healthcareWorkerID);
        HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID", "");
        Log.e("HealthcareWorkerID", "HealthcareWorkerID is " + HealthcareWorkerID);

        WearebleIDpref = context.getSharedPreferences("WearebleIDSharedPreference", MODE_PRIVATE);
        WearebleIDeditor = WearebleIDpref.edit();
        WearebleIDeditor.putString("WearebleID", wearableID);
        boolean b = WearebleIDeditor.commit();
        WearebleID = WearebleIDpref.getString("WearebleID", wearableID);
        Log.e("WearebleID", "WearebleID is " + WearebleID);

        WearebleAliasIDpref = context.getSharedPreferences("WearebleAliasIDSharedPreference", MODE_PRIVATE);
        WearebleAliasIDeditor = WearebleAliasIDpref.edit();
        WearebleAliasIDeditor.putString("WearebleAliasID", wearableAliasID);
        boolean c = WearebleAliasIDeditor.commit();
        WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID", wearableAliasID);
        Log.e("WearebleAliasID", "WearebleAliasID is " + WearebleAliasID);

        // Update Shared preference
        // Alias Name - WearableName - HealthcareworkerID

        return (a && b && c);
    }


    private void readFileDataforUpdatingNotification(){
        Log.e("KMCAppWidget", "Update");
        File mainfolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + ("KMC").trim());


        File file = new File(mainfolder, "WearableLog.csv");

        if (!file.exists()) {
            wearableSyncTime = "Not Connected";
        }
        else {
            try {
                fs = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                wearableSyncTime = br.readLine().trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File fileServer = new File(mainfolder, "ServerLog.csv");


        if (!fileServer.exists()) {
            serverSyncTime = "Not Connected";
        }
        else {
            try {
                fs_Server = new FileInputStream(fileServer);
                BufferedReader br = new BufferedReader(new InputStreamReader(fs_Server));
                serverSyncTime = br.readLine().trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean RqsLocation(int cid, int lac){

        Boolean result = false;

        String urlmmap = "http://www.google.com/glm/mmap";

        try {
            URL url = new URL(urlmmap);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            WriteData(outputStream, cid, lac);

            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {
                latitude = ""+ dataInputStream.readInt();
                longitude = ""+dataInputStream.readInt();

                result = true;

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    private void WriteData(OutputStream out, int cid, int lac)
            throws IOException
    {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }

    public static String getMcc() {
        return mcc;
    }

    public static String getMnc() {
        return mnc;
    }

    public static int getCellid() {
        return cellid;
    }

    public static int getLac() {
        return lac;
    }


    private void NotifyServer(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, TimerThreadService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification n  = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.babylogo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();

        notificationManager.notify(1, n);
    }


    private void NotifyWearable(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, TimerThreadService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification n  = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.babylogo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();

        notificationManager.notify(2, n);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firstAttempt = true;
        Log.e("Service Started", "Service Started");

        PowerManager.WakeLock wl;
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "whatever");
        wl.acquire();
/*

        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        networkOperator  = tel.getNetworkOperator();

        cellLocation = (GsmCellLocation) tel.getCellLocation();
*/

        startTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
        currentTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();

        context = getApplicationContext();
        ServerdataCountpref = context.getSharedPreferences("ServerdataCountSharedPreference", MODE_PRIVATE);
        ServerdataCounteditor = ServerdataCountpref.edit();
        ServerdataCount = ServerdataCountpref.getInt("ServerdataCount", 0);

        FiledataCountpref = context.getSharedPreferences("FiledataCountSharedPreference", MODE_PRIVATE);
        FiledataCounteditor = FiledataCountpref.edit();
        FiledataCount = FiledataCountpref.getInt("FiledataCount", 0);

        HealthcareWorkerIDpref = context.getSharedPreferences("HealthcareWorkerIDSharedPreference", MODE_PRIVATE);
        HealthcareWorkerIDeditor = HealthcareWorkerIDpref.edit();
        HealthcareWorkerID = HealthcareWorkerIDpref.getString("HealthcareWorkerID", "");

        WearebleIDpref = context.getSharedPreferences("WearebleIDSharedPreference", MODE_PRIVATE);
        WearebleIDeditor = WearebleIDpref.edit();
        WearebleID = WearebleIDpref.getString("WearebleID", "");

        WearebleAliasIDpref = context.getSharedPreferences("WearebleAliasIDSharedPreference", MODE_PRIVATE);
        WearebleAliasIDeditor = WearebleAliasIDpref.edit();
        WearebleAliasID = WearebleAliasIDpref.getString("WearebleAliasID", "");

        connectionTypepref = context.getSharedPreferences("connectionTypepref", MODE_PRIVATE);
        connectionTypeeditor = connectionTypepref.edit();
        connectionType = connectionTypepref.getString("connectionTypepref", "");

        serverIPAddresspref = context.getSharedPreferences("serverIPAddresspref", MODE_PRIVATE);
        serverIPAddresseditor = serverIPAddresspref.edit();
        serverIPAddress = serverIPAddresspref.getString("serverIPAddresspref", "");
        Log.e("Details", WearebleID+"-"+WearebleAliasID+","+HealthcareWorkerID +","+serverIPAddress);

        networkOperation = new NetworkSyncOperation();

        KMCViolation = new String[300];

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        currentTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
                        if (firstAttempt) {
                            firstAttempt = false;
                            startService(new Intent(TimerThreadService.this, BluetoothService.class));
                        }

                        if (BluetoothService.livedata) {
                            BluetoothService.livedata = false;
                            BluetoothService.disconnect = true;
                            ConfigureWearableActivity.disconnectButtonPressed = true;
                            stopService(new Intent(TimerThreadService.this, BluetoothService.class));
                            turnoffBLE();
                            TimerThreadService.isBLEConnectionDisabled = true;
                            Thread.sleep(2000);
                            turnonBLE();

                            readFileDataforUpdatingNotification();

                            getWearebleID();

                            NotifyWearable("Wearable " + WearebleID, "" + wearableSyncTime);

                            NotifyServer("Server ", "" + serverSyncTime);

                        }

                        if (currentTimeinMilliSeconds - startTimeinMilliSeconds >= connectioninterval) {
                            Log.e("HIT", "Re-Start");

                            startTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
                            currentTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
                            // Reconnect to the device //
                            BluetoothService.livedata = false;
                            BluetoothService.disconnect = true;
                            ConfigureWearableActivity.disconnectButtonPressed = true;
                            stopService(new Intent(TimerThreadService.this, BluetoothService.class));
                            turnoffBLE();
                            Thread.sleep(2000);
                            turnonBLE();

                            readFileDataforUpdatingNotification();

                            getWearebleID();

                            NotifyWearable("Wearable " +WearebleID, "" + wearableSyncTime);

                            NotifyServer("Server ", "" + serverSyncTime);

                            int simState = tel.getSimState();

                            switch (simState){
/*
                                case TelephonyManager.SIM_STATE_READY:
                                {
                                    mcc = networkOperator.substring(0, 3);
                                    mnc = networkOperator.substring(3);
                                    cellid =  cellLocation.getCid();
                                    lac =  cellLocation.getLac();
                                    break;
                                }
                                */
                                default:{
                                    mcc = "77";
                                    mnc = "77";
                                    cellid =  77;
                                    lac =  77;
                                    break;
                                }

                            }

                            /*mcc = networkOperator.substring(0, 3);
                            mnc = networkOperator.substring(3);
                            cellid =  cellLocation.getCid();
                            lac =  cellLocation.getLac();
*/




                            Thread.sleep(1000);
                            startService(new Intent(TimerThreadService.this, BluetoothService.class));
                            Log.e("GetOpenCellID", mcc + "," + mnc + "," + cellid + "," + lac);
                        }


                        //GetOpenCellID();
                        //RqsLocation(cellid,lac);

                        // GetOpenCellID: 404,45,27730151,34232


                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();


        new Thread() {
            public void run() {
                while (true) {
                    Log.e("File", "Thread Running");

                    //if (isNetworkAvailable() && isBLEConnectionDisabled) {
                    if (isNetworkAvailable()) {
                        ServerdataCount = ServerdataCountpref.getInt("ServerdataCount", 0);
                        FiledataCount = 50000;//FiledataCountpref.getInt("FiledataCount", 0);
                        Log.e("File", FiledataCount + "-" + ServerdataCount);
                        if (FiledataCount - ServerdataCount > 0) {
                            Log.e("File", "Data Need to be sent" + FiledataCount + "-" + ServerdataCount +" - "+connectionType);

                            root = Environment.getExternalStorageDirectory() +
                                    File.separator + "KMC".trim() +
                                    File.separator + (WearebleID).trim() +
                                    File.separator + (WearebleAliasID).trim();
                            file = new File(root, WearebleID+".csv");

                            try {
                                fs = new FileInputStream(file);
                                br = new BufferedReader(new InputStreamReader(fs));
                                for (int i = 0; i <= FiledataCount; i++) {
                                    String data = br.readLine();
                                    if (data != null) {
                                        Log.e("File", "Reading Line-" + i + "Total-" + FiledataCount + "Sent-" + ServerdataCount + "-" + data);
                                        if (i == ServerdataCount) {
                                            Log.e("File", "F-" + FiledataCount + "S-" + ServerdataCount + "-" + data);

                                            /////////////////////////////

                                            try {
                                                httpClient = new DefaultHttpClient();
                                                httpPost = new HttpPost(serverIPAddress);

                                                //creating map object to creat Json object from it
                                                Map<String, String> jsonValues = new HashMap<String, String>();
                                                jsonValues.put("data", data);
                                                JSONObject json = new JSONObject(jsonValues);

                                                Log.e("JSON is ",json.toString());

                                                //setting json object to post request.
                                                AbstractHttpEntity entity = new ByteArrayEntity(json.toString().getBytes("UTF8"));
                                                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                                                httpPost.setEntity(entity);

                                                response = httpClient.execute(httpPost);

                                                String _status = ""+response.getStatusLine().getStatusCode();

                                                if (_status.contains("201")) {
                                                    serverresponse = "success";
                                                    Log.e("HIT","Update ServerSyncTime in File for Widget");
                                                    updateServerSyncTime();
                                                }

                                                Log.e("In Network Loop","HIT");
                                                Log.e("Status is ",_status);

                                                builder = new StringBuilder();
                                                for (String line = null; (line = reader.readLine()) != null; ) {
                                                    builder.append(line).append("\n");
                                                    Log.d("Http Post Response:", line);
                                                    if (line.contains("201")) {
                                                        serverresponse = "success";
                                                        Log.e("HIT","Update ServerSyncTime in File for Widget");
                                                        updateServerSyncTime();
                                                    } else if (line.contains("Invalid")) {
                                                        serverresponse = "invalid";
                                                    } else if (line.contains("timeout")) {
                                                        serverresponse = "timeout";
                                                    } else if (line.contains("out-of-range")) {
                                                        serverresponse = "outofrange";
                                                    } else {
                                                        serverresponse = "notknown";
                                                    }
                                                }
                                            } catch (Exception e) {
                                                System.out.println(e);
                                            }


                                            Log.e("serverresponse", serverresponse);

                                            if (serverresponse.equalsIgnoreCase("success")) {
                                                Log.e("Success Received", "Increment File Line");
                                                if (ServerdataCount < FiledataCount) {
                                                    ServerdataCount += 1;
                                                    serverresponse = "notknown";
                                                }
                                            } else if (serverresponse.equalsIgnoreCase("outofrange")) {
                                                Log.e("Success Received", "Increment File Line");
                                                if (ServerdataCount < FiledataCount) {
                                                    ServerdataCount += 1;
                                                    serverresponse = "notknown";
                                                }
                                            }

                                            ServerdataCounteditor.putInt("ServerdataCount", ServerdataCount);
                                            ServerdataCounteditor.commit();
                                            Log.e("File UDServerdataCount", "" + ServerdataCount);
                                            // Changed // Thread.sleep(0);
                                        }
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //catch (InterruptedException e) {
                            //    e.printStackTrace();
                            //}
                        } else if ((FiledataCount - ServerdataCount == 0)) {
                            Log.e("File", "All data sent" + FiledataCount + "-" + ServerdataCount);
                        }
                    }
                    try {
                        Thread.sleep(1000 * 60 * 1);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void updateServerSyncTime() {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        byte _currentHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
        byte _currentMinutes = (byte) cal.get(Calendar.MINUTE);
        byte _currentSecond = (byte) cal.get(Calendar.SECOND);
        byte _currentDate = (byte) (cal.get(Calendar.DATE));
        byte _currentMonth = (byte) (cal.get(Calendar.MONTH) + 1);
        int _currentYear_logging = cal.get(Calendar.YEAR);
        String _currentTime = _currentYear_logging + "-" + _currentMonth + "-" + _currentDate + "~" + String.format("%02d", _currentHour) + "." + String.format("%02d", _currentMinutes) + "." + String.format("%02d", _currentSecond);
        Log.e("Last ServerSyncTime",_currentTime);

        File mainfolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + ("KMC").trim());

        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }


        try {
            String root = Environment.getExternalStorageDirectory() +
                    File.separator + ("KMC").trim();

            String fileName = "ServerLog" + ".csv";
            fileName = fileName.replaceAll("\\s", "").trim();

            File file = new File(root, fileName);

            if(file.exists()){
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bw = new BufferedWriter(new FileWriter(file, true));//System.out.format("%02d", i);
            System.out.println(_currentTime);
            _currentTime = _currentTime.trim();
            System.out.println(_currentTime);
            bw.write(_currentTime);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File _mainfolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "KMC".trim() +
                File.separator + (WearebleID).trim() +
                File.separator + (WearebleAliasID).trim());

        if (!_mainfolder.exists()) {
            _mainfolder.mkdir();
        }

        try {
            String root = Environment.getExternalStorageDirectory() +
                    File.separator + "KMC".trim() +
                    File.separator + (WearebleID).trim() +
                    File.separator + (WearebleAliasID).trim();

            String fileName = "ServerLog" + ".csv";
            fileName = fileName.replaceAll("\\s", "").trim();

            File file = new File(root, fileName);

            if(file.exists()){
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            bw = new BufferedWriter(new FileWriter(file, true));//System.out.format("%02d", i);
            System.out.println(_currentTime);
            _currentTime = _currentTime.trim();
            System.out.println(_currentTime);
            bw.write(_currentTime);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void groupURLSent(){
        strURLSent =
                "http://www.opencellid.org/cell/get?mcc=" + mcc
                        +"&mnc=" + mnc
                        +"&cellid=" + cellid
                        +"&lac=" + lac
                        +"&fmt=txt";
    }

    public String getLocation(){
        return(latitude + " : " + longitude);
    }

    public void GetOpenCellID() throws Exception {
        groupURLSent();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(strURLSent);
        HttpResponse response = client.execute(request);
        GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
        spliteResult();
    }

    public String getGetOpenCellID_fullresult(){
        return GetOpenCellID_fullresult;
    }

    private void spliteResult() {
        if (GetOpenCellID_fullresult.equalsIgnoreCase("err")) {
            error = true;
        } else {
            error = false;
            String[] tResult = GetOpenCellID_fullresult.split(",");
            latitude = tResult[0];
            longitude = tResult[1];

            Log.e("Lat and Long ",latitude+","+longitude);
        }
    }
}

