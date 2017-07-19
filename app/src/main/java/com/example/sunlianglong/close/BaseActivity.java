package com.example.sunlianglong.close;

/**
 * Created by sun liang long on 2017/6/6.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAppalication.getInstance().addActivity(BaseActivity.this);
    }

}