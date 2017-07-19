package com.example.sunlianglong.lbstest;
/*
* 40个柜子的状态以及下单决定：丑到不能再丑
* 下单包括：确定是否用户并没有没付款的柜子 有的话 不能下单 否则的话，下单更新数据库
*/
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sunlianglong.close.VisitPHP;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUIActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button1;private Button button2;private Button button3;private Button button4;private Button button5;private Button button6;private Button button7;private Button button8;
    private Button button9;private Button button10;private Button button11;private Button button12;private Button button13;private Button button14;private Button button15;private Button button16;
    private Button button17;private Button button18;private Button button19;private Button button20;private Button button21;private Button button22;private Button button23;private Button button24;
    private Button button25;private Button button26;private Button button27;private Button button28;private Button button29;private Button button30;private Button button31;private Button button32;
    private Button button33;private Button button34;private Button button35;private Button button36;private Button button37;private Button button38;private Button button39;private Button button40;
    int selectColor1;
    int selectColor2;
    private TextView editText;
    private Resources resources;
    private String URL3 = "http://123.206.17.117/dollar/order_info.php";
    static OkHttpClient client = new OkHttpClient();
    private String result2 = null,result3=null;
    double [] d_id = new double[40];
    private String [] s_id = new String[40];
    private String [] s_size = new String[40];
    private String [] s_group = new String[40];
    private String [] s_status = new String[40];
    private String address;//具体地址
    private String group_id;//柜子组号
    private String jsonString;
    private String username;
    private String location_id;
    private String position_search;
    private int sure_status=0;
    //标志位，1表示用户在使用柜子，0表示未使用
    public int flagId=0;
    ArrayList<String> flag_arraylist = new ArrayList<String>();
    Integer[] id= new Integer[]{
            R.id.button1,R.id.button2,R.id.button3, R.id.button4,R.id.button5,
            R.id.button6,R.id.button7, R.id.button8,R.id.button9,R.id.button10,
            R.id.button11,R.id.button12, R.id.button13,R.id.button14,R.id.button15,
            R.id.button16, R.id.button17,R.id.button18,R.id.button19,R.id.button20,
            R.id.button21,R.id.button22, R.id.button23, R.id.button24,R.id.button25,
            R.id.button26,R.id.button27,R.id.button28,R.id.button29, R.id.button30,
            R.id.button31,R.id.button32,R.id.button33,R.id.button34,R.id.button35,
            R.id.button36,R.id.button37,R.id.button38,R.id.button39,R.id.button40};
    Button[] buttons=new Button[]{
            button1, button2, button3, button4, button5,button6, button7, button8,
            button9,button10,button11, button12, button13, button14, button15,button16,
            button17, button18, button19,button20,button21, button22, button23, button24,
            button25,button26, button27, button28, button29,button30,button31, button32,
            button33, button34, button35,button36, button37, button38, button39,button40};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        editText = (TextView)findViewById(R.id.dingwei);
        //验证username是否已经在使用柜子
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        location_id = intent.getStringExtra("location_id");
        position_search = intent.getStringExtra("position_search");

        //判断用户是否在order_info中，是的话 不能操作下单
        final JsonParser myJsonP2 =new JsonParser();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //result3=Get2();
                    //将JSON的String 转成一个JsonArray对象
                    VisitPHP visitPHP = new VisitPHP();
                    jsonString = visitPHP.SendRequest("http://123.206.17.117/dollar/user_arraylist.php","position_search",position_search);
                    System.out.println(jsonString);
                    JsonArray jsonArray2 = myJsonP2.parse(jsonString).getAsJsonArray();
                    Gson gson2 = new Gson();
                    for (JsonElement user2 : jsonArray2) {
                        Username username2 = gson2.fromJson(user2,Username.class);
                        flag_arraylist.add(username2.toStringName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //button数组
        for(int x=0;x<40;x++){
            buttons[x]=(Button)findViewById(id[x]);
            buttons[x].setOnClickListener(this);
        }
        //附颜色
        resources = getBaseContext().getResources();  //核心代码
        selectColor1 = resources.getColor(R.color.colorAccent);
        selectColor2 = resources.getColor(R.color.guan);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(position_search);
            }
        });




        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //String ip_adds,  String up_name,  String up_name_key
                    VisitPHP visitPHP = new VisitPHP();
                    jsonString = visitPHP.SendRequest("http://123.206.17.117/dollar/box_info.php","position_search",position_search);
                    //在线程中判断是否得到成功从服务器得到数据
                    System.out.print(position_search);
                    Log.i("jsonString",jsonString);
                        JSONArray jsonArray = new JSONArray(jsonString);
                        System.out.println(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            s_id[i] = jsonObject.optString("box_id");
                            s_size[i] = jsonObject.optString("box_size");
                            s_group[i] = jsonObject.optString("group_id");
                            s_status[i] = jsonObject.optString("box_status");
                            d_id[i] = Double.parseDouble(s_id[i]);
                            if(i==0){
                                group_id=s_group[0];
                            }
                        }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int j=0;j<40;j++){
                                if(s_status[j].equals("1")){
                                    buttons[j].setTextColor(selectColor1);
                                }
                                if(s_status[j].equals("0")){
                                    buttons[j].setTextColor(selectColor2);
                                }
                            }
                            //Toast.makeText(GUIActivity.this,position_search+" "+username,Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    public void onClick(View v){
        for(int i=0;i<40;i++){
            if (v.getId() == buttons[i].getId()) {   //这里获取的id就不会错啦。
                if(s_status[i].equals("1")){
                    Toast.makeText(this, i+1+"号柜子正在被使用，请选择其他绿色柜子，谢谢", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(this, i+1+"号柜子可以使用", Toast.LENGTH_SHORT).show();
                    final int finalI = i;
                    final int num=i+1;
                    new AlertDialog.Builder(GUIActivity.this).setTitle("下单开柜").setMessage("点击'开启'下单，打开"+num+"号个人柜")
                            .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for(int i = 0;i < flag_arraylist.size(); i++){
                                        if (flag_arraylist.get(i).equals(username)){
                                            flagId=1;
                                        }
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                //String ip_adds,  String up_name,  String up_name_key
                                                VisitPHP visitPHP_sure = new VisitPHP();
                                                jsonString = visitPHP_sure.SendRequest("http://123.206.17.117/dollar/box_info.php","position_search",position_search);
                                                //在线程中判断是否得到成功从服务器得到数据
                                                System.out.print(position_search);
                                                Log.i("jsonString",jsonString);
                                                JSONArray jsonArray_sure = null;
                                                jsonArray_sure = new JSONArray(jsonString);
                                                System.out.println(jsonString);
                                                for (int i = 0; i < jsonArray_sure.length(); i++) {
                                                    JSONObject jsonObject = jsonArray_sure.optJSONObject(i);
                                                    //只访问status
                                                    s_status[i] = jsonObject.optString("box_status");
                                                }
                                                if(s_status[num]=="1"){
                                                    sure_status=1;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).start();


                                    //判断1：是否在用其他箱子 判断2：是否被抢占
                                    if(flagId==0&&sure_status==0){
                                        //上传box_id查询是否可用
                                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                                        //替换键值对，这里的键必须和接口中post传递的键一致
                                        params.add(new BasicNameValuePair("group_id", group_id));
                                        params.add(new BasicNameValuePair("box_id", s_id[finalI]));
                                        params.add(new BasicNameValuePair("username", username));
                                        JSONParser jsonParser = new JSONParser();
                                        try{
                                               JSONObject json = jsonParser.makeHttpRequest(URL3,"POST", params);

                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
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
                                        Toast.makeText(GUIActivity.this,"下单成功，快去开启属于你自己的柜子吧",Toast.LENGTH_LONG).show();
                                        //刷新界面：
                                        refresh();

                                    }else if(flagId==1&&sure_status==0){
                                        Toast.makeText(GUIActivity.this,"您还有未结账的柜子，请结账后再下单",Toast.LENGTH_LONG).show();
                                    }else if(sure_status==1){
                                        Toast.makeText(GUIActivity.this,"这个柜子已被其他用户使用，请选择其他绿色的柜子，谢谢",Toast.LENGTH_LONG).show();
                                        //刷新界面：
                                        refresh();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        }
    }
    public String Get()throws IOException {
        Request request = new Request.Builder().url("http://123.206.17.117/dollar/box_info"+location_id+".php").get().build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            //Return string part.Do not use toString() to get return value
            return response.body().string();
        } else {
            return "Wrong";
        }
    }
    public String Get2()throws IOException {
        Request request1 = new Request.Builder().url("http://123.206.17.117/dollar/user_arraylist.php").get().build();
        Response response1 = client.newCall(request1).execute();
        if (response1.isSuccessful()) {
            //Return string part.Do not use toString() to get return value
            return response1.body().string();
        } else {
            return "Wrong";
        }
    }
    private void refresh() {
        finish();
        Intent intent = new Intent(GUIActivity.this, GUIActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("location_id",location_id);
        intent.putExtra("position_search",position_search);
        startActivity(intent);
    }
}
