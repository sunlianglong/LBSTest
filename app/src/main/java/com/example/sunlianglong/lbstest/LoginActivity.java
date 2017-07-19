package com.example.sunlianglong.lbstest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunlianglong.close.BaseActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
public class LoginActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText editText_user,editText_passwd;
    private Button button_login;
    private SharedPreferences sharedPreferences;
    private int retCode;
    private String user,passwd,result;
    private TextView register;
    private int FLAG = 1 ;
    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            Log.e(TAG, s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                retCode = jsonObject.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (retCode == 1) {
                Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                intent.putExtra("username",user);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this,"用户名或密码错误!",Toast.LENGTH_SHORT).show();
            }
        }
    };
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e(TAG, volleyError.getMessage(), volleyError);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button_login = (Button) findViewById(R.id.signin_button);
        editText_user = (EditText)findViewById(R.id.username_edit);
        editText_passwd = (EditText)findViewById(R.id.password_edit);
        register = (TextView)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FLAG = 0;
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        if(FLAG == 1){
            //显示已保存的用户名和密码
            sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
            String getName = sharedPreferences.getString("username", null);
            if (getName == null) {
                //Toast.makeText(LoginActivity.this,"没有保存用户信息，请重新输入!",Toast.LENGTH_SHORT).show();
            } else {
                editText_user.setText(sharedPreferences.getString("userName",null));
                editText_passwd.setText(sharedPreferences.getString("passWord",null));
            }
            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = editText_user.getText().toString();
                    passwd = editText_passwd.getText().toString();
                    if (user.equals("") || passwd.equals("")) {
                        Toast.makeText(LoginActivity.this,"用户名或密码不能为空，请重新输入!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,"http://123.206.17.117/dollar/login.php",listener,errorListener) {
                        @Override
                        protected Map<String,String> getParams() throws AuthFailureError {
                            Map<String,String> map = new HashMap<String, String>();
                            map.put("username",user);
                            map.put("password",passwd);
                            return map;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            });
        }
    }
}

