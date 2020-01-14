package com.example.myweather2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity {
    private TextView locationText;
    private TextView nowTemNum;
    private TextView nowTemQing;
    private TextView temRange;
    private TextView sunRain;
    private TextView wind;
    private ImageView imageIcon;
    private String province;
    private String city;
    private String district;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref=getSharedPreferences("userData",MODE_PRIVATE);
        location=pref.getString("location","empty");
        if(location.equals("empty")){
            Intent intent=new Intent(MainActivity.this,ProvinceActivity.class);
            startActivity(intent);
        }

        //状态栏与软件融合
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }//状态栏与软件融合
        setContentView(R.layout.activity_main);

        TextView locationText=(TextView)findViewById(R.id.location);
        locationText.setText(location);
        TextView time_now=(TextView)findViewById(R.id.mytime);
        nowTemNum=(TextView)findViewById(R.id.now_tem_num);
        nowTemQing=(TextView)findViewById(R.id.now_tem_qing);
        temRange=(TextView)findViewById(R.id.tem_range);
        sunRain=(TextView)findViewById(R.id.sun_rain);
        wind=(TextView)findViewById(R.id.wind);
        imageIcon=(ImageView) findViewById(R.id.image_icon);

        try {
            time_now.setText(new TimeNow().getRes());
        } catch (ParseException e) {
            e.printStackTrace();
        }//获取日期信息并展示

        Button nav_button=(Button)findViewById(R.id.nav_button);
        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ProvinceActivity.class);
                startActivity(intent);
            }
        });

        WeatherHttp.getWeatherWithHttpConnection(handler,location);



    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String nowTemNumData=msg.getData().getString("nowTemNum");
                    String nowTemQingData=msg.getData().getString("nowTemQing");
                    String temRangeData=msg.getData().getString("temRange");
                    String sunRainData=msg.getData().getString("sunRain");
                    String windData=msg.getData().getString("wind");
                    String imageNameData=msg.getData().getString("imageName");
                    int imgId=getResources().getIdentifier(imageNameData,"drawable","com.example.myweather2");

                    nowTemNum.setText(nowTemNumData);
                    nowTemQing.setText(nowTemQingData);
                    temRange.setText(temRangeData);
                    sunRain.setText(sunRainData);
                    wind.setText(windData);
                    imageIcon.setImageResource(imgId);

                    break;
            }
        }
    };





}
