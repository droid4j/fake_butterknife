package com.dapan.butterknife;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dapan.butterknife.annotation.ViewBind;

public class SecondActivity extends AppCompatActivity {

    @ViewBind(R.id.textView1)
    TextView textView1;

    @ViewBind(R.id.textView2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
