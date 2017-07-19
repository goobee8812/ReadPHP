package com.example.administrator.readphp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getData = null;
    private TextView machineShow = null;
    private TextView ad1Show = null;
    private TextView ad2Show = null;
    private TextView ad3Show = null;
    private TextView ad4Show = null;
    private TextView ad5Show = null;
    private TextView ad6Show = null;
    private TextView dateShow = null;

    private String id ;
    private String machine;
    private String ad1;
    private String ad2;
    private String ad3;
    private String ad4;
    private String ad5;
    private String ad6;
    private String date;

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
        machineShow = (TextView)findViewById(R.id.machineTv);
        ad1Show = (TextView)findViewById(R.id.ad1Tv);
        ad2Show = (TextView)findViewById(R.id.ad2Tv);
        ad3Show = (TextView)findViewById(R.id.ad3Tv);
        ad4Show = (TextView)findViewById(R.id.ad4Tv);
        ad5Show = (TextView)findViewById(R.id.ad5Tv);
        ad6Show = (TextView)findViewById(R.id.ad6Tv);
        dateShow = (TextView)findViewById(R.id.dateTv);
        getData = (Button)findViewById(R.id.getBtn);
        getData.setOnClickListener(this);
        timer  = new Timer();
        timer.schedule(task,0,5000);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.getBtn){
            sendRequestWithOkHttp();
        }
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://120.76.114.183:666/readS.php") //指定访问的服务器地址是电脑本机
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseXMLWithPull(responseData);
                    parseJSONWithGSON(responseData);
                    //showResponse(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private void parseJSONWithGSON(String jsonData) {
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0;i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                id = jsonObject.getString("id");
                machine = jsonObject.getString("machine");
                ad1 = jsonObject.getString("ad1");
                ad2 = jsonObject.getString("ad2");
                ad3 = jsonObject.getString("ad3");
                ad4 = jsonObject.getString("ad4");
                ad5 = jsonObject.getString("ad5");
                ad6 = jsonObject.getString("ad6");
                date = jsonObject.getString("date");
                showResponse();
//                Log.d("MainActivity:-------","id is "+id);
//                Log.d("MainActivity:-------","machine is "+machine);
//                Log.d("MainActivity:-------","ad1 is "+ad1);
//                Log.d("MainActivity:-------","date is "+date);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在这里进行UI操作
                machineShow.setText(machine);
                ad1Show.setText(ad1);
                ad2Show.setText(ad2);
                ad3Show.setText(ad3);
                ad4Show.setText(ad4);
                ad5Show.setText(ad5);
                ad6Show.setText(ad6);
                dateShow.setText(date);
                Log.d("TAG", "run: TextViewShow--------");
            }
        });
    }
}

