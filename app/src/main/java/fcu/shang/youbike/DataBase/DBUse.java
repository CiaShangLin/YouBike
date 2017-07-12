package fcu.shang.youbike.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import fcu.shang.youbike.ListActivity;
import fcu.shang.youbike.Youbike.YouBike;

/**
 * Created by SERS on 2017/6/27.
 */

public class DBUse {
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;

    public DBUse(Context context){
        this.context=context;
        dbOpenHelper=new DBOpenHelper(context);
    }

    public ArrayList<YouBike> getAllYoubike(){                     //取得所有站點 ListActivity,MapActivity
        db=dbOpenHelper.getReadableDatabase();
        ArrayList<YouBike> youBikes=new ArrayList<>();
        cursor=db.rawQuery("select * from "+DBOpenHelper.YOUBIKE_TABLE,null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            youBikes.add(getYouBike(cursor));
            cursor.moveToNext();
        }
        close();
        return  youBikes;
    }

    public ArrayList<YouBike> getFavoriteYoubike(){          //取得所有加入最愛的站點 FavoriteActivity
        db=dbOpenHelper.getReadableDatabase();
        ArrayList<YouBike> youBikes=new ArrayList<>();
        Cursor cursor=db.rawQuery("select * from "+DBOpenHelper.YOUBIKE_TABLE+" where love=1",null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            youBikes.add(getYouBike(cursor));
            cursor.moveToNext();
        }
        return youBikes;
    }

    public Cursor getAllYoubikeCousor(){          // 取得所有YOUBIKE的指標ListActivity
        db=dbOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+DBOpenHelper.YOUBIKE_TABLE,null);
        cursor.moveToFirst();
        db.close();
        return cursor;
    }

    public YouBike getYouBike(Cursor cursor){          //讀出指標的資料
        YouBike youBike=new YouBike();
        youBike.setSna(cursor.getString(1));
        youBike.setSbi(cursor.getInt(2));
        youBike.setBemp(cursor.getInt(3));
        youBike.setLat(cursor.getDouble(4));
        youBike.setLng(cursor.getDouble(5));
        youBike.setMday(cursor.getString(6));
        youBike.setCity(cursor.getString(7));
        youBike.setLove(cursor.getInt(8));
        return youBike;
    }

    public void JoinFavorite(YouBike youBike){              //加入最愛  MyadapterItem
        db=dbOpenHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBOpenHelper.love,1);
        db.update(DBOpenHelper.YOUBIKE_TABLE,cv, DBOpenHelper.sna+"='"+youBike.getSna()+"'",null);
        youBike.setLove(1);
        Toast.makeText(context,youBike.getSna()+"加入最愛",Toast.LENGTH_SHORT).show();
        db.close();
    }

    public void JoinFavorite(String title){              //加入最愛  MapActivity
        db=dbOpenHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBOpenHelper.love,1);
        db.update(DBOpenHelper.YOUBIKE_TABLE,cv, DBOpenHelper.sna+"='"+title+"'",null);

        Toast.makeText(context,title+"加入最愛",Toast.LENGTH_SHORT).show();
        db.close();
    }

    public void Cancel(YouBike youBike){                 //取消最愛  MyadapterItem
        db=dbOpenHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DBOpenHelper.love,0);
        db.update(DBOpenHelper.YOUBIKE_TABLE,cv, DBOpenHelper.sna+"='"+youBike.getSna()+"'",null);
        youBike.setLove(0);
        Toast.makeText(context,youBike.getSna()+"取消最愛",Toast.LENGTH_SHORT).show();
        db.close();
    }

    public int haveCity(){                    //回傳資料表的CITY數量
        db=dbOpenHelper.getReadableDatabase();
        cursor=db.rawQuery("select * from "+DBOpenHelper.YOUBIKE_TABLE,null);
        int size=cursor.getCount();
        db.close();
        cursor.close();
        return size;
    }

    public void DeleteDataSheet(){                        //刪除資料表,從新輸入
        db=dbOpenHelper.getWritableDatabase();
        dbOpenHelper.onUpgrade(db,1,1);
        dbOpenHelper=new DBOpenHelper(context);
        //Log.d("DBUSE","創建資料表");
    }

    public void UpdataYoubike(ArrayList<YouBike> youBikes){            //更新資料表
        db=dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+DBOpenHelper.YOUBIKE_TABLE,null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            ContentValues cv=new ContentValues();
            cv.put("sbi",youBikes.get(i).getSbi());
            cv.put("bemp",youBikes.get(i).getBemp());
            cv.put("mDay",youBikes.get(i).getMday());
            db.update(DBOpenHelper.YOUBIKE_TABLE,cv,"_id="+(i),null);
            cursor.moveToNext();
        }
        close();
    }



    public void close(){
        db.close();
        cursor.close();
    }





}
