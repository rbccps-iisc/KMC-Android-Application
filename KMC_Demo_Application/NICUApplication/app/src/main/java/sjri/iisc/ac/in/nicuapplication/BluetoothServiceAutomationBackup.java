package sjri.iisc.ac.in.nicuapplication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.LeScanCallback;

public class BluetoothServiceAutomationBackup extends Service {
    public BluetoothAdapter mBLEAdapter;
    public BluetoothManager bmanager;
    public BluetoothLeScanner mLeScanner;
    public static boolean connecttosensor = false;
    public static String sensorName;
    public static boolean ay_signbit;
    public static boolean connectedtoGATT = false;
    public static int touchValue;
    public static boolean archivaldatareceived = false;
    public boolean write_success = false;
    public SimpleDateFormat dateFormat;
    public Date date;
    public static String t1Value = "32";
    public static String t2Value = "32";
    public static short angle1_y = 0;
    public static short angle2_y = 0;
    public static int angle_y = 0;
    public static int signbit_y = 0;
    public static short angle1_z = 0;
    public static short angle2_z = 0;
    public static int angle_z = 0;
    public static int signbit_z = 0;
    public static float ay;
    public static float az;
    public static double radian;
    public static double degree_y;
    public static double degree_z;
    public static String received_data;
    public  static boolean hour = false;
    public  static boolean minute = false;
    public  static boolean day = false;
    public  static boolean month = false;
    public  static boolean year = false;
    public  static boolean touch1 = false;
    public  static boolean touch2 = false;
    public static int a1Value;
    public static int a2Value;
    public  static short batValue1;
    public  static short batValue2;
    public  static int battery = 0;
    public  static double vBat;
    public  static boolean HitKMC001;
    public  static boolean setGatt = false;
    public static DecimalFormat df;
    public static boolean archivaldata = false;
    public static boolean livedata = false;
    public static boolean lastarchivaldata = false;
    public static String archival_hour;
    public static String archival_min;
    public static String archival_day;
    public static String archival_month;
    public static String dataSource;
    public static Calendar cal;
    static BluetoothGattService service;
    static UUID uuid_service;
    static UUID uuid_characteristic_archiveddata;
    static BluetoothGattCharacteristic characteristic_archiveddata;
    public  static byte currentSecond;
    public  static byte currentHour;
    public  static byte currentMinutes;
    public  static byte currentDate;
    public  static byte currentMonth;
    public  static byte currentYear;
    public  static int currentYear_logging;
    public String currentTime;
    public static boolean synced = false;
    public String syncTime;
    public  byte[] setValueBytes = new byte[1];
    public static SharedPreferences pref;
    public static SharedPreferences WBatterypref;
    public static SharedPreferences.Editor editor;
    public static String name;
    public static long currenttimeinMillisec;
    public static long previoustimeinMillisec;
    public static long lastwritetimeinMillisec;
    public static boolean disconnectGatt;
    public static boolean disconnect_success = false;
    public static boolean dataCollectionDone = false;
    public static boolean gattConnected;
    public static boolean gatttimeout = false;
    private static Handler scanHandler;
    public static int count = 0;
    String currentDateandMonth;
    public static BluetoothGatt blegatt;
    public static boolean disconnect = false;
    public static BufferedWriter bw = null;
    public static String _data;
    public static boolean bluetoothturnedOn = false;
    public static boolean bluetoothScanning = true;
    public static int i = 0;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        scanHandler = new Handler();
         pref = getApplicationContext().getSharedPreferences("NICUSharedPreference", MODE_PRIVATE);
        WBatterypref = getApplicationContext().getSharedPreferences("NICUSharedPreference_WBattery", MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
        date = new Date();
        setValueBytes[0] = (byte) (1 & 0xFF);
        previoustimeinMillisec = Calendar.getInstance().getTimeInMillis();
        currenttimeinMillisec = Calendar.getInstance().getTimeInMillis();
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        sensorName = AdminScreen.tagName;
        connecttosensor = true;

        // Automation
        sensorName = "KMC020";
        // Automation
        disconnectGatt = false;

        turnonBLE();
        discoverBLEDevices();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        BluetoothServiceAutomationBackup.connecttosensor = false;
        return super.onUnbind(intent);
    }

