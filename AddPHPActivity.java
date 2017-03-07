package com.example.chinmay.smartmirror;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Chinmay on 01-03-2017.
 */

public class AddPHPActivity extends AppCompatActivity {


    private TextView tvresult;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.php_result);

        tvresult = (TextView)findViewById(R.id.tvResult);

    }
}
