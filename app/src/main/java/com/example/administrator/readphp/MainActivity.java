package com.example.administrator.readphp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] permissionsArray = new String[]{
            Manifest.permission.READ_PHONE_STATE};

    private static final String TAG = "Loggggggggggg";
    private OkHttpClient client = new OkHttpClient();
    private Button getData = null;
    private Button postData = null;
    private Button saveSP = null;
    private Button getSP = null;
    private JSONObject dataJson;

    private String id ;
    private String machine;
    private String ad1;
    private String ad2;
    private String ad3;
    private String ad4;
    private String ad5;
    private String ad6;
    private String date;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String url = "https://zhuanlan.zhihu.com/api/columns/pandemic?";
//    private final String url = "http://192.168.8.19/postS.php";
//    private final String url = "http://117.48.203.36";



    private final String json = "{ \"firstName\": \"Brett\" }";
    Timer timer ;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    sendRequestWithOkHttp();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postData = (Button)findViewById(R.id.postBtn);
        getData = (Button)findViewById(R.id.getBtn);
        saveSP = (Button)findViewById(R.id.saveSPBtn);
        getSP = (Button)findViewById(R.id.getSPBtn);

        getData.setOnClickListener(this);
        postData.setOnClickListener(this);
        saveSP.setOnClickListener(this);
        getSP.setOnClickListener(this);
        timer  = new Timer();
        timer.schedule(task,0,5000);

        checkAndRequestPermission();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getBtn:
                sendRequestWithOkHttp();
                break;
            case R.id.postBtn:
//                postRequestWithOkHttp(url,json);
                Log.d("TAG", "onClick: POST json Id = " + getuniqueId());
                Log.d("TAG", "onClick: POST json Id = " + getUniquePsuedoID());
                break;
            case R.id.saveSPBtn:
                saveTokenToSharedPreferences(getUniquePsuedoID()); //保存token
                break;
            case R.id.getSPBtn:
                Log.d(TAG, "onClick: ---print token from SP --:" + getTokenFromSharedPreference());
                break;
            default:
                break;
        }
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(url) //指定访问的服务器地址是电脑本机
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    Log.d(TAG, "run: " + responseData);
                    List<Topics> list = getJSONWithGSON(responseData);
                    for (int i = 0; i < list.size(); i++) {
                        Log.d("ceshi",list.get(i).getPostsCount() + "--" + list.get(i).getId() + "--" + list.get(i).getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void postRequestWithOkHttp(final String url, final String json){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(JSON,json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("TAG", "POST!");
                    if (response.isSuccessful()) {
                        Log.d("TAG", response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithGSON(String jsonData) { //使用jsonarray解析
        List<Topics> topicsList = new ArrayList<Topics>();
        try {
            dataJson = new JSONObject(jsonData); //t是数据
            JSONArray postTopicsArray = dataJson.getJSONArray("postTopics");   //数组对应的标签获取数组
            Log.d(TAG, "parseJSONWithGSON: array.length:  " + postTopicsArray.length());

            //解析数据：
            JsonParser parse =new JsonParser();  //创建json解析器
            JsonObject json=(JsonObject) parse.parse(jsonData);
            //解析--对象数据
            JsonObject creator = json.get("creator").getAsJsonObject();
            JsonObject avatar = creator.get("avatar").getAsJsonObject();
            Log.d(TAG, "解析对象：" + avatar.get("template").getAsString() + " " + creator.get("bio").getAsString());

            String followersCount = dataJson.getString("followersCount");
            String activateState = dataJson.getString("activateState");
//            String creator = dataJson.getString("creator");
            Log.d(TAG, "parseJSONWithGSON: followersCount:  " + followersCount + " " + activateState);

            JSONObject info = postTopicsArray.getJSONObject(0);  //解析数组第一组数据
            String postsCount = info.getString("postsCount");
            String id = info.getString("id");
            String name = info.getString("name");
            Log.d(TAG, "parseJSONWithGSON: " + postsCount + "----" + id + "----" + name + "----");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public List<Topics> getJSONWithGSON(String jsonData) {  //使用
        List<Topics> topicsList = new ArrayList<Topics>();
        try {
            dataJson = new JSONObject(jsonData); //t是数据
            JSONArray postTopicsArray = dataJson.getJSONArray("postTopics");   //数组对应的标签获取数组
            Log.d(TAG, "parseJSONWithGSON: array.length:  " + postTopicsArray.length());

            //解析
            for (int i = 0;i < postTopicsArray.length(); i++){
                JSONObject jsonObject= (JSONObject) postTopicsArray.get(i);
                topicsList.add(new Topics(jsonObject.optString("postsCount"), jsonObject.optString("id"), jsonObject.optString("name")));
            }
            return topicsList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void showResponse(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在这里进行UI操作
//                machineShow.setText(s);
//                ad1Show.setText(ad1);
//                ad2Show.setText(ad2);
//                ad3Show.setText(ad3);
//                ad4Show.setText(ad4);
//                ad5Show.setText(ad5);
//                ad6Show.setText(ad6);
//                dateShow.setText(date);
                Log.d("TAG", "run: TextViewShow--------");
            }
        });
    }


    private String getuniqueId(){  //获取手机IMEI码
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = tm.getDeviceId();
        String imei;
        //java反射机制
        Class clazz = tm.getClass();
        Method getImei= null;//(int slotId)
        try {
            getImei = clazz.getDeclaredMethod("getImei",int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            //获得IMEI 1的信息：
            imei = getImei.invoke(tm, 0).toString();
            //获得IMEI 2的信息：
            imei = imei + "+" + getImei.invoke(tm, 1);
            return imei;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


//        String simSerialNumber=tm.getSimSerialNumber();
//        String androidId =android.provider.Settings.Secure.getString(
//                getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
//        UUID deviceUuid = newUUID(androidId.hashCode(), ((long)imei.hashCode() << 32) |simSerialNumber.hashCode());
//        String uniqueId = deviceUuid.toString();
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){ //权限号。
            case RunntimePermissionHelper.REQUEST_CODE_ASK_PERMISSIONS:
                for (int i=0; i<permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "做一些申请成功的权限对应的事！"+permissions[i], Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "权限被拒绝： "+permissions[i], Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
    }

    private boolean checkAndRequestPermission() {
        return RunntimePermissionHelper.checkAndRequestForRunntimePermission(
                this, permissionsArray);
    }


     //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 位
        Log.d(TAG, "getUniquePsuedoID: ---" + m_szDevIDShort);
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            Log.d(TAG, "getUniquePsuedoID: serialAPI >= 9" + serial);
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
            Log.d(TAG, "getUniquePsuedoID: serialAPI < 9" + serial);
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    //保存token至本地sharpreference  token由本地生成uuid，在登录的时候发送至服务器，服务器重新打包后返回真正的token，保存至本地。
    private void saveTokenToSharedPreferences(String token){
        //将String保持至SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("testSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.commit();
    }
    //取出本地保存的string
    private String getTokenFromSharedPreference(){
        SharedPreferences sharedPreferences=getSharedPreferences("testSP", Context.MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String tokenString=sharedPreferences.getString("token", "");
        return tokenString;
    }
}

