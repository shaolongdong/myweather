package com.example.myweather2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 董少龙 on 2019/10/8.
 */

public class WeatherHttp {

    public static void getWeatherWithHttpConnection(final Handler handler,final String location){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> weatherList=new ArrayList<String>();
                List listTemp= Arrays.asList("晴","多云","阴","阵雨","雷阵雨","雷阵雨伴有冰雹","雨夹雪","小雨","中雨","大雨","暴雨","大暴雨","特大暴雨","阵雪","小雪","中雪","大雪","暴雪","雾","冻雨","沙尘暴","小雨-中雨","中雨-大雨","大雨-暴雨","暴雨-大暴雨","大暴雨-特大暴雨","小雪-中雪","中雪-大雪","大雪-暴雪","浮尘","扬沙","强沙尘暴","霾");
                weatherList.addAll(listTemp);//weatherList初始化
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try {
                    String key="dc8fbdaa5d4949473fc027d999555dc0";
                    String cityCode= null;
                    cityCode = URLEncoder.encode(location,"UTF-8");
                    String urlStr="http://v.juhe.cn/weather/index?format=2&cityname="+cityCode+"&key="+key;
                    URL url=new URL(urlStr);
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }

                    JSONObject jsonObject=new JSONObject(response.toString());
                    JSONObject result=jsonObject.getJSONObject("result");
                    JSONObject sk=result.getJSONObject("sk");
                    JSONObject today=result.getJSONObject("today");
                    JSONObject weather_id=today.getJSONObject("weather_id");
                    String weatherIdFa=weather_id.getString("fa");
                    int weatherIdFaInt=Integer.parseInt(weatherIdFa);
                    if(weatherIdFaInt==53) weatherIdFaInt=32;

                    String nowTemNum=sk.getString("temp");
                    String nowTemQing=weatherList.get(weatherIdFaInt)+"(实时)";
                    String temRange=today.getString("temperature");
                    String sunRain=today.getString("weather");
                    String wind=today.getString("wind");
                    String imageName="i"+weatherIdFa;

                    Message msg=new Message();
                    msg.what=1;
                    Bundle bundle=new Bundle();
                    bundle.putString("nowTemNum",nowTemNum);
                    bundle.putString("nowTemQing",nowTemQing);
                    bundle.putString("temRange",temRange);
                    bundle.putString("sunRain",sunRain);
                    bundle.putString("wind",wind);
                    bundle.putString("imageName",imageName);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
