package com.example.sunlianglong.lbstest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.sunlianglong.close.BaseActivity;
import com.example.sunlianglong.close.MyAppalication;
import com.example.sunlianglong.close.PaidActivity;
import com.example.sunlianglong.close.SettingsActivity;
import com.example.sunlianglong.close.VisitPHP;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
/*
展示的是地图：当前的位置坐标以及默认的重庆邮电大学大的柜子组。
想法是：可以显示自己的位置周边假如100m圆圈范围内的柜子组坐标 不采取默认的重邮周围的那种
*/
public class MenuActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private long exitTime=0;
    public LocationClient mLocationClient;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private Marker marker[];
    BitmapDescriptor bitmapDescriptor;
    BitmapDescriptor bdGround;

    //Gson解析经纬度
    private String URL = "http://123.206.17.117/dollar/location_info.php";
    static OkHttpClient client = new OkHttpClient();
    String result = null;
    private String username;

    private String position_search="邮电大学";
    private int num;
    //存储json解析下来的柜子信息
    double [][] d = new double[100][2];
    String [][] s = new String[100][2];
    String [] addr =new String[100];
    private String jsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MenuActivity.MyLocationListener());
        //隐藏textview 注意xml中的         android:visibility="gone"
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_menu);
        //获取用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("个人储物柜");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mapView = (MapView) findViewById(R.id.bmapView);//地图

        //获取baiduMap实例
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        positionText = (TextView) findViewById(R.id.position_text_view);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //String ip_adds,  String up_name,  String up_name_key
                    VisitPHP visitPHP = new VisitPHP();
                    jsonString = visitPHP.SendRequest("http://123.206.17.117/dollar/location_by_key.php","position_search",position_search);
                    //在线程中判断是否得到成功从服务器得到数据
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (num=0; num < jsonArray.length(); num++);
                    Log.i("num",num+"");
                    System.out.println(jsonString);
                    for (int i= 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        s[i][0] = jsonObject.optString("lat");
                        s[i][1] = jsonObject.optString("lng");
                        addr[i] = jsonObject.optString("position");
                        d[i][0] = Double.parseDouble(s[i][0]);
                        d[i][1] = Double.parseDouble(s[i][1]);
                        Log.i("lat+lng",s[2][0]+s[2][1]);
                        Log.i("choose_location",position_search);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LocationOfPhoto locations[] = new LocationOfPhoto[num];
                            for(int i=0;i<num;i++) {
                                locations[i] = new LocationOfPhoto(d[i][0], d[i][1]);
                            }
                            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pin);
                            bdGround = BitmapDescriptorFactory.fromResource(R.drawable.pin);
                            initOverlay(locations);
                            baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker m) {
                                    for(int index = 0; index < locations.length; index++){
                                        if(m == marker[index]){
                                            //  Toast.makeText(getApplicationContext(), locations[index].getLocation()+"\n", Toast.LENGTH_LONG).show();
                                            final int finalIndex = index+1;
                                            new AlertDialog.Builder(MenuActivity.this).setTitle("位置:"+addr[index]).setMessage("点击查看进入在线个人柜")
                                                    .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(MenuActivity.this,GUIActivity.class);
                                                            intent.putExtra("username",username);
                                                            String locatoin_idd = finalIndex+"";
                                                            intent.putExtra("location_id", locatoin_idd);
                                                            intent.putExtra("position_search",addr[finalIndex-1]);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        requestLocation();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Open) {
            Intent intent = new Intent(MenuActivity.this, ChooseLocaActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
        } else if (id == R.id.Close) {
            Intent intent2 = new Intent(MenuActivity.this, PaidActivity.class);
            intent2.putExtra("username",username);
            startActivity(intent2);
        } else if (id == R.id.Settings) {
            Intent intent3 = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent3);
        } else if (id == R.id.About) {
            //Intent intent4 = new Intent(MenuActivity.this,Person.class);
            //startActivity(intent4);

        } else if (id == R.id.Share) {

        } else if (id == R.id.Update) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // return true;//返回真表示返回键被屏蔽掉
            creatDialog();// 创建弹出的Dialog
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 弹出提示退出对话框
     */
    private void creatDialog() {
        new AlertDialog.Builder(this)
                .setMessage("确定要退出您的在线储物柜?")
                .setPositiveButton("退出",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                MyAppalication.getInstance().exit();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    public void initOverlay(LocationOfPhoto locations[]){
        int count = locations.length;
        LatLng latLngs;
        LatLngBounds bounds = null;
        double min_latitude = 0, min_longitude = 0,
                max_latitude = 0, max_longitude = 0;
        for(int i = 0; i < count-1; i++){
            if(locations[i].getLocation().latitude <= locations[i+1].getLocation().latitude){
                min_latitude = locations[i].getLocation().latitude;
                max_latitude = locations[i+1].getLocation().latitude;
            }
            else {
                min_latitude = locations[i+1].getLocation().latitude;
                max_latitude = locations[i].getLocation().latitude;
            }
            if(locations[i].getLocation().longitude <= locations[i+1].getLocation().longitude){
                min_longitude = locations[i].getLocation().longitude;
                max_longitude = locations[i+1].getLocation().longitude;
            }
            else {
                min_longitude = locations[i+1].getLocation().longitude;
                max_longitude = locations[i].getLocation().longitude;
            }
        }
        marker = new Marker[count];
        for(int i = 0; i < count; i++){
            latLngs = locations[i].getLocation();
            OverlayOptions overlayOptions_marker = new MarkerOptions().position(latLngs).icon(bitmapDescriptor);
            marker[i] = (Marker)(baiduMap.addOverlay(overlayOptions_marker));
        }
        LatLng southwest = new LatLng(min_latitude, min_longitude);
        LatLng northeast = new LatLng(max_latitude, max_longitude);
        LatLng northwest = new LatLng(max_latitude, min_longitude);
        LatLng southeast = new LatLng(min_latitude, max_longitude);
        bounds = new LatLngBounds.Builder().include(northeast).include(southwest).include(southeast).include(northwest).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
        baiduMap.animateMapStatus(mapStatusUpdate,1000);
        MapStatusUpdate mapStatusUpdate_zoom = MapStatusUpdateFactory.zoomTo(5);
        baiduMap.setMapStatus(mapStatusUpdate_zoom);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {//isFirstLocate:防止多次调用
            //Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);//缩放级别
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
            //移动地图到指定位置
//            LatLng lat = new LatLng(39.915,116.404);
//            MapStatusUpdate update2 = MapStatusUpdateFactory.newLatLng(lat);
//            baiduMap.animateMapStatus(update2);

        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
        //封装某个位置：
//        MyLocationData.Builder locationBuilder1 = new MyLocationData.Builder();
//        locationBuilder1.latitude(29.6314);
//        locationBuilder1.longitude(106.6086);
//        MyLocationData locationData1 = locationBuilder.build();
//        baiduMap.setMyLocationData(locationData1);


    }

    //实现5秒更新一次经纬度
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        //设置详细地点
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        //强化只是用GPS定位的代码：
        // option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();//最后销毁应用时，一定要调用stop方法，要不在后台会一直定位。
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);

    }

// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
    //mCurrentMarker = BitmapDescriptorFactory
    //        .fromResource(R.drawable.icon_geo);
    // MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
//mBaiduMap.setMyLocationConfiguration();

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
            currentPosition.append("国家：").append(location.getCountry()).append("\n");
            currentPosition.append("省：").append(location.getProvince()).append("\n");
            currentPosition.append("市：").append(location.getCity()).append("\n");
            currentPosition.append("区：").append(location.getDistrict()).append("\n");
            currentPosition.append("街道：").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            positionText.setText(currentPosition);
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }
    public String Get()throws IOException {
        Request request = new Request.Builder().url(URL).get().build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            //Return string part.Do not use toString() to get return value
            return response.body().string();
        } else {
            return "Wrong";
        }
    }
}
