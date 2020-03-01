package com.dapan.butterknife.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.dapan.butterknife.ButterKnife;
import com.dapan.butterknife.UnBinder;
import com.dapan.butterknife.annotation.ViewBind;

public class MainActivity extends AppCompatActivity {

    @ViewBind(R.id.textView1)
    TextView textView1;

    @ViewBind(R.id.textView2)
    TextView textView2;

    private UnBinder unBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unBinder = ButterKnife.bind(this);
        textView1.setText("inject 1");
        textView2.setText("inject 2");
    }

    @Override
    protected void onDestroy() {
        unBinder.unbind();
        super.onDestroy();
    }
}
