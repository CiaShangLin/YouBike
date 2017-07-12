package fcu.shang.youbike;


import java.util.ArrayList;
import fcu.shang.youbike.Youbike.YouBike;

/**
 * Created by Shang on 2017/3/18.
 */
public interface FunctionListener {
    void setYouBikeCity(ArrayList<YouBike> youbike);
    void setWeater(String result);
    void setPM(String result);
}
