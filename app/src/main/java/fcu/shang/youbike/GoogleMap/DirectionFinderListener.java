package fcu.shang.youbike.GoogleMap;

import java.util.List;

/**
 * Created by Shang on 2017/3/11.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
