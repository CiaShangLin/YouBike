package fcu.shang.youbike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fcu.shang.youbike.Adapter.MyAdapterItem;
import fcu.shang.youbike.DataBase.DBUse;
import fcu.shang.youbike.GoogleMap.MapsActivity;
import fcu.shang.youbike.Youbike.YouBike;

public class FavoriteActivity extends AppCompatActivity {

    private ListView favorite;
    private ArrayList<YouBike> youBikes=new ArrayList<>();
    private DBUse dbUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        admob();
        dbUse=new DBUse(this);

        youBikes=dbUse.getFavoriteYoubike();

        favorite=(ListView)findViewById(R.id.favoriteListView);
        favorite.setOnItemClickListener(favoriteListener);
        favorite.setAdapter(new MyAdapterItem(this,youBikes));
    }

    AdapterView.OnItemClickListener favoriteListener=new AdapterView.OnItemClickListener() {       //短壓,切換到地圖模式並傳送經緯度
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            YouBike youBike=youBikes.get(position);
            Intent intent=new Intent(FavoriteActivity.this,MapsActivity.class);
            intent.putExtra(ListActivity.MyLat,youBike.getLat());
            intent.putExtra(ListActivity.MyLng,youBike.getLng());
            startActivity(intent);
        }
    };

    private void admob(){
        //MobileAds.initialize(MainActivity.this,"ca-app-pub-3596318314144695~7532733968");
        AdView mAdView = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                //.addTestDevice("BD34A9A0939A0A4AF862F98AB60A85E4")
                .build(); //測試用廣告
        //我的ASUS手機 BD34A9A0939A0A4AF862F98AB60A85E4
        //Log.d("DEVICE_ID_EMULATOR",AdRequest.DEVICE_ID_EMULATOR);
        mAdView.loadAd(adRequest);
    }

}
