package rbccps.kmc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import rbccps.kmc.ui.login.LoginActivity;

public class BootReciever extends BroadcastReceiver {
    public BootReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        // To auto start the UI on BOOT
        Intent startIntent = new Intent(context, LoginActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);


        // To auto start the service on BOOT
        //context.startService(new Intent(context, TimerThreadService.class));
    }
}
