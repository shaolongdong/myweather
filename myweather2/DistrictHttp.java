package com.example.myweather2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 董少龙 on 2019/10/8.
 */

public class DistrictHttp {
    public static void getDistrictWithHttpConnection(final Handler handler,final int provinceId, final int cityId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try {
                    URL url=new URL("http://guolin.tech/api/china/"+provinceId+"/"+cityId);
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


                    JSONArray jsonArray=new JSONArray(response.toString());
                    ArrayList<String> districtNameList=new ArrayList<String>();
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        districtNameList.add(name);
                    }

                    Message msg=new Message();
                    msg.what=1;
                    Bundle bundle=new Bundle();
                    bundle.putStringArrayList("districtNameList",districtNameList);
                    bundle.putString("districtJson",response.toString());

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
