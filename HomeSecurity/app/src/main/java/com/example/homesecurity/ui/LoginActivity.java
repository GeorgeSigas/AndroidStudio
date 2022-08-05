package com.example.homesecurity.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homesecurity.R;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView header = findViewById(R.id.loginhdr);

        username = findViewById(R.id.username);

        password = findViewById(R.id.password);

        Button login = findViewById(R.id.loginbtn);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /**check credentials
                 * commented-out for examiners
                 * Login with any username / password
                 * Remove from comments to activate credentials checking
                 * change "correct" values from static finals at lines 17,18**/
                /*
                if ( username.getText().toString().equals(USERNAME) && password.getText().toString().equals(PASSWORD) )
                {
                    //go to main activity
                    Intent app = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(app);
                }
                else
                {
                    username.setText(null);
                    password.setText(null);
                    Toast.makeText(LoginActivity.this, "Wrong username or password.\n Please Try Again.", Toast.LENGTH_SHORT).show();
                }
                */

                //go to main activity
                Intent app = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(app);

            }
        });
    }
}
