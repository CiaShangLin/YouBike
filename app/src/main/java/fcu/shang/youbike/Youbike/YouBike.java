package fcu.shang.youbike.Youbike;

import java.io.Serializable;

public class YouBike{

    private int sno;               //sno:站點代號
    private String sna;           //sna:場站名稱
    private String sarea;          //sarea:場站區域(中文)
    private String ar;            //ar:地址(中文)
    private int tot;                //tot:場站總停車格
    private int sbi;                //sbi:可借車位數
    private int bemp;             //bemp:可還空位數
    private double lat;             //lat:緯度
    private double lng;            //lng:經度
    private String mday;          //mday:資料更新時間
    private String city="";
    private int love=0;

    public void setSna(String sna) {
        this.sna = sna;
    }
    public void setSbi(int sbi) {
        this.sbi = sbi;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public void setBemp(int bemp) {
        this.bemp = bemp;
    }
    public void setMday(String mday) {
        this.mday = mday;
    }
    public void setCity(String city){
        this.city=city;
    }
    public void setLove(int love){
        this.love=love;
    }

    public String getSna() {
        return sna;
    }
    public int getSbi() {
        return sbi;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public int getBemp() {
        return bemp;
    }
    public String getMday() {
        return mday;
    }
    public String getCity(){
        return city;
    }
    public int getLove(){
        return love;
    }

}
