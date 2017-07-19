package com.example.sunlianglong.lbstest;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sunlianglong.close.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class RegisterActivity extends BaseActivity {

    //声明接口地址
    private String url = "http://123.206.17.117/dollar/register.php";
    private String a;
    private String b;
    private String c;
    private String username;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et1 = (EditText) findViewById(R.id.username_edit);
        et2 = (EditText) findViewById(R.id.password_edit);
        btn = (Button) findViewById(R.id.signin_button);
        et3 = (EditText) findViewById(R.id.password_edit_sure);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //获取输入框中的内容
                a = et1.getText().toString();
                int len = a.length();
                b = et2.getText().toString();
                c = et3.getText().toString();
                username = a;
                if(len!=11){
                    Toast.makeText(RegisterActivity.this,"您输入的手机号码有误，请重新输入",Toast.LENGTH_SHORT).show();
                }else {
                    if(!b.equals(c)){
                        Toast.makeText(RegisterActivity.this,"您前后两次输入的密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                    }else {
                        //替换键值对，这里的键必须和接口中post传递的键一致
                        params.add(new BasicNameValuePair("name", a));
                        params.add(new BasicNameValuePair("password", b));

                        JSONParser jsonParser = new JSONParser();

                        try{
                            JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
                            Log.v("uploadsucceed", "uploadsucceed");
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(RegisterActivity.this,MenuActivity.class);
                        intent.putExtra("username",username);
                        startActivity(intent);
                    }

                }


            }
        });

        //下面的代码是必须加上的，具体的意义还需要大家去探索吧，这里不是主要讲的

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

    }

}