package com.example.myweather2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CityActivity extends AppCompatActivity {
    ListView cityListView;
    int provinceId;
    ArrayList<Integer> cityIdList;
    ArrayList<String> cityNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Intent intent=getIntent();
        provinceId=intent.getIntExtra("provinceId",0);
        String provinceName=intent.getStringExtra("provinceName");
        TextView provinceNameText=(TextView)findViewById(R.id.province_name);
        provinceNameText.setText(provinceName);

        cityListView=(ListView)findViewById(R.id.city_list_view);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CityActivity.this,DistrictActivity.class);
                intent.putExtra("citytId",cityIdList.get(position));
                intent.putExtra("cityName",cityNameList.get(position));
                intent.putExtra("provinceId",provinceId);
                startActivity(intent);
            }
        });

        SharedPreferences pref=getSharedPreferences("cityData"+provinceId,MODE_PRIVATE);
        String cityNameJson=pref.getString("cityNameJson","empty");
        if(cityNameJson.equals("empty")){
            CityHttp.getCityWithHttpConnection(handler,provinceId);
        }
        else{
            loadLocalData(cityNameJson);
        }
    }

    private void loadLocalData(String cityNameJson){
        try {
            JSONArray jsonArray=new JSONArray(cityNameJson);
            cityNameList=new ArrayList<String>();
            cityIdList=new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("name");
                cityNameList.add(name);
                int id=jsonObject.getInt("id");
                cityIdList.add(id);
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(CityActivity.this,android.R.layout.simple_list_item_1,cityNameList);
            cityListView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    cityNameList=msg.getData().getStringArrayList("cityNameList");
                    cityIdList=msg.getData().getIntegerArrayList("cityIdList");
                    String cityNameJson=msg.getData().getString("cityJson");
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(CityActivity.this,android.R.layout.simple_list_item_1,cityNameList);
                    cityListView.setAdapter(adapter);
                    SharedPreferences.Editor editor=getSharedPreferences("cityData"+provinceId,MODE_PRIVATE).edit();
                    editor.putString("cityNameJson",cityNameJson);
                    editor.apply();
                    break;
            }
        }
    };

}
