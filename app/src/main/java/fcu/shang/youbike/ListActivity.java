package fcu.shang.youbike;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fcu.shang.youbike.Adapter.MyAdapterItem;
import fcu.shang.youbike.DataBase.DBUse;
import fcu.shang.youbike.GoogleMap.MapsActivity;
import fcu.shang.youbike.Youbike.YouBike;


public class ListActivity extends AppCompatActivity {

    public static final String MyLat="MyLat";
    public static final String MyLng="MyLng";
    private ArrayList<ArrayList<YouBike>> cityYoubike=new ArrayList<>();
    final String cityName[]={"台北","基隆","新竹","桃園","台中","彰化"};

    private ListView listView;
    private Spinner spinner;

    private int pos=0;
    private DBUse dbUse;

    private MyAdapterItem myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbUse=new DBUse(this);

        for(int i=0;i<cityName.length;i++){                  //先幫二維串列新增一條
            cityYoubike.add(new ArrayList<YouBike>());
        }

        SortCity();
        init();
        admob();

    }

    public void SortCity(){
        Cursor cursor=dbUse.getAllYoubikeCousor();
        for(int i=0;i<cursor.getCount();i++){
            switch (cursor.getString(7)){              //7=city
                case "台北市":
                    cityYoubike.get(0).add(dbUse.getYouBike(cursor));
                    break;
                case "基隆市":
                    cityYoubike.get(1).add(dbUse.getYouBike(cursor));
                    break;
                case "新竹市":
                    cityYoubike.get(2).add(dbUse.getYouBike(cursor));
                    break;
                case "桃園市":
                    cityYoubike.get(3).add(dbUse.getYouBike(cursor));
                    break;
                case "台中市":
                    cityYoubike.get(4).add(dbUse.getYouBike(cursor));
                    break;
                case "彰化市":
                case "彰化縣":
                    cityYoubike.get(5).add(dbUse.getYouBike(cursor));
                    break;
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void admob(){
        AdView mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                //.addTestDevice("BD34A9A0939A0A4AF862F98AB60A85E4")
                .build(); //測試用廣告
        mAdView.loadAd(adRequest);
    }

    public void init(){
        listView=(ListView)findViewById(R.id.listview);
        myAdapter=new MyAdapterItem(this,cityYoubike.get(0));
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(listOnItemClick);


        spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                cityName));
        spinner.setOnItemSelectedListener(spinnerItemClick);
    }



    AdapterView.OnItemClickListener listOnItemClick=new AdapterView.OnItemClickListener() {     //短壓,切換到地圖模式並傳送經緯度
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            YouBike youBike=cityYoubike.get(pos).get(position);
            Intent intent=new Intent(ListActivity.this,MapsActivity.class);
            intent.putExtra(MyLat,youBike.getLat());
            intent.putExtra(MyLng,youBike.getLng());
            startActivity(intent);
        }
    };


    AdapterView.OnItemSelectedListener spinnerItemClick=new AdapterView.OnItemSelectedListener() {      //更換縣市YOUBIK
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            pos=position;
            myAdapter=new MyAdapterItem(ListActivity.this,cityYoubike.get(pos));
            listView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


}

