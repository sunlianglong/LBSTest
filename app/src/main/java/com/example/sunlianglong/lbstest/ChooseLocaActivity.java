package com.example.sunlianglong.lbstest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChooseLocaActivity extends Activity {
    private Button button;
    private EditText editText;
    private String choose_location;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_loca);
        editText = (EditText)findViewById(R.id.choose);
        button = (Button)findViewById(R.id.choose_button);
        Intent intent1 = getIntent();
        username = intent1.getStringExtra("username");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_location = editText.getText().toString();
                Intent intent2 = new Intent(ChooseLocaActivity.this,MainActivity.class);
                intent2.putExtra("choose_location",choose_location);
                intent2.putExtra("username",username);
                System.out.println(choose_location+":"+username);
                startActivity(intent2);
            }
        });
    }
}
