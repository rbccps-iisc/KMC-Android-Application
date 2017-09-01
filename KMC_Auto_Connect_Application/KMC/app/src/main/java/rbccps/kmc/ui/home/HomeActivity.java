package rbccps.kmc.ui.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import rbccps.kmc.R;
import rbccps.kmc.ui.administration.ConfigureWearableActivity;

public class HomeActivity extends AppCompatActivity {

    Button adminButton;
    Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adminButton = (Button) findViewById(R.id.startadminscreenbutton);
        closeButton = (Button) findViewById(R.id.checkbutton);

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Administrator Activity
                Intent i = new Intent(HomeActivity.this,
                        ConfigureWearableActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the Application
                finish();
            }
        });

    }
}
