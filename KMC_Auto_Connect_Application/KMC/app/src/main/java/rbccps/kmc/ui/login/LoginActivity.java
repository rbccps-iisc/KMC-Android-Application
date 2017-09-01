package rbccps.kmc.ui.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rbccps.kmc.R;

import rbccps.kmc.ui.administration.ConfigureWearableActivity;

public class LoginActivity extends AppCompatActivity {

    TextView passwordTextView;
    Button loginButton;
    Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordTextView = (TextView) findViewById(R.id.passwordtextview);
        loginButton = (Button) findViewById(R.id.loginButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = passwordTextView.getText().toString().trim();
                Log.e("Password", password);

                if (password.contains("1234")) {
                    Log.e("Password Login", "Success");
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent adminActivity = new Intent(LoginActivity.this, ConfigureWearableActivity.class);
                    startActivity(adminActivity);
                    finish();

                } else {
                    Log.e("Password Login", "Failure");
                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Login", "Exit Button Pressed");
                Toast.makeText(LoginActivity.this, "Closing KMC Application", Toast.LENGTH_SHORT).show();
                System.exit(0);
                finish();
            }
        });
    }
}
