package fcu.shang.youbike.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;

import java.util.ArrayList;

import fcu.shang.youbike.DataBase.DBOpenHelper;
import fcu.shang.youbike.DataBase.DBUse;
import fcu.shang.youbike.R;
import fcu.shang.youbike.Youbike.YouBike;

/**
 * Created by Shang on 2017/6/28.
 */

public class MyAdapterItem extends BaseAdapter{

    LayoutInflater mLayInf;
    ArrayList<YouBike> youBikes;
    Context context;
    DBUse dbUse;


    class ViewHolder{
        TextView list_head,list_sbi,list_bemp,list_time;
        ImageButton imageButton;
    }

    public MyAdapterItem(Context context,ArrayList<YouBike> youBikes){
        this.context=context;
        this.youBikes=youBikes;
        mLayInf=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        dbUse=new DBUse(context);
    }

    @Override
    public int getCount() {
        return youBikes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        convertView=mLayInf.inflate(R.layout.itemlayout,parent,false);
        viewHolder=new ViewHolder();
        viewHolder.list_head=(TextView)convertView.findViewById(R.id.list_head);
        viewHolder.list_sbi=(TextView)convertView.findViewById(R.id.list_sbi);
        viewHolder.list_bemp=(TextView)convertView.findViewById(R.id.list_bemp);
        viewHolder.list_time=(TextView)convertView.findViewById(R.id.list_time);
        viewHolder.imageButton=(ImageButton)convertView.findViewById(R.id.imageButton);


        final YouBike youBike=youBikes.get(position);
        viewHolder.list_head.setText("站點:"+youBike.getSna());
        viewHolder.list_sbi.setText("可停:"+String.valueOf(youBike.getSbi())+"/");
        viewHolder.list_bemp.setText("可還:"+String.valueOf(youBike.getBemp()));

        StringBuffer sb=new StringBuffer("");
        sb.append(youBike.getMday().substring(0,4)+"/")
                .append(youBike.getMday().substring(4,6)+"/")
                .append(youBike.getMday().substring(6,8)+"/")
                .append(youBike.getMday().substring(8,10)+":")
                .append(youBike.getMday().substring(10,12)+":")
                .append(youBike.getMday().substring(12,14));
        viewHolder.list_time.setText("更新日期:"+sb.toString());

        if(youBike.getLove()==0){                 //設置圖片
            viewHolder.imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.none));
        }else{
            viewHolder.imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
        }

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {          //加入最愛
            @Override
            public void onClick(View v) {
                if(youBike.getLove()==0){
                    viewHolder.imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
                    dbUse.JoinFavorite(youBike);
                }else{
                    viewHolder.imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.none));
                    dbUse.Cancel(youBike);
                }
            }
        });

        return convertView;
    }
}
