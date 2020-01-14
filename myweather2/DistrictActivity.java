package com.example.myweather2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DistrictActivity extends AppCompatActivity {
    ListView districtListView;
    int citytId;
    ArrayList<String> districtNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);

        Intent intent=getIntent();
        citytId=intent.getIntExtra("citytId",0);
        String cityName=intent.getStringExtra("cityName");
        int provinceId=intent.getIntExtra("provinceId",0);
        TextView cityNameText=(TextView)findViewById(R.id.city_name);
        cityNameText.setText(cityName);

        districtListView=(ListView)findViewById(R.id.district_list_view);
        districtListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(DistrictActivity.this,MainActivity.class);
                intent.putExtra("districtName",districtNameList.get(position));
                SharedPreferences.Editor editor=getSharedPreferences("userData",MODE_PRIVATE).edit();
                editor.putString("location",districtNameList.get(position));
                editor.apply();
                startActivity(intent);
            }
        });

        SharedPreferences pref=getSharedPreferences("districtData"+citytId,MODE_PRIVATE);
        String districtNameJson=pref.getString("districtNameJson","empty");
        if(districtNameJson.equals("empty")){
            DistrictHttp.getDistrictWithHttpConnection(handler,provinceId,citytId);
        }
        else{
            loadLocalData(districtNameJson);
        }
    }

    private void loadLocalData(String districtNameJson){
        try {
            JSONArray jsonArray=new JSONArray(districtNameJson);
            districtNameList=new ArrayList<String>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("name");
                districtNameList.add(name);
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(DistrictActivity.this,android.R.layout.simple_list_item_1,districtNameList);
            districtListView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    districtNameList=msg.getData().getStringArrayList("districtNameList");
                    String districtNameJson=msg.getData().getString("districtJson");
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(DistrictActivity.this,android.R.layout.simple_list_item_1,districtNameList);
                    districtListView.setAdapter(adapter);
                    SharedPreferences.Editor editor=getSharedPreferences("districtData"+citytId,MODE_PRIVATE).edit();
                    editor.putString("districtNameJson",districtNameJson);
                    editor.apply();
                    break;
            }
        }
    };
}
