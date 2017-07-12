package fcu.shang.youbike.Youbike;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fcu.shang.youbike.DataBase.DBOpenHelper;

/**
 * Created by Shang on 2017/6/13.
 */

public class YoubikeCity extends AsyncTask<String,Integer,String> {

    long start,end;
    ProgressDialog progressDialog;
    Context context;
    URL url;
    HttpURLConnection httpURLConnection;
    InputStream inputStream;
    BufferedReader bufferedReader;
    boolean flag=true;
    ArrayList<YouBike> youbikes;

    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;

    String city[]={"彰化縣","彰化市","新北市","基隆市","台北市","桃園市","台中市","新竹市"};

    int API=0;


    public YoubikeCity(Context context, ArrayList<YouBike> youBikes){
        this.context=context;
        this.youbikes=youBikes;
        dbOpenHelper=new DBOpenHelper(context);
        db=dbOpenHelper.getWritableDatabase();
    }

    @Override
    protected void onPreExecute() {
        Log.d("Runnable","現成啟動"+youbikes.size());
        start=System.currentTimeMillis();

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("第一次載入會比較久");
        progressDialog.setMessage("載入資料中");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(youbikes.size());
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        for(int i=0;i<youbikes.size();i++){
            flag=true;
            String urlcity=getUrl_City(youbikes.get(i).getLat(), youbikes.get(i).getLng(),i);
            openHttp(urlcity);
            readCity(i);

            while(flag){
                try {
                    Thread.sleep(200);
                    openHttp(urlcity);
                    readCity(i);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(API==1){
                Log.d("APICLOSE","close");
                break;
            }
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {

        progressDialog.dismiss();
        end=System.currentTimeMillis();
        Log.d("Runnable","現成結束 "+(double)(end-start)/1000);
        db.close();
    }


    public String getUrl_City(double lat,double lng,int i){
        StringBuffer url_city=new StringBuffer("");
        url_city.append("https://maps.google.com/maps/api/geocode/json?latlng=")
                .append(lat)
                .append(",")
                .append(lng)
                .append("&language=zh-TW&sensor=true");
        Log.d("URL",i+" "+url_city.toString());
        return url_city.toString();
    }

    public void openHttp(String urlcity){
        try {
            url=new URL(urlcity);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(3000);
            inputStream=httpURLConnection.getInputStream();
            bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

        }  catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readCity(int p){
        String line="";
        StringBuffer sb=new StringBuffer("");
        try {
            while(flag==true && (line=bufferedReader.readLine())!=null){
                sb.append(line);
                Log.d("SBLINE",sb.toString());

                if(sb.toString().indexOf("request quota for this API")!=-1){
                    flag=false;
                    API=1;
                    break;
                }else if(sb.toString().indexOf("rate-limit for this API")!=-1){  //超速
                    break;
                }

                int match;
                for(int j=0;j<city.length;j++){
                    match=line.indexOf(city[j]);
                    if(match != -1){
                        youbikes.get(p).setCity(city[j]);
                        inputCity(youbikes.get(p));
                        progressDialog.setProgress(p);
                        flag=false;
                        //Log.v("CITY",p+":"+youbikes.get(p).getCity());
                        break;
                    }
                }

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inputCity(YouBike youBike){                   //寫入資料庫
        ContentValues cv=new ContentValues();
        cv.put(DBOpenHelper.sna,youBike.getSna());
        cv.put(DBOpenHelper.sbi,youBike.getSbi());
        cv.put(DBOpenHelper.bemp,youBike.getBemp());
        cv.put(DBOpenHelper.lat,youBike.getLat());
        cv.put(DBOpenHelper.lng,youBike.getLng());
        cv.put(DBOpenHelper.mDay,youBike.getMday());
        cv.put(DBOpenHelper.city,youBike.getCity());
        cv.put(DBOpenHelper.love,youBike.getLove());
        db.insert(DBOpenHelper.YOUBIKE_TABLE,null,cv);
    }
}
