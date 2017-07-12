package fcu.shang.youbike.Weather;

import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.os.AsyncTask;
import android.system.ErrnoException;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fcu.shang.youbike.FunctionListener;

/**
 * Created by SERS on 2017/7/2.
 */

public class PmBW extends AsyncTask<String,Void,String>{

    private Document document;
    private Elements elements,station_elements;
    private FunctionListener functionListener;

    String local;
    String AQI;

    public PmBW(FunctionListener functionListener,String local){
        this.functionListener=functionListener;
        this.local=local;
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            document= Jsoup.connect(params[0]).get();
            station_elements=document.select("table.TABLE_G").select("span");     //每七個唯一站

            elements=document.select("table.TABLE_G").select("a");     //每七個唯一站

            //Log.d("PM",elements.get(0).text());
            //Log.d("PM",elements.get(1).text());

            int station=8;
            for(int i=0;;i++,station+=7){
                String name=elements.get(i).text().substring(0,2);
                Log.d("PM",name);
                if(local.equals(name)){
                    station_elements=document.select("table.TABLE_G").select("span");
                    AQI=station_elements.get(station).text();
                    Log.d("PM",AQI);
                    break;
                }
            }

        }catch (Exception e){

        }
        return AQI;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        functionListener.setPM(s);
    }
}
