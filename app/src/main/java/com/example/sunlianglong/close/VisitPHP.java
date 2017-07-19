package com.example.sunlianglong.close;

import android.util.Log;

import org.apache.http.HttpResponse;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sun liang long on 2017/6/3.
 */

public class VisitPHP {
    public   String SendRequest(String ip_adds,String up_name,String up_name_key){
        //通过HttpClient类与WEB服务器交互
        HttpClient httpClient = new DefaultHttpClient();
        //定义与服务器交互的地址
        String ServerUrl = ip_adds;//ip_adds
        //设置读取超时，注意CONNECTION_TIMEOUT和SO_TIMEOUT的区别
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        //设置读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        //POST方式
        HttpPost httpRequst = new HttpPost(ServerUrl);
        //准备传输的数据
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(up_name, up_name_key));
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
                //System.out.println(builder.toString());
                return builder.toString();
            }
            else{
                Log.e(TAG, "连接超时 ");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "请求错误 ");
            Log.e(TAG, e.getMessage());
        }
        return null;
    }
}
