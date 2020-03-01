package com.dapan.butterknife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.dapan.butterknife.annotation.ViewBind;

public class MainActivity extends AppCompatActivity {

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
