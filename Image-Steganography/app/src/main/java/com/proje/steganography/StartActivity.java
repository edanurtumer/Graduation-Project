package com.proje.steganography;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ayush.steganography.R;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn = (Button) findViewById((R.id.start_reg_btn));
        mLogBtn = (Button) findViewById((R.id.start_log_btn));

        mRegBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });

        mLogBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent log_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(log_intent);
            }
        });

    }
}
