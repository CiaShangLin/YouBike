package fcu.shang.youbike.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBOpenHelper extends SQLiteOpenHelper {

    public final static int _DBVersion = 1;
    public static String DBNAME="YOUBIKE.db";
    public static String YOUBIKE_TABLE="YOUBIKE";
    public static String id="_id";
    public static String sna="sna";
    public static String sbi="sbi";
    public static String bemp="bemp";
    public static String lat="lat";
    public static String lng="lng";
    public static String mDay="mDay";
    public static String city="city";
    public static String love="love";


    public DBOpenHelper(Context context) {
        super(context, DBNAME, null,_DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL="CREATE TABLE "+YOUBIKE_TABLE+" ("+
                id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                sna+" TEXT, "+
                sbi+" INTEGER, "+
                bemp+" INTEGER, "+
                lat+" REAL, "+
                lng+" REAL, "+
                mDay+" TEXT,"+
                city+" TEXT,"+
                love+" INTEGER"+
                ")";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        final String SQL = "DROP TABLE " + YOUBIKE_TABLE;
        db.execSQL(SQL);
        onCreate(db);
    }


}
