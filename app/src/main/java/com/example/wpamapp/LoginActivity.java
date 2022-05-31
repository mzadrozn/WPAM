package com.example.wpamapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wpamapp.notimportant.MainActivity;

public class LoginActivity extends AppCompatActivity {

    EditText eEmail;
    EditText ePassword;
    Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eEmail = findViewById(R.id.editTextEmail);
        ePassword = findViewById(R.id.editTextPassword);
        bLogin = findViewById(R.id.buttonLogin);

        setTitle("");

        Intent i = new Intent(this, MainActivity.class);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputEmail = eEmail.getText().toString();
                String inputPassword = ePassword.getText().toString();

                startActivity(i);
            }
        });
    }
}