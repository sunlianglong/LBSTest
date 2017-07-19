package com.example.sunlianglong.close;
//付款界面 付款之后更改柜子状态 但是好像并没有从1—>0 我在研究研究php代码
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunlianglong.lbstest.JSONParser;
import com.example.sunlianglong.lbstest.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;

public class PaidActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private Button button_paid;
    private TextView t0;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private static final String TAG="LYGK";
    private  String username;
    private int flag =0;
    private String group_id;
    private String box_id;
    private String order_time;
    private  String current_time;
    private  double cost;//费用
    private  String cost1;
    private  String position;
    private float   hours;
    private int return_sof=0;

    // 此为测试Appid,请将Appid改成你自己的Bmob AppId
    String APPID = "7622e00921d1787dbcf113708390345a";
    // 此为微信支付插件的官方最新版本号,请在更新时留意更新说明
    int PLUGINVERSION = 7;

    EditText order;
    Button go;
    RadioGroup type;
    TextView tv;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.i(TAG, "启动程序 ");
        StartRequestFromPHP();
        //button_paid = (Button)findViewById(R.id.go);
        t0 = (TextView)findViewById(R.id.t0);
        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);
        t3 = (TextView)findViewById(R.id.t3);
        t4 = (TextView)findViewById(R.id.t4);
        t5 = (TextView)findViewById(R.id.t5);
        t6 = (TextView)findViewById(R.id.t6);

        //hours = Time_to_Time();
        // 初始化BmobPay对象,可以在支付时再初始化
        BP.init(APPID);
        order = (EditText) findViewById(R.id.order);
        go = (Button) findViewById(R.id.go);
        type = (RadioGroup) findViewById(R.id.type);
        tv = (TextView) findViewById(R.id.tv);

        type.setOnCheckedChangeListener(this);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.getCheckedRadioButtonId() == R.id.alipay){// 当选择的是支付宝支付时
                    if(flag==1){
                        new AlertDialog.Builder(PaidActivity.this).setTitle("支付开柜").setMessage("您确定要支付吗？点击'支付'，开柜取物")
                                .setPositiveButton("支付", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pay(true);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }else {
                        Toast.makeText(PaidActivity.this,"您当前没有订单，快去开启属于自己的个人柜吧！",Toast.LENGTH_LONG).show();
                    }

                }
                else if (type.getCheckedRadioButtonId() == R.id.wxpay){// 调用插件用微信支付
                    if(flag==1){
                        new AlertDialog.Builder(PaidActivity.this).setTitle("支付开柜").setMessage("您确定要支付吗？点击'支付'，开柜取物")
                                .setPositiveButton("支付", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pay(false);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }else {
                        Toast.makeText(PaidActivity.this,"您当前没有订单，快去开启属于自己的个人柜吧！",Toast.LENGTH_LONG).show();
                    }
                }
               else if (type.getCheckedRadioButtonId() == R.id.query) // 选择查询时
                   query();
            }
        });

