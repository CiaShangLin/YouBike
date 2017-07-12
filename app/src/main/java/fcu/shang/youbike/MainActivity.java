package fcu.shang.youbike;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fcu.shang.youbike.Adapter.MainListAdapter;
import fcu.shang.youbike.DataBase.DBUse;
import fcu.shang.youbike.GPS.GpsFunction;
import fcu.shang.youbike.GoogleMap.MapsActivity;
import fcu.shang.youbike.Weather.PmBW;
import fcu.shang.youbike.Weather.WeaterBW;
import fcu.shang.youbike.Youbike.YouBike;
import fcu.shang.youbike.Youbike.YoubikeBW;
import fcu.shang.youbike.Youbike.YoubikeCity;


public class MainActivity extends AppCompatActivity implements FunctionListener{

    private ListView ItemListView;
    private Spinner weatherSp,pmSp;
    TextView weatherTv,weatherTv2,weatherTv3,weatherTv4,weatherTv5,pmTv,pmTv2;
    Intent intent;


    public static final String TAG_GPS = "TAG_GPS";
    public static final int REQUEST_CODE = 1; //onRequestPermissionsResult回傳用
    GpsFunction gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        admob();
        init();

        if(isConnected()){
            InputYouBike();
        }else{
            Toast.makeText(MainActivity.this,"請打開網路更新資料",Toast.LENGTH_SHORT).show();
        }

