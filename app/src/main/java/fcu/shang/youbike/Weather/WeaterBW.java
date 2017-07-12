package fcu.shang.youbike.Weather;

import android.app.ProgressDialog;
import android.icu.text.LocaleDisplayNames;
import android.icu.text.StringSearch;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import fcu.shang.youbike.FunctionListener;

/**
 * Created by Shang on 2017/6/29.
 */

public class WeaterBW extends AsyncTask<String,Integer,String> {


    StringBuffer sb=new StringBuffer("");
    private Document document;
    private Elements elements;
    private FunctionListener functionListener;

    public WeaterBW(FunctionListener listener){
        this.functionListener=listener;
    }


    @Override
    protected String doInBackground(String... params) {
        today(params[0]);
        sb.append("end ");

        return sb.toString();
    }

    public void today(String url){
        try{
            document= Jsoup.connect(url).get();
            elements=document.select("table.FcstBoxTable01").select("td");
            //Log.d("TODAY",elements.get(0).text()); //26 ~ 29

            String today[]=elements.get(0).text().split(" ");              //氣溫
            sb.append(today[0]+""+today[1]+""+today[2]+" ");


            elements=document.select("table.FcstBoxTable01").select("img");
            sb.append(elements.get(0).attr("alt")+" ");
            //Log.d("TODAY",elements.attr("alt"));                //天氣狀況

            elements=document.select("table.FcstBoxTable01").select("td");
            sb.append(elements.get(2).text()+" ")
                    .append(elements.get(3).text()+" ");

            //Log.d("TODAY 2",elements.get(2).text());            //降雨機率
            //Log.d("TODAY 3",elements.get(3).text());            //降雨機率

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sp(String text){
        String s[]=text.split(" ");
        return s[0];
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        functionListener.setWeater(result);

    }
}
