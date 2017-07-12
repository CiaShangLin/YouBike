package fcu.shang.youbike.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import fcu.shang.youbike.R;


public class MainListAdapter extends BaseAdapter{

    String item[];
    Context context;
    LayoutInflater layoutInflater;

    public MainListAdapter(Context context){
        this.context=context;
        item=context.getResources().getStringArray(R.array.item);
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return item.length;
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
        convertView=layoutInflater.inflate(R.layout.mainlistlayout,parent,false);
        ImageView ig=(ImageView)convertView.findViewById(R.id.itemIg);
        TextView tv=(TextView)convertView.findViewById(R.id.itemTv);


        switch (position){
            case 0:
                ig.setImageDrawable(context.getResources().getDrawable(R.drawable.love));
                tv.setText(item[position]);
                break;
            case 1:
                ig.setImageDrawable(context.getResources().getDrawable(R.drawable.googlemap));
                tv.setText(item[position]);
                break;
            case 2:
                ig.setImageDrawable(context.getResources().getDrawable(R.drawable.list));
                tv.setText(item[position]);
                break;
            case 3:
                ig.setImageDrawable(context.getResources().getDrawable(R.drawable.maker));
                tv.setText(item[position]);
                break;
            case 4:
                ig.setImageDrawable(context.getResources().getDrawable(R.drawable.gmail));
                tv.setText(item[position]);
                break;
            case 5:
                ig.setImageDrawable(context.getResources().getDrawable(R.drawable.description));
                tv.setText(item[position]);
                break;
        }


        return convertView;
    }
}
