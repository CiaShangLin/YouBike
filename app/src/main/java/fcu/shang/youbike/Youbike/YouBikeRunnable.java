package fcu.shang.youbike.Youbike;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Shang on 2017/5/21.
 */
public class YouBikeRunnable implements Runnable{

    URL url;
    HttpURLConnection httpURLConnection;
    InputStream inputStream;
    BufferedReader bufferedReader;
    boolean flag=true;
    YouBike youbike;
    String city[]={"彰化縣","彰化市","新北市","基隆市","台北市","桃園市","台中市","新竹市"};

    public YouBikeRunnable( YouBike youbike){
        this.youbike=youbike;
    }

    @Override
    public void run() {
        String urlcity=getUrl_City(youbike.getLat(), youbike.getLng());
        openHttp(urlcity);
        readCity();

        while(flag){
            try {
                Thread.sleep(100);
                openHttp(urlcity);
                readCity();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public String getUrl_City(double lat,double lng){
        StringBuffer url_city=new StringBuffer("");
        url_city.append("https://maps.google.com/maps/api/geocode/json?latlng=")
                .append(lat)
                .append(",")
                .append(lng)
                .append("&language=zh-TW&sensor=true");
        Log.d("URL",url_city.toString());
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

    public void readCity(){
        String line="";
        StringBuffer sb=new StringBuffer("");
        try {
            while((line=bufferedReader.readLine())!=null){
                sb.append(line);
                //Log.v("CITY",sb.toString());
                int[] a=new int[city.length];
                for(int i=0;i<city.length;i++) {
                    a[i] = line.indexOf(city[i]);
                    if (a[i] != -1) {
                        youbike.setCity(city[i]);
                        Log.v("CITY", youbike.getCity());
                        break;
                    }
                }
                if(!youbike.getCity().equals("")){
                    flag=false;
                    break;
                }
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