        openGps();


    }

    public void InputYouBike(){
        YoubikeBW youbikeBW=new YoubikeBW(MainActivity.this,this);
        youbikeBW.execute();
    }

    public void init(){

        weatherTv=(TextView)findViewById(R.id.weatherTv);              //今日
        weatherTv2=(TextView)findViewById(R.id.weatherTv2);            //溫度
        weatherTv3=(TextView)findViewById(R.id.weatherTv3);            //天氣
        weatherTv4=(TextView)findViewById(R.id.weatherTv4);            //舒適度
        weatherTv5=(TextView)findViewById(R.id.weatherTv5);            //降雨機率
        pmTv=(TextView)findViewById(R.id.pmTv);                      //數值
        pmTv2=(TextView)findViewById(R.id.pmTv2);                    //品質

        ItemListView=(ListView)findViewById(R.id.ItemListView);
        ItemListView.setAdapter(new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_activated_1
                ,getResources().getStringArray(R.array.item)));
        ItemListView.setOnItemClickListener(itemClickListener);
        ItemListView.setAdapter(new MainListAdapter(this));

        weatherSp=(Spinner)findViewById(R.id.weatherSp);
        pmSp=(Spinner)findViewById(R.id.pmSp);

        weatherSp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.weather)));
        weatherSp.setOnItemSelectedListener(weaterListener);

        pmSp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.pmStation)));
        pmSp.setOnItemSelectedListener(pmListener);


    }

    private void admob(){                            //廣告
        //MobileAds.initialize(MainActivity.this,"ca-app-pub-3596318314144695~7532733968");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                //.addTestDevice("BD34A9A0939A0A4AF862F98AB60A85E4")
                .build(); //測試用廣告
        mAdView.loadAd(adRequest);
    }

    AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:                           //常用站點
                    intent=new Intent(MainActivity.this,FavoriteActivity.class);
                    startActivity(intent);
                    break;
                case 1:                           //地圖模式
                    intent=new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(intent);
                    break;
                case 2:                            //列表模式
                    intent=new Intent(MainActivity.this,ListActivity.class);
                    startActivity(intent);
                    break;
                case 3:                            //製作者
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("製作者")
                            .setMessage("姓名:蔡尚霖\n逢甲大學資工系三年級")
                            .setNegativeButton("關閉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    break;
                case 4:                            //E-mail
                    Intent email=new Intent(Intent.ACTION_SENDTO);
                    email.setData(Uri.parse("mailto:west7418@gmail.com"));
                    email.putExtra(Intent.EXTRA_SUBJECT, "Youbike");
                    email.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(email);
                    break;
                case 5:                           //使用說明
                    new FullScreenDialog(MainActivity.this).show();
                    break;
            }

        }
    };

    AdapterView.OnItemSelectedListener weaterListener=new AdapterView.OnItemSelectedListener() {          //選擇地區天氣
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(isConnected()==true){
                String url[]=getResources().getStringArray(R.array.weatherUrl);
                WeaterBW weaterBW=new WeaterBW(MainActivity.this);
                weaterBW.execute(url[position]);
            }else{
                Toast.makeText(MainActivity.this,"請開啟網路",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener pmListener=new AdapterView.OnItemSelectedListener() {        //選擇地區PM2.5
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(isConnected()==true){
                String station[]=getResources().getStringArray(R.array.pmStation);
                //Log.d("station",station[position]);
                PmBW pmBW=new PmBW(MainActivity.this,station[position]);
                pmBW.execute("http://taqm.epa.gov.tw/taqm/tw/Aqi/North.aspx?type=all&fm=Pm25Index");
            }else{
                Toast.makeText(MainActivity.this,"請開啟網路",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    public void setYouBikeCity(ArrayList<YouBike> youbike){                  //Youbike的Json解析完後,換解析城市
        YoubikeCity youbikeCity=new YoubikeCity(this,youbike);
        youbikeCity.execute();
    }

    @Override
    public void setWeater(String result) {                                  //設定天氣UI
        String today[]=result.split(" ");
        weatherTv.setText("今日");
        weatherTv2.setText("溫度:"+today[0]+"℃");
        weatherTv3.setText("天氣:"+today[1]);
        weatherTv4.setText("舒適度:"+today[2]);
        weatherTv5.setText("降雨機率:"+today[3]+"%");
    }

    @Override
    public void setPM(String result){                                    //設定PM2.5 UI
        int quality=Integer.parseInt(result.equals("")?"-1":result);
        int color=getResources().getColor(R.color.green);
        int quality_number=0;
        String quality_name[]=getResources().getStringArray(R.array.quality);

        if(quality>=0 && quality<=50){
            color=getResources().getColor(R.color.green);
            quality_number=0;
        }else if(quality>=51 && quality<=100){
            color=getResources().getColor(R.color.yellow);
            quality_number=1;
        }else if(quality>=101 && quality<=150){
            color=getResources().getColor(R.color.orangered);
            quality_number=2;
        }else if(quality>=151 && quality<=200){
            color=getResources().getColor(R.color.red);
            quality_number=3;
        }else if(quality>=201 && quality<=300){
            color=getResources().getColor(R.color.darkviolet);
            quality_number=4;
        }else if(quality>=301 && quality<=500){
            color=getResources().getColor(R.color.darkred);
            quality_number=5;
        }else{
            quality_number=6;
        }

        pmTv2.setText("品質:"+quality_name[quality_number]);
        pmTv2.setTextColor(color);

        pmTv.setText("AQI:"+result);
        pmTv.setTextColor(color);
    }

    private boolean isConnected(){             //判斷是否有沒有開網路
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public void openGps(){
        gps=new GpsFunction(MainActivity.this);
        if(!gps.checkGPS()){                            //會跳出GPS權限打開詢問
            Intent intent = new Intent();              //答應開啟前往打開GPS
            intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            if(!gps.isOpenGps())                         //GPS沒開啟的話,就切到開啟畫面
                startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //接收GSP權限回復
        if (requestCode == REQUEST_CODE) {
            Log.d(TAG_GPS, requestCode + " " + REQUEST_CODE);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(!gps.isOpenGps())                 //權限開啟後,如果GSP沒開就切換到開啟頁面
                    startActivityForResult(gps.IntentOpenGps(), REQUEST_CODE);
            } else {
                Log.d(TAG_GPS, "拒絕開啟GPS");
                Toast.makeText(MainActivity.this, "拒絕開啟GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {         //接收GPS開啟回復
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG_GPS, "進入開啟GPS畫面");
        if (resultCode == REQUEST_CODE) {   //成功開啟
            Toast.makeText(MainActivity.this, "已開啟GPS", Toast.LENGTH_SHORT).show();
        } else {                                                      //開啟失敗
            Toast.makeText(MainActivity.this, "沒有開啟而返回", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG_GPS, resultCode == PackageManager.PERMISSION_GRANTED ? "有開啟GPS返回" : "沒有開啟GSP返回");
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
