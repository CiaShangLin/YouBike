package fcu.shang.youbike.GoogleMap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import fcu.shang.youbike.DataBase.DBOpenHelper;
import fcu.shang.youbike.DataBase.DBUse;
import fcu.shang.youbike.ListActivity;
import fcu.shang.youbike.R;
import fcu.shang.youbike.Youbike.YouBike;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        ,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener
        ,DirectionFinderListener{

    private GoogleMap mMap;

    Intent intent;
    ArrayList<YouBike> youBikes=new ArrayList<>();

    private ImageButton navigateBt,backBt,myLocalBt;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    protected static final String TAG="MapsActivity";

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    protected static int Navigation=0;         //0=非導航 1=導航模式
    protected static double MyLat=0;
    protected static double MyLng=0;

    double lat=24.178808,lng=120.646797;
    DBUse dbUse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        init();
        inputYoubike();
    }

    public void inputYoubike(){
        dbUse=new DBUse(this);
        youBikes=dbUse.getAllYoubike();
    }


    private void init(){
        navigateBt=(ImageButton)findViewById(R.id.gmBt);
        navigateBt.setOnClickListener(BtListener);

        backBt=(ImageButton)findViewById(R.id.back);
        backBt.setOnClickListener(BackListener);

        myLocalBt=(ImageButton)findViewById(R.id.gmMyLocalion);
        myLocalBt.setOnClickListener(MyLocalListener);

        intent=getIntent();
        if(intent.getDoubleExtra(ListActivity.MyLat,0.0)!=0.0)
            lat=intent.getDoubleExtra(ListActivity.MyLat,0.0);
        if(intent.getDoubleExtra(ListActivity.MyLng,0.0)!=0.0)
            lng=intent.getDoubleExtra(ListActivity.MyLng,0.0);

        buildGoogleApiClient();           //打開GOOGLEMAP API 取得GPS
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("YoubikeSize",youBikes.size()+"");
        for(int i=0;i<youBikes.size();i++){               //匯入所有的youbike
            LatLng local=new LatLng(youBikes.get(i).getLat(),youBikes.get(i).getLng());
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(local)
                    .title(youBikes.get(i).getSna())
                    .snippet(getYoubikeInfo(youBikes.get(i)));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            mMap.addMarker(markerOptions);
        }

       // mMap.setMyLocationEnabled(true);                        //點下去移動我的位置  右上角的按鈕
        mMap.setInfoWindowAdapter(InfoWindowAdapter);          //marker點下去跑出資訊視窗
        mMap.setOnMarkerClickListener(markerClickListener);      //marker點下發生事件
        mMap.setOnInfoWindowClickListener(infoWindowClickListener);


        LatLng fcu=new LatLng(lat,lng);             //預設逢甲大學
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fcu,15));

    }

    View.OnClickListener BtListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Navigation=1;           //導航模式
            Toast.makeText(MapsActivity.this,"請點選一個站點",Toast.LENGTH_LONG).show();
        }
    };

    View.OnClickListener BackListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener MyLocalListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MyLat=mLocation.getLatitude();
            MyLng=mLocation.getLongitude();
            LatLng myLocal=new LatLng(MyLat,MyLng);             //自己的位置
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocal,17));

            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(myLocal);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            mMap.addMarker(markerOptions);
        }
    };

    public String getYoubikeInfo(YouBike youBike){            //infolayout要用的資料
        StringBuffer sb=new StringBuffer("");
        sb.append(youBike.getSbi()+"\n")
                .append(youBike.getBemp()+"\n")
                .append(youBike.getMday()+"\n");
        return sb.toString();
    }

    GoogleMap.OnMarkerClickListener markerClickListener=new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if(MapsActivity.Navigation==1){
                mGoogleApiClient.connect();    //可以取得最新經緯
                sendRequest(MyLat,MyLng,marker.getPosition().latitude,marker.getPosition().longitude);
                Log.d("YOUBIKE",marker.getPosition().latitude+" "+marker.getPosition().longitude);
                Navigation=0;
                return true;
            }else{
                return false;
            }
        }
    };

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener=new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            dbUse.JoinFavorite(marker.getTitle());
        }
    };

    GoogleMap.InfoWindowAdapter InfoWindowAdapter=new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {

            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {          //如果這個false 才會呼叫上面的
            View viwe=getLayoutInflater().inflate(R.layout.infolayout,null);

            TextView tv1=(TextView) viwe.findViewById(R.id.ybTv1);
            TextView tv2=(TextView) viwe.findViewById(R.id.ybTv2);
            TextView tv3=(TextView) viwe.findViewById(R.id.ybTv3);

            if(marker.getTitle()!=null){
                String youbikeTitle=marker.getTitle();
                String youbikeInfo[]=marker.getSnippet().split("\n");

                StringBuffer sb=new StringBuffer("");
                sb.append(youbikeInfo[2].substring(0,4)+"/")
                        .append(youbikeInfo[2].substring(4,6)+"/")
                        .append(youbikeInfo[2].substring(6,8)+"/")
                        .append(youbikeInfo[2].substring(8,10)+":")
                        .append(youbikeInfo[2].substring(10,12)+":")
                        .append(youbikeInfo[2].substring(12,14));

                tv1.setText("場站名稱 : "+youbikeTitle);
                tv2.setText("可借/可停 : "+youbikeInfo[0]+"/"+youbikeInfo[1]);
                tv3.setText("更新時間 : "+sb.toString());
            }else{
                tv1.setText("我的位置");
                tv2.setText("");
                tv3.setText("");
            }


            return viwe;

        }
    };

    private void sendRequest(double myLat,double myLng,double youbikeLat,double youbikeLng) {
        String origin="origin=" + myLat + "," +myLng;
        String destination="destination=" + youbikeLat + "," + youbikeLng;
        try {
            new DirectionFinder(MapsActivity.this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient=new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(MapsActivity.this)
                .addOnConnectionFailedListener(MapsActivity.this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {   //GoogleApiClient.ConnectionCallbacks介面
        mLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);  //取得目前最新位置
        if(mLocation!=null){
            MyLat=mLocation.getLatitude();
            MyLng=mLocation.getLongitude();
            Log.d(TAG,mLocation.getLatitude()+"");
            Log.d(TAG,mLocation.getLongitude()+"");
        }else{
            Toast.makeText(this,"GPS NOT OPEN",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route routes : route) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routes.startLocation, 16));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < routes.points.size(); i++)
                polylineOptions.add(routes.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}