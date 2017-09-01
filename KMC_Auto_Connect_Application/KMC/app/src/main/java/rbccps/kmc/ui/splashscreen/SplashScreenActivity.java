package rbccps.kmc.ui.splashscreen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rbccps.kmc.R;
import rbccps.kmc.ui.home.HomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful to showcase app logo
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // and Start your app main activity

                Intent i = new Intent(SplashScreenActivity.this,
                        HomeActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, 2500);

    }
}
