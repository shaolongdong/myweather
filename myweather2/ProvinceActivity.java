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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProvinceActivity extends AppCompatActivity {
    ListView provinceListView;
    ArrayList<String> provinceNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        provinceListView=(ListView)findViewById(R.id.province_list_view);
        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ProvinceActivity.this,CityActivity.class);
                intent.putExtra("provinceId",position+1);
                intent.putExtra("provinceName",provinceNameList.get(position));
                startActivity(intent);
            }
        });

        SharedPreferences pref=getSharedPreferences("provinceData",MODE_PRIVATE);
        String provinceNameJson=pref.getString("provinceNameJson","empty");
        if(provinceNameJson.equals("empty")){
            ProvinceHttp.getProvinceWithHttpConnection(handler);
        }
        else{
            loadLocalData(provinceNameJson);
        }

    }

    private void loadLocalData(String provinceNameJson){
        try {
            JSONArray jsonArray=new JSONArray(provinceNameJson);
            provinceNameList=new ArrayList<String>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("name");
                provinceNameList.add(name);
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(ProvinceActivity.this,android.R.layout.simple_list_item_1,provinceNameList);
            provinceListView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    provinceNameList=msg.getData().getStringArrayList("provinceNameList");
                    String provinceNameJson=msg.getData().getString("provinceJson");
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(ProvinceActivity.this,android.R.layout.simple_list_item_1,provinceNameList);
                    provinceListView.setAdapter(adapter);
                    SharedPreferences.Editor editor=getSharedPreferences("provinceData",MODE_PRIVATE).edit();
                    editor.putString("provinceNameJson",provinceNameJson);
                    editor.apply();
                    break;
            }
        }
    };
}
