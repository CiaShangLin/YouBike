package fcu.shang.youbike.Youbike;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import fcu.shang.youbike.DataBase.DBOpenHelper;
import fcu.shang.youbike.DataBase.DBUse;
import fcu.shang.youbike.FunctionListener;
import fcu.shang.youbike.MainActivity;

/*Youbike的所有資料除了城市*/

public class YoubikeBW extends AsyncTask<String,Integer,String> {

    private Elements elements;
    private Document doc;
    private Element e;
    private static final String URL="http://i.youbike.com.tw/cht/f11.php";
    private FunctionListener functionListener;
    ArrayList<YouBike> youBikes=new ArrayList<>();
    Context context;
    SQLiteDatabase db;
    DBOpenHelper dbOpenHelper;
    ProgressDialog progressDialog;

    public YoubikeBW(FunctionListener listener,Context context){
        functionListener=listener;
        this.context=context;
        //dbOpenHelper=new DBOpenHelper(context);
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        //db=dbOpenHelper.getWritableDatabase();

        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("資料更新中...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String YoubikeInfo=getInfo();
        parseJson(YoubikeInfo);
        return YoubikeInfo;
    }

    public String getInfo(){
        try {
            doc= Jsoup.connect(URL).timeout(10000).get();
            elements=doc.getElementsByTag("script");
            e=elements.get(20);                                   //第20個節點
            List<DataNode> dataNode=e.dataNodes();
            Log.d("DATANODE",dataNode.get(0).getWholeData());

            StringBuffer sb=new StringBuffer("");
            sb.append(dataNode.get(0).getWholeData());

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseJson(String result){
        String[] str=result.split("siteContent");
        String s=str[2].substring(2,str[2].length()-2);   //剃好了 JSON格式

        int count=1;
        try {
            JSONObject jsonObject=new JSONObject(s);
            while(count<=7068) {                                    //0001~7068 因為沒有照順序排

                String local = getLocal(count);
                try {
                    JSONObject youbike = jsonObject.getJSONObject(local);
                    setYoubike(youbike);
                } catch (JSONException e) {                         //沒有每個都有 所以例外就跳回去不理她
                    count++;
                    continue;
                }
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLocal(int count){                            //int轉字串前面補0的處理
        StringBuffer sb=new StringBuffer("0000");
        sb.append(String.valueOf(count));
        String local=sb.substring(sb.length()-4,sb.length());
        return local;
    }

    public void setYoubike(JSONObject jsonObject){                //設定YOUBIKE 存入ArrayList
        YouBike youBike=new YouBike();
        try {
            youBike.setSna(jsonObject.getString("sna"));
            youBike.setSbi(jsonObject.getInt("sbi"));
            youBike.setBemp(jsonObject.getInt("bemp"));
            youBike.setLat(jsonObject.getDouble("lat"));
            youBike.setLng(jsonObject.getDouble("lng"));
            youBike.setMday(jsonObject.getString("mday"));
            youBikes.add(youBike);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


        upData(youBikes);                            //更新資料庫

    }

    public void upData(ArrayList<YouBike> youBikes){
        DBUse dbUse=new DBUse(context);
        int citySize=dbUse.haveCity(),youbikeSize=youBikes.size();

        if(citySize==0){           //city數沒有相符,可能是第一次載入,
            Log.d("DBUSE",citySize+" city");
            progressDialog.dismiss();
            functionListener.setYouBikeCity(youBikes);
        }else if(citySize<youbikeSize){          //有可能是API超出使用次數
            Log.d("DBUSE",citySize+" "+youbikeSize);
            dbUse.DeleteDataSheet();
            progressDialog.dismiss();
            functionListener.setYouBikeCity(youBikes);
        }else if(citySize==youbikeSize){         //已載入到資料表,只需要更新資料
            Log.d("DBUSE","youbikesize: "+youbikeSize);
            dbUse.UpdataYoubike(youBikes);
            progressDialog.dismiss();
        }




        /*Cursor cursor=db.rawQuery("select * from "+DBOpenHelper.YOUBIKE_TABLE,null);
        cursor.moveToFirst();
        try{
            Log.d("Cursor",cursor.getString(7)+" "+cursor.getInt(0));             //沒有的話觸發例外
            for(int i=0;i<cursor.getCount();i++){
                ContentValues cv=new ContentValues();
                cv.put("sbi",youBikes.get(i).getSbi());
                cv.put("bemp",youBikes.get(i).getBemp());
                cv.put("mDay",youBikes.get(i).getMday());
                db.update(DBOpenHelper.YOUBIKE_TABLE,cv,"_id="+(i),null);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }catch (Exception e){
            Log.d("Cursor","觸發例外，第一次載入");
            cursor.close();
            db.close();
            functionListener.setYouBikeCity(youBikes);
        }*/

    }



}
