package sjri.iisc.ac.in.nicuapplication;

import android.app.KeyguardManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class TimerThreadService extends Service {

    static long currentTimeinMilliSeconds;
    static long startTimeinMilliSeconds;
    static boolean firstAttempt;

    public BluetoothAdapter mBLEAdapter;
    public BluetoothManager bmanager;
    public BluetoothLeScanner mLeScanner;
    public static Intent startAdminScreen;

    public TimerThreadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void turnoffBLE() {
        // TODO Auto-generated method stub
        bmanager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBLEAdapter = bmanager.getAdapter();
        mBLEAdapter.disable();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        firstAttempt = true;
        Log.e("Service Started", "Service Started");
        startTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
        currentTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        currentTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();

                        if (firstAttempt) {
                            firstAttempt = false;
                            startService(new Intent(TimerThreadService.this, BluetoothService.class));
                        /*    Intent startMonitor = new Intent(TimerThreadService.this, NICUHome.class);
                            startMonitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startMonitor);
                        */}
/*

                        if (BluetoothService.livedata) {
                            BluetoothService.livedata = false;
                            BluetoothService.disconnect = true;
                            AdminScreen.disconnectButtonPressed = true;
                            stopService(new Intent(TimerThreadService.this, BluetoothService.class));
                            turnoffBLE();
                        }
*/

                        if (currentTimeinMilliSeconds - startTimeinMilliSeconds >= 1 * 600 * 1000) {
                            Log.e("HIT", "Re-Start");
                            startTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
                            currentTimeinMilliSeconds = Calendar.getInstance().getTimeInMillis();
                                // Reconnect to the device //

                            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                            wakeLock.acquire();

                            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                            keyguardLock.disableKeyguard();

                            BluetoothService.livedata = false;
                            BluetoothService.disconnect = true;
                            AdminScreen.disconnectButtonPressed = true;
                            stopService(new Intent(TimerThreadService.this, BluetoothService.class));
                            turnoffBLE();
                            Thread.sleep(1000);
                            startService(new Intent(TimerThreadService.this, BluetoothService.class));

                        }
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