//        button_paid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(flag==1){
//                    new AlertDialog.Builder(PaidActivity.this).setTitle("支付开柜").setMessage("您确定要支付吗？点击'支付'，开柜取物")
//                            .setPositiveButton("支付", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Log.i(TAG, "flag:"+flag);
//                                    //上传各种信息，插入表
//                                    List<NameValuePair> paramss = new ArrayList<NameValuePair>();
//                                    //替换键值对，这里的键必须和接口中post传递的键一致
//                                    paramss.add(new BasicNameValuePair("group_id", group_id));
//                                    paramss.add(new BasicNameValuePair("box_id", box_id));
//                                    paramss.add(new BasicNameValuePair("username", username));
//                                    paramss.add(new BasicNameValuePair("order_time", order_time));
//                                    paramss.add(new BasicNameValuePair("pad_time", current_time));
//                                    paramss.add(new BasicNameValuePair("cost", cost+""));
//                                    JSONParser jsonParser = new JSONParser();
//                                    try{
//                                        JSONObject json = jsonParser.makeHttpRequest("http://123.206.17.117/dollar/write_saleinfo.php","POST", paramss);
//                                    }catch(Exception e){
//                                        e.printStackTrace();
//                                    }
//                                    //下面的代码是必须加上的，具体的意义还需要大家去探索吧，这里不是主要讲的
//                                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                                            .detectDiskReads()
//                                            .detectDiskWrites()
//                                            .detectNetwork()   // or .detectAll() for all detectable problems
//                                            .penaltyLog()
//                                            .build());
//                                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                                            .detectLeakedSqlLiteObjects()
//                                            .detectLeakedClosableObjects()
//                                            .penaltyLog()
//                                            .penaltyDeath()
//                                            .build());
//                                    Toast.makeText(PaidActivity.this,"支付成功！您已经没有订单",Toast.LENGTH_LONG).show();
//                                    //刷新界面？
//                                    refresh();
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            }).show();
//                }else {
//                    Toast.makeText(PaidActivity.this,"您当前没有订单，快去开启属于自己的个人柜吧！",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });



    }
    private void StartRequestFromPHP()
    {
        //新建线程
        new Thread(){
            public void run(){
                try {
                    SendRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private  void SendRequest(){
        //通过HttpClient类与WEB服务器交互
        HttpClient httpClient = new DefaultHttpClient();
        //定义与服务器交互的地址
        String ServerUrl = "http://123.206.17.117/dollar/test2.php";
        //设置读取超时，注意CONNECTION_TIMEOUT和SO_TIMEOUT的区别
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        //设置读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        //POST方式
        HttpPost httpRequst = new HttpPost(ServerUrl);
        //准备传输的数据
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        try{
            //发送请求
            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            //得到响应
            HttpResponse response = httpClient.execute(httpRequst);
            //返回值如果为200的话则证明成功的得到了数据
            if(response.getStatusLine().getStatusCode() == 200)
            {
                StringBuilder builder = new StringBuilder();
                //将得到的数据进行解析
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                //readLine()阻塞读取
                for(String s =buffer.readLine(); s!= null; s = buffer.readLine())
                {
                    builder.append(s);
                }
                System.out.println(builder.toString());
                //得到Json对象
//                JSONObject jsonObject   = new JSONObject(builder.toString());
//                ArrayList<String> ss = new ArrayList<String>();
//                //通过得到键值对的方式得到值
//                String name = jsonObject.getString("username");
//                String SResult = jsonObject.getString("SResult");
//                String SUserName = jsonObject.getString("SUserName");
//                int SResultPara = jsonObject.getInt("SResultPara");
//                Log.i(TAG, "读取到数据 ");
//                Log.i(TAG, "name:"+name);
//                  Log.i(TAG, "UserName:"+SUserName);
//                //在线程中判断是否得到成功从服务器得到数据
                JSONArray jsonArray = new JSONArray(builder.toString());
                String [] name = new String[jsonArray.length()];
                for (int i= 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    name[i] = jsonObject.optString("username");
                    group_id = jsonObject.optString("group_id");
                    box_id = jsonObject.optString("box_id");
                    order_time = jsonObject.optString("order_time");
                    position = jsonObject.optString("position");
                }
                Log.i(TAG, "name:"+name[0]);
                Log.i(TAG, "group_id:"+group_id);
                Log.i(TAG, "box_id:"+box_id);
                Log.i(TAG, "order_time:"+order_time);
                Log.i(TAG, "position:"+position);
                final String namee = name[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
                        DateFormat formart= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final String    date    =    formart.format(new java.util.Date());
                        current_time = date;
                        float   between = 0;
                        try {
                            Date begin = formart.parse(order_time);
                            Date   end   =  formart.parse(date);
                            between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒
                            hours=(between/3600);

                        } catch (ParseException e) {
                            Log.e(TAG,e.getMessage());
                        }
                        cost = Sumcost(hours);
                        cost1 = formatDouble(cost);
                        cost = Double.parseDouble(cost1);
                        if(namee.equals(username)){
                            flag=1;
                            t1.setText("用户:"+username+"");
                            t2.setText("下单时间:"+order_time+"");
                            t3.setText("现在时间:"+current_time+"");
                            t4.setText("费用总计:"+cost+"￥");
                            t5.setText("您的柜子编号:"+box_id+"号");
                            t6.setText("地点:"+position);


                        }
                        else{
                            t0.setText("您当前没有订单");
                            t1.setText("用户:"+username+"");
                            t2.setText("下单时间:");
                            t3.setText("现在时间:");
                            t4.setText("费用总计:");
                            t5.setText("您的柜子编号:");
                            t6.setText("地点:");
                        }
                    }
                });
            }
            else{
                Log.e(TAG, "连接超时 ");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "请求错误 ");
            Log.e(TAG, e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    t0.setText("您当前没有订单");
                    t1.setText("用户:"+username+"");
                    t2.setText("下单时间:");
                    t3.setText("现在时间:");
                    t4.setText("费用总计:");
                    t5.setText("您的柜子编号:");
                    t6.setText("地点:");
                }
            });
        }
    }
    //计算总费用
    private double Sumcost(float Sumhours){
        double Scost = 0;
        float hour =Sumhours;
        if(hour>0&&hour<24){
            Scost=hour*0.3;
        }
        else if(hour>24){
            Scost=12+(hour-24)*0.5;
        }
        return Scost;
    }
    //费用保留两位小数
    public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    }
    private void refresh() {
        finish();
        Intent intent = new Intent(PaidActivity.this, PaidActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }





    //paid
    /**
     * 检查某包名应用是否已经安装
     *
     * @param packageName 包名
     * @param browserUrl  如果没有应用市场，去官网下载
     * @return
     */
    private boolean checkPackageInstalled(String packageName, String browserUrl) {
        try {
            // 检查是否有支付宝客户端
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有安装支付宝，跳转到应用市场
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                startActivity(intent);
            } catch (Exception ee) {// 连应用市场都没有，用浏览器去支付宝官网下载
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(browserUrl));
                    startActivity(intent);
                } catch (Exception eee) {
                    Toast.makeText(PaidActivity.this,
                            "您的手机上没有没有应用市场也没有浏览器，我也是醉了，你去想办法安装支付宝/微信吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }
    private static final int REQUESTPERMISSION = 101;

    private void installApk(String s) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
        } else {
            installBmobPayPlugin(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installBmobPayPlugin("bp.db");
                } else {
                    //提示没有权限，安装不了
                    Toast.makeText(PaidActivity.this,"您拒绝了权限，这样无法安装支付插件",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    /**
     * 调用支付
     *
     * @param alipayOrWechatPay 支付类型，true为支付宝支付,false为微信支付
     */

    //                if(flag==1){
//                    new AlertDialog.Builder(PaidActivity.this).setTitle("支付开柜").setMessage("您确定要支付吗？点击'支付'，开柜取物")
//                            .setPositiveButton("支付", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Log.i(TAG, "flag:"+flag);
//                                    //上传各种信息，插入表
//                                    List<NameValuePair> paramss = new ArrayList<NameValuePair>();
//                                    //替换键值对，这里的键必须和接口中post传递的键一致
//                                    paramss.add(new BasicNameValuePair("group_id", group_id));
//                                    paramss.add(new BasicNameValuePair("box_id", box_id));
//                                    paramss.add(new BasicNameValuePair("username", username));
//                                    paramss.add(new BasicNameValuePair("order_time", order_time));
//                                    paramss.add(new BasicNameValuePair("pad_time", current_time));
//                                    paramss.add(new BasicNameValuePair("cost", cost+""));
//                                    JSONParser jsonParser = new JSONParser();
//                                    try{
//                                        JSONObject json = jsonParser.makeHttpRequest("http://123.206.17.117/dollar/write_saleinfo.php","POST", paramss);
//                                    }catch(Exception e){
//                                        e.printStackTrace();
//                                    }
//                                    //下面的代码是必须加上的，具体的意义还需要大家去探索吧，这里不是主要讲的
//                                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                                            .detectDiskReads()
//                                            .detectDiskWrites()
//                                            .detectNetwork()   // or .detectAll() for all detectable problems
//                                            .penaltyLog()
//                                            .build());
//                                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                                            .detectLeakedSqlLiteObjects()
//                                            .detectLeakedClosableObjects()
//                                            .penaltyLog()
//                                            .penaltyDeath()
//                                            .build());
//                                    Toast.makeText(PaidActivity.this,"支付成功！您已经没有订单",Toast.LENGTH_LONG).show();
//                                    //刷新界面？
//                                    refresh();
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            }).show();
//                }else {
//                    Toast.makeText(PaidActivity.this,"您当前没有订单，快去开启属于自己的个人柜吧！",Toast.LENGTH_LONG).show();
//                }
    void pay(final boolean alipayOrWechatPay) {

        if (alipayOrWechatPay) {
            if (!checkPackageInstalled("com.eg.android.AlipayGphone",
                    "https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
                Toast.makeText(PaidActivity.this, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        } else {
            if (checkPackageInstalled("com.tencent.mm", "http://weixin.qq.com")) {// 需要用微信支付时，要安装微信客户端，然后需要插件
                // 有微信客户端，看看有无微信支付插件，170602更新了插件，这里可检查可不检查
                if (!BP.isAppUpToDate(this, "cn.bmob.knowledge", 8)){
                    Toast.makeText(
                            PaidActivity.this,
                            "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)",
                            Toast.LENGTH_SHORT).show();
                    installApk("bp.db");
                    return;
                }
            } else {// 没有安装微信
                Toast.makeText(PaidActivity.this, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        showDialog("正在获取订单...\nSDK版本号:" + BP.getPaySdkVersion());
        final String name = getName();

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.bmob.app.sport",
                    "com.bmob.app.sport.wxapi.BmobActivity");
            intent.setComponent(cn);
            this.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        BP.pay(name, getBody(), getPrice(), alipayOrWechatPay, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(PaidActivity.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                tv.append(name + "，您的支付状态未知\n\n");
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                //上传各种信息，插入表
                List<NameValuePair> paramss = new ArrayList<NameValuePair>();
                //替换键值对，这里的键必须和接口中post传递的键一致
                paramss.add(new BasicNameValuePair("group_id", group_id));
                paramss.add(new BasicNameValuePair("box_id", box_id));
                paramss.add(new BasicNameValuePair("username", username));
                paramss.add(new BasicNameValuePair("order_time", order_time));
                paramss.add(new BasicNameValuePair("pad_time", current_time));
                paramss.add(new BasicNameValuePair("cost", cost+""));
                JSONParser jsonParser = new JSONParser();
                try{
                    JSONObject json = jsonParser.makeHttpRequest("http://123.206.17.117/dollar/write_saleinfo.php","POST", paramss);
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
                Toast.makeText(PaidActivity.this,"支付成功！您已经没有订单",Toast.LENGTH_LONG).show();
                //刷新界面？
                refresh();



                Toast.makeText(PaidActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
                tv.append(name + "，您的支付已成功\n\n");
                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                order.setText(orderId);
                tv.append(name + "，您的订单号为：" + orderId + "\n\n");
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            PaidActivity.this,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
//                    installBmobPayPlugin("bp.db");
                    installApk("bp.db");
                } else {
                    Toast.makeText(PaidActivity.this, "支付中断,请重新支付", Toast.LENGTH_SHORT)
                            .show();
                }
                tv.append(name + "'本次交易失败 \n"
                        + " ,原因是:" + reason + ",请重新支付"+"\n\n");
//                tv.append(name + "'本次交易失败，s pay status is fail, error code is \n"
//                        + code + " ,reason is " + reason + "\n\n");
                hideDialog();
            }
        });
    }
    //    // 执行订单查询
    void query() {
        showDialog("正在查询订单...");
        final String orderId = getOrder();

        BP.query(orderId, new QListener() {

            @Override
            public void succeed(String status) {
                Toast.makeText(PaidActivity.this, "查询成功!该订单状态为 : " + status,
                        Toast.LENGTH_SHORT).show();
                tv.append("支付状态:" + orderId + " 是 " + status + "\n\n");
                hideDialog();
            }

            @Override
            public void fail(int code, String reason) {
                Toast.makeText(PaidActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                tv.append("订单查询失败，"
                        + " ,原因是: \n" + reason + "\n\n");
                hideDialog();
            }
        });
    }
    // 以下仅为控件操作，可以略过
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.alipay:
                // 以下仅为控件操作，可以略过
                //order.setVisibility(View.GONE);
                go.setText("支付宝支付");
                break;
            case R.id.wxpay:
                // 以下仅为控件操作，可以略过
                //order.setVisibility(View.GONE);
                go.setText("微信支付");
                break;
            case R.id.query:
                // 以下仅为控件操作，可以略过
                //order.setVisibility(View.VISIBLE);
                go.setText("订单查询");
                break;
            default:
                break;
        }
    }

    // 默认为0.02
    double getPrice() {
        double price = cost;
        try {
            //price = Double.parseDouble(this.price.getText().toString());
            price=cost;
        } catch (NumberFormatException e) {
        }
        return price;
    }

    // 商品详情(可不填)
    String getName() {
        return "您的个人储物柜是位于"+position+"的"+box_id+"号\n";
    }

    // 商品详情(可不填)
    String getBody() {
        return "个人便利储物柜";
    }

    // 支付订单号(查询时必填)
    String getOrder() {
        return this.order.getText().toString();
    }

    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    /**
     * 安装assets里的apk文件
     *
     * @param fileName
     */
    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