    private void turnonBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        bluetoothturnedOn = mBLEAdapter.enable();
    }

    public void discoverBLEDevices() {
        // TODO Auto-generated method stub

        // bluetoothScanning = mBLEAdapter.startLeScan(mLeScanCallback);

        mLeScanner = mBLEAdapter.getBluetoothLeScanner();

        if(mLeScanner!= null){
                mLeScanner.startScan(mScanCallback);}
        else{
            Toast.makeText(getApplicationContext(), "BLE Error. Restart Application",
                    Toast.LENGTH_LONG).show();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        Log.e("BLE Service", "Discovering nearby BLE Devices" + bluetoothScanning);
        Toast.makeText(getApplicationContext(), "Scanning for TAGS",
                Toast.LENGTH_LONG).show();
    }

    private Runnable startScan = new Runnable() {
        @Override
        public void run() {
            if(!connectedtoGATT) {
                mBLEAdapter.startLeScan(mLeScanCallback);}
                scanHandler.postDelayed(stopScan, 5000);
        }
    };

    private Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            if(!connectedtoGATT) {
                mBLEAdapter.stopLeScan(mLeScanCallback);}
                scanHandler.postDelayed(startScan, 10);
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mBLEAdapter.stopLeScan(mLeScanCallback);
        Log.e("BLE Service","Scanning Stopped, Bluetooth Turned Off");
        BluetoothServiceAutomationBackup.connecttosensor = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    protected ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            final BluetoothDevice device = result.getDevice();
            name = device.getName();

            System.out.println("Scanning........"+name);

            if(name!=null){
                if(name.contains("KMC")) {
                    if (connecttosensor) {
                        setGatt = true;
                        Log.e("BLEService", "onLeScan, " + device.getName() + " TimeStamp - " + dateFormat.format(date));
                       // if (device.getName().contains("KMC020") || device.getName() == "KMC020") {
                        if (device.getName().contains(sensorName) || device.getName() == sensorName) {
                            Log.e("BLEService", "onLeScan, " + device.getName() + " TimeStamp - " + dateFormat.format(date));
                            device.connectGatt(getApplicationContext(), false, btleGattCallback);
                        }
                    }
                }
            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };



    // Device scan callback.
    @SuppressLint("NewApi")
    protected LeScanCallback mLeScanCallback = new LeScanCallback() {

        //System.out.println("Scanning........");

        @SuppressLint("NewApi")
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {

            name = device.getName();
            System.out.println("Scanning........"+name);

            if(name!=null){
                if(name.contains("KMC")) {
                    if (connecttosensor) {
                        setGatt = true;
                        Log.e("BLEService", "onLeScan, " + device.getName() + " TimeStamp - " + dateFormat.format(date));
                        if (device.getName().contains(sensorName) || device.getName() == sensorName) {
                            Log.e("BLEService", "onLeScan, " + device.getName() + " TimeStamp - " + dateFormat.format(date));

                            // Start Timer Thread

                            device.connectGatt(getApplicationContext(), false, btleGattCallback);
                        }
                    }
                }
            }
        }
    };

    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation

            if(BluetoothServiceAutomationBackup.disconnect && AdminScreen.disconnectButtonPressed) {
                Log.e("Disconnect", "Disconnect");
                connecttosensor = false;
                gattConnected = false;
                disconnectGatt = true;
                BluetoothServiceAutomationBackup.connectedtoGATT = false;
                gatt.close();
                gatt.disconnect();

                mLeScanner.stopScan(mScanCallback);
                mBLEAdapter.stopLeScan(mLeScanCallback);
                mBLEAdapter.disable();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBLEAdapter.enable();

                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLeScanner.startScan(mScanCallback);*/
                }

            gattConnected = true;
            connectedtoGATT = true;
            currenttimeinMillisec = Calendar.getInstance().getTimeInMillis();

            // Write to GATT every 1 min

            if(currenttimeinMillisec-lastwritetimeinMillisec >=1000 * 60 * 1){

                lastwritetimeinMillisec = currenttimeinMillisec;

                count+=1;

                if(count == 5){
                    count = 0;
                    Log.e("HIT","onCharacteristicWrite Hour = 5 min");
                    BluetoothServiceAutomationBackup.gatttimeout = true;
                }

                Log.e("TEST - ","TIMER HIT FOR WRITE");

                cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                currentHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
                // currentHour = (byte) (currentHour % 12);
                currentMinutes = (byte) cal.get(Calendar.MINUTE);
                currentDate = (byte) (cal.get(Calendar.DATE));
                currentMonth = (byte) (cal.get(Calendar.MONTH) + 1);
                currentYear_logging = cal.get(Calendar.YEAR);
                if(currentYear_logging == 2015){
                                currentYear = (byte) 15;
                } else if(currentYear_logging == 2016){
                    currentYear = (byte) 16;}

                Log.e("Current Hour", "-" + currentHour);
                System.out.println("TEST - Hour -" + currentHour + " Minute - " + currentMinutes + " Date - " + currentDate + " Month - " + currentMonth + " Year - " + currentYear_logging);

                setValueBytes[0] = (byte) (currentHour & 0xFF);
                Log.e("Current Hour", "-" + setValueBytes[0]);
                characteristic_archiveddata.setValue(setValueBytes);

                hour = gatt.writeCharacteristic(characteristic_archiveddata);
                Log.e("HIT", "onCharacteristicWrite Hour = " + setValueBytes[0] + " " + hour + " " + connecttosensor);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            byte [] value = characteristic.getValue();
            Log.e("HIT","onCharacteristicChanged");
            StringBuilder sb = new StringBuilder();
            for (byte b : value) {
                sb.append(String.format("%02X ", b));
            }

            received_data = sb.toString();

            //Log.e("Live Data",received_data);

            int length = sb.length();
            String[] myArray = sb.toString().split(" ");

            dataSource = myArray[11];

            if(dataSource.contains("01")){
                archivaldata = true;
                livedata = false;
            } else if(dataSource.contains("00")){
                livedata = true;
                archivaldata = false;
            } else if(dataSource.contains("02")){
                archivaldata = true;
                lastarchivaldata = true;
                livedata = false;
            }

            if(livedata) {

                synced = false;
                NICUHome.writing_archiving = false;

                if(archivaldatareceived) {
                    if (!write_success) {

/*
                        uuid_characteristic_archiveddata = UUID.fromString("f000aa03-0451-4000-b000-000000000000");
                        characteristic_archiveddata = service.getCharacteristic(uuid_characteristic_archiveddata);

                        setValueBytes[0] = (byte) (153 & 0xFF);
                        Log.e("Current Hour", "-" + setValueBytes[0]);
                        characteristic_archiveddata.setValue(setValueBytes);
                        write_success = gatt.writeCharacteristic(characteristic_archiveddata);

                        Log.e("HIT", "onCharacteristicWrite Success = " + setValueBytes[0] + " " + write_success + " " + connecttosensor);
*/

                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Automated Testing

                BluetoothServiceAutomationBackup.dataCollectionDone = true;

                // Automated Testing

                t1Value = Integer.parseInt(myArray[0],16) + "." + Integer.parseInt(myArray[1],16);
                t2Value = Integer.parseInt(myArray[2],16) + "." + Integer.parseInt(myArray[3],16);
                touchValue = Integer.parseInt(myArray[4],16); // 00 01 - Mother, 10 - Baby

                if(touchValue == 0){
                    touch1 = false;
                    touch2 = false;
                    a1Value = 0;
                    a2Value = 0;
                } else if(touchValue == 1){
                    touch1 = false;
                    touch2 = true;
                    a1Value = 0;
                    a2Value = 1;
                } else if(touchValue == 2){
                    touch1 = true;
                    touch2 = false;
                    a1Value = 1;
                    a2Value = 0;
                } else if(touchValue == 3){
                    touch1 = true;
                    touch2 = true;
                    a1Value = 1;
                    a2Value = 1;
                }

                ////////////////////////////////  Calculating ay ////////////////

                angle1_y = Short.parseShort(myArray[7], 16);
                angle2_y = Short.parseShort(myArray[8], 16);
                angle_y = (angle1_y << 8);
                angle_y =  (angle_y | angle2_y);
                signbit_y = (angle_y>>15);

                if(signbit_y == 1){
                    ay_signbit = true;
                    angle_y = (short) Integer.parseInt(Integer.toBinaryString(angle_y), 2);
                    System.out.println(angle_y);
                }

                ay = (float) ((float) angle_y *2.0/32768.0);
                Log.e("ay",""+ay);

                if(HitKMC001){
                    HitKMC001 = false;
                    if(ay>0){
                        ay+=0.2;
                    } else{
                        ay-=0.2;
                    }
                    Log.e("az KMC001",""+ay );
                }

                if(ay > 1 ){
                    ay = 1;
                } else if(ay < -1){
                    ay = -1;
                }
                radian = Math.asin(ay);
                degree_y = Math.toDegrees(radian);


                ////////////////////////////////  Calculating ay ////////////////


                ////////////////////////////////  Calculating az ////////////////

                angle1_z = Short.parseShort(myArray[9], 16);
                angle2_z = Short.parseShort(myArray[10], 16);
                angle_z = (angle1_z << 8);
                angle_z =  (angle_z | angle2_z);
                signbit_z = (angle_z>>15);

                if(signbit_z == 1){
                    degree_y=(degree_y*-1)+180;
                    angle_z = (short) Integer.parseInt(Integer.toBinaryString(angle_z), 2);
                    System.out.println("Z Axis : "+angle_z + " " +degree_y);
                } else if(ay_signbit) {
                    ay_signbit = false;
                    degree_y=(degree_y)+360;
                }


                Log.e("degree_y - Before --",""+degree_y);

                // degree = 0 = 180 and 180 = 0.

                if(degree_y <= 180){
                    degree_y = 180 - degree_y;}
                else if(degree_y > 180 && degree_y <= 270){
                    degree_y = 270 - degree_y + 270;}
                else if(degree_y > 270 && degree_y <= 360){
                    degree_y = 360 - degree_y + 180;}

                Log.e("degree_y - After --",""+degree_y);

                az = (float) ((float) angle_z *4.0/32768.0);
                Log.e("az",""+az);

                if(HitKMC001){
                    HitKMC001 = false;
                    if(az>0){
                        az+=0.2;
                    } else{
                        az-=0.2;
                    }
                    Log.e("az KMC001",""+az );
                }
                if(az > 1 ){
                    az = 1;
                } else if(az < -1){
                    az = -1;
                }
                radian = Math.asin(az);
                degree_z = Double.parseDouble(df.format(Math.toDegrees(radian)));

                ////////////////////////////////  Calculating az ////////////////


                ////////////////////////////////  Calculating battery ////////////////

                batValue1 = Short.parseShort(myArray[12], 16);
                batValue2 = Short.parseShort(myArray[13], 16);

                battery = (batValue1 << 8);

                battery =  (battery | batValue2);

                vBat = 4.3* battery/4096;
                cal = Calendar.getInstance();

                ////////////////////////////////  Calculating battery ////////////////

                currentHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
                currentMinutes = (byte) cal.get(Calendar.MINUTE);
                currentSecond = (byte) cal.get(Calendar.SECOND);
                currentDate = (byte) (cal.get(Calendar.DATE));
                currentMonth = (byte) (cal.get(Calendar.MONTH)+1);
                currentYear_logging = cal.get(Calendar.YEAR);
                if(currentYear_logging == 2015){
                    currentYear = (byte) 15;
                } else if(currentYear_logging == 2016){
                    currentYear = (byte) 16;}


                currentTime = currentYear_logging +"-"+currentMonth+"-"+currentDate+"~"+String.format("%02d",currentHour)+"."+String.format("%02d",currentMinutes)+"."+String.format("%02d",currentSecond);

                Log.e("Live Data : ",""+"length" + " - " +"t1Value" + " - " +"t2Value" + " - " +"touch"+ " - " +"angle_z" + " - " +"radian"+  " - " +"degree_y" +" - " +"degree_z" +" - " +"vBat" +" - "+"Second" +"-"+"Data Source");
                Log.e("Live Data : ",""+length + " - " +t1Value + " - " +t2Value + " - " +touchValue + " - " +angle_z + " - " +radian+  " - " +degree_y + " - " +degree_z +" - " +vBat+" - "+currentSecond+" - "+dataSource);


              writetofile();

            } else if(archivaldata){
                archivaldatareceived = true;
                NICUHome.writing_archiving = true;

                i = i+1;

                if(!synced){
                    synced = true;
                    currentHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
                    currentMinutes = (byte) cal.get(Calendar.MINUTE);
                    currentSecond = (byte) cal.get(Calendar.SECOND);
                    currentDate = (byte) (cal.get(Calendar.DATE));
                    currentMonth = (byte) (cal.get(Calendar.MONTH)+1);
                    currentYear_logging = cal.get(Calendar.YEAR);
                    if(currentYear_logging == 2015){
                        currentYear = (byte) 15;
                    } else if(currentYear_logging == 2016){
                        currentYear = (byte) 16;}

                    syncTime = currentYear_logging +"-"+currentMonth+"-"+currentDate+"~"+String.format("%02d",currentHour)+"."+String.format("%02d",currentMinutes)+"."+String.format("%02d",currentSecond);
                }

                degree_y = 0;
                t1Value = Integer.parseInt(myArray[0],16) + "." + Integer.parseInt(myArray[1],16);
                t2Value = Integer.parseInt(myArray[2],16) + "." + Integer.parseInt(myArray[3],16);
                touchValue = Integer.parseInt(myArray[4],16); // 00 01 - Mother, 10 - Baby


                byte temptouchValue = (byte)(touchValue & 3 );
                Log.e("temptouchValue","temptouchValue - "+temptouchValue);

                if(temptouchValue == 0){
                    touch1 = false;
                    touch2 = false;
                    a1Value = 0;
                    a2Value = 0;
                } else if(temptouchValue == 1){
                    touch1 = false;
                    touch2 = true;
                    a1Value = 0;
                    a2Value = 1;
                } else if(temptouchValue == 2){
                    touch1 = true;
                    touch2 = false;
                    a1Value = 1;
                    a2Value = 0;
                } else if(temptouchValue == 3){
                    touch1 = true;
                    touch2 = true;
                    a1Value = 1;
                    a2Value = 1;
                }

                archival_hour = String.valueOf(Integer.parseInt(myArray[5],16));
                int hour = Integer.parseInt(archival_hour);
                archival_min = String.valueOf(Integer.parseInt(myArray[6],16));
                int min = Integer.parseInt(archival_min);
                archival_day = String.valueOf(Integer.parseInt(myArray[7],16));
                archival_month = String.valueOf(Integer.parseInt(myArray[8],16));

                //yyyy/MM/dd HH:mm:ss:SSS
                currentTime = currentYear_logging+"-"+archival_month+"-"+archival_day+"~"+String.format("%02d", hour)+"."+String.format("%02d", min)+"."+String.format("%02d",00);

                ////////////////////////////////  Calculating az ////////////////

                byte signValue = (byte) (touchValue & 4);
                Log.e("signValue","signValue - "+signValue);

                if(signValue == 4){
                    ay_signbit = true;
                    Log.e("HIT","Sign Check");
                } else{
                    ay_signbit = false;
                }


                ////////////////////////////////  Calculating ay ////////////////

                angle1_y = Short.parseShort(myArray[9], 16);
                angle2_y = Short.parseShort(myArray[10], 16);
                angle_y = (angle1_y << 8);
                angle_y =  (angle_y | angle2_y);
                signbit_y = (angle_y>>15);

                if(signbit_y == 1){
                    ay_signbit = true;
                    angle_y = (short) Integer.parseInt(Integer.toBinaryString(angle_y), 2);
                    System.out.println(angle_y);
                }

                ay = (float) ((float) angle_y *2.0/32768.0);
                Log.e("ay",""+ay);

                if(HitKMC001){
                    HitKMC001 = false;
                    if(ay>0){
                        ay+=0.2;
                    } else{
                        ay-=0.2;
                    }
                    Log.e("az KMC001",""+ay );
                }

                if(ay > 1 ){
                    ay = 1;
                } else if(ay < -1){
                    ay = -1;
                }
                radian = Math.asin(ay);
                degree_y = Math.toDegrees(radian);

                ////////////////////////////////  Calculating ay ////////////////

                byte signbitofz = (byte) ((touchValue & 4) >> 2);

                if(signbitofz == 1){
                    degree_y=(degree_y*-1)+180;
                    Log.e("Archival Z Sign Bit",""+degree_y +" Z Bit"+signbitofz);

                } else if(ay_signbit) {
                    ay_signbit = false;
                    degree_y=(degree_y)+360;
                    Log.e("Archival Y Sign Bit",""+degree_y+" Z Bit"+signbitofz);
                }


                ////////////////////////////////  Calculating az ////////////////


                ////////////////////////////////  Calculating battery ////////////////

                batValue1 = Short.parseShort(myArray[12], 16);
                batValue2 = Short.parseShort(myArray[13], 16);

                battery = (batValue1 << 8);

                battery =  (battery | batValue2);

                vBat = 4.3* battery/4096;

                ////////////////////////////////  Calculating battery ////////////////

                // Sequence Number //

                int sequenceNumber = Integer.parseInt(myArray[15],16);

                Log.e("Archival Data No : ",""+"length" + " - " +"t1Value" + " - " +"t2Value" + " - " +"touch"+ " - " +"signbitofz" +" - " +"H/m/D/M"+ "Data Sign" );
                Log.e("Archival Data No : " +i , "" + length + " - " + t2Value + " - " + t1Value + " - " + touchValue + " - " + signbitofz + " - " + archival_hour + "/" + archival_min + "/" + archival_day + "/" + archival_month + "- Sequence No : " +sequenceNumber +" Data Source - "+dataSource);

                writetofile();

                if(lastarchivaldata){
                    Log.e("Log","Last Archival Data");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    uuid_characteristic_archiveddata = UUID.fromString("f000aa03-0451-4000-b000-000000000000");
                    characteristic_archiveddata = service.getCharacteristic(uuid_characteristic_archiveddata);

                    setValueBytes[0] = (byte) (113 & 0xFF);
                    Log.e("Current Hour", "-" + setValueBytes[0]);
                    characteristic_archiveddata.setValue(setValueBytes);
                    write_success = gatt.writeCharacteristic(characteristic_archiveddata);

                    Log.e("HIT", "onCharacteristicWrite Success = " + setValueBytes[0] + " " + write_success + " " + connecttosensor);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastarchivaldata = false;
                }
            }
        }

        private void writetofile() {

            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            currentHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
            // currentHour = (byte) (currentHour % 12);
            currentMinutes = (byte) cal.get(Calendar.MINUTE);
            currentDate = (byte) (cal.get(Calendar.DATE));
            currentMonth = (byte) (cal.get(Calendar.MONTH) + 1);
            currentYear_logging = cal.get(Calendar.YEAR);
            if(currentYear_logging == 2015){
                currentYear = (byte) 15;
            } else if(currentYear_logging == 2016){
                currentYear = (byte) 16;}

            currentDateandMonth = "Month_"+currentMonth+"-Year_"+currentYear_logging+"/"+currentDate;

            File mainfolder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + ("KMC").trim());

            if(!mainfolder.exists()){
                mainfolder.mkdir();
            }


            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + ("KMC").trim() +
                    File.separator + (BluetoothServiceAutomationBackup.sensorName).trim());

            if(!folder.exists()){
                folder.mkdir();
            }


            File subfolder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + ("KMC").trim() +
                    File.separator + (BluetoothServiceAutomationBackup.sensorName).trim()+
                    File.separator + currentDateandMonth);

            if(!subfolder.exists()){
                subfolder.mkdirs();
            }



            try {
                String root = Environment.getExternalStorageDirectory() +
                        File.separator + ("KMC").trim() +
                        File.separator + (BluetoothServiceAutomationBackup.sensorName).trim()+
                        File.separator + currentDateandMonth;
                // Environment.getExternalStorageDirectory();
                String fileName = (BluetoothServiceAutomationBackup.sensorName).trim()+"_"+currentDate+"_"+currentHour+".csv";
                fileName=fileName.replaceAll("\\s","").trim();

                File file = new File(root, fileName); // Change to Name
                //file.createNewFile();

                bw = new BufferedWriter(new FileWriter(file, true));//System.out.format("%02d", i);
                if(true){
                    _data = sensorName +","+ currentTime + ","+t1Value + ","+t2Value + "," + a1Value + "," + a2Value + "," +String.format("%.02f", BluetoothServiceAutomationBackup.degree_y)+","+70.978699+","+80.99990+","+ "A"+ "," + "A"+ "," +syncTime;
                    System.out.println(_data);
                    _data = _data.trim();
                    System.out.println(_data);
                    bw.write(_data);
                }
                bw.newLine();
                bw.flush();
                bw.close();
                //System.out.println("Data Written : "+fileName+"  :  " + received_data +MainActivity.key+"_"+MainActivity._value+"_"+currentDate+"_"+currentMonth+".csv" );

                // Send Data To SERVER
               //  new SyncOperation().execute("Post");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects

            Log.e("HIT","onConnectionStateChange");
            blegatt = gatt;

            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.e("HIT","onConnectionStateChange STATE CONNECTED");
                gatt.discoverServices();
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.e("HIT","onConnectionStateChange STATE DISCONNECTED");
                AdminScreen.disconnectButtonPressed = false;
                BluetoothServiceAutomationBackup.disconnect = false;
                gatt.close();
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call

            Log.e("HIT","onServicesDiscovered");
            if(connecttosensor) {
                connecttosensor = false;

                uuid_service = UUID.fromString("f000aa00-0451-4000-b000-000000000000");
                service = gatt.getService(uuid_service);
                uuid_characteristic_archiveddata = UUID.fromString("f000aa02-0451-4000-b000-000000000000");
                characteristic_archiveddata = service.getCharacteristic(uuid_characteristic_archiveddata);

                cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                currentHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
                // currentHour = (byte) (currentHour % 12);
                currentMinutes = (byte) cal.get(Calendar.MINUTE);
                currentDate = (byte) (cal.get(Calendar.DATE));
                currentMonth = (byte) (cal.get(Calendar.MONTH) + 1);
                currentYear_logging = cal.get(Calendar.YEAR);
                if(currentYear_logging == 2015){
                    currentYear = (byte) 15;
                } else if(currentYear_logging == 2016){
                    currentYear = (byte) 16;}


                currentDateandMonth = "Month_"+currentMonth+"-Year_"+currentYear+"/"+currentDate;

                Log.e("Current Hour", "-" + currentHour);
                System.out.println("Hour -" + currentHour + " Minute - " + currentMinutes + " Date - " + currentDate + " Month - " + currentMonth + " Year - " + currentYear);

                setValueBytes[0] = (byte) (currentHour & 0xFF);
                Log.e("Current Hour", "-" + setValueBytes[0]);
                characteristic_archiveddata.setValue(setValueBytes);

                hour = gatt.writeCharacteristic(characteristic_archiveddata);
                Log.e("HIT", "onCharacteristicWrite Hour = " + setValueBytes[0] + " " + hour + " " + connecttosensor);
                try {
                    Thread.sleep(50);
                    //Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            lastwritetimeinMillisec= Calendar.getInstance().getTimeInMillis();
            if(hour){
                hour=false;
                setValueBytes[0] = (byte) (currentMinutes & 0xFF);
                characteristic_archiveddata.setValue(setValueBytes);
                minute = gatt.writeCharacteristic(characteristic_archiveddata);
                Log.e("HIT","onCharacteristicWrite hour Min = "+setValueBytes[0]+" "+minute);
                try {
                    Thread.sleep(50);
                    //Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if(minute){
                minute=false;
                setValueBytes[0] = (byte) (currentDate & 0xFF);
                characteristic_archiveddata.setValue(setValueBytes);
                day = gatt.writeCharacteristic(characteristic_archiveddata);
                Log.e("HIT","onCharacteristicWrite minute day = " +setValueBytes[0]+" "+day);
                try {
                    Thread.sleep(1000);
                    //Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(day){
                day = false;
                setValueBytes[0] = (byte) (currentMonth & 0xFF);
                characteristic_archiveddata.setValue(setValueBytes);
                month = gatt.writeCharacteristic(characteristic_archiveddata);
                Log.e("HIT","onCharacteristicWrite day Mon = "+setValueBytes[0]+" "+month);
                try {
                    Thread.sleep(1000);
                    //Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(month){
                month = false;


                if(currentYear_logging == 2015){
                setValueBytes[0] = (byte) (15 & 0xFF);}
                else if(currentYear_logging == 2016){
                    setValueBytes[0] = (byte) (16 & 0xFF);}

                characteristic_archiveddata.setValue(setValueBytes);
                year = gatt.writeCharacteristic(characteristic_archiveddata);
                Log.e("HIT", "onCharacteristicWrite month Yea = " + setValueBytes[0] + " " + year);

                /*
                try {
                    Thread.sleep(1000);
                    //Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */

            } else if(year){
                year = false;
                UUID uuid_characteristic_livedata = UUID.fromString("f000aa01-0451-4000-b000-000000000000");
                BluetoothGattCharacteristic characteristic_livedata = service.getCharacteristic(uuid_characteristic_livedata);
                boolean read = gatt.readCharacteristic(characteristic_livedata);
                Log.e("HIT","onCharacteristicWrite year read"+" "+read);
            }
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

/*

            // Requesting HIGH PRIORITY
            gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
*/
            Log.e("HIT","onCharacteristicRead");
            UUID uuid_characteristics = UUID.fromString("f000aa01-0451-4000-b000-000000000000");
            UUID uuid_characteristic = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); //00002902-0000-1000-8000-00805f9b34fb
            UUID uuid_service = UUID.fromString("f000aa00-0451-4000-b000-000000000000");
            BluetoothGattCharacteristic characteristics = gatt.getService(uuid_service).getCharacteristic(uuid_characteristics);
            gatt.setCharacteristicNotification(characteristics, true);
            BluetoothGattDescriptor descriptor_ = characteristic.getDescriptor(uuid_characteristic);
            descriptor_.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[] { 0x00, 0x00 });
            gatt.writeDescriptor(descriptor_);

            byte[] value = characteristic.getValue();
            System.out.println("Value is = " +value.toString());

            if(value!=null){
                StringBuilder sb = new StringBuilder();
                for (byte b : value) {
                    sb.append(String.format("%02X ", b));
                }}
            super.onCharacteristicRead(gatt, characteristic, status);
        }
    };


    protected class SyncOperation  extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try
            {
                HttpClient httpClient=new DefaultHttpClient();
                HttpGet httpGet=new HttpGet(
                        "http://KMCTest.sjri.res.in/RestAuthorizeClientSession.svc/GenTkn/"+sensorName.trim());

                HttpResponse response = httpClient.execute(httpGet);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                String json = reader.readLine();
                final JSONObject obj = new JSONObject(json);
                final String token = obj.getString("GenerateTokenResult");
                System.out.println(token);
                Log.d("Http Post Response:", json);
                // write response to log
                Thread.sleep(1000);
                httpGet=new HttpGet(
                        "http://KMCTest.sjri.res.in/RestAuthorizeClientSession.svc/ImportData/"+token+"/"+sensorName+"/"+_data);

                System.out.println("http://KMCTest.sjri.res.in/RestAuthorizeClientSession.svc/ImportData/"+token+"/"+sensorName+"/"+_data);

                response = httpClient.execute(httpGet);
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                    Log.d("Http Post Response:", line);
                }
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            return null;
        }
    }
}