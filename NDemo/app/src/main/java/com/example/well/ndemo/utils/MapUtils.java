package com.example.well.ndemo.utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;
import com.example.well.ndemo.bean.NodemoMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${LuoChen} on 2017/4/24 10:19.
 * email:luochen0519@foxmail.com
 */

public class MapUtils {
    /**
     * 将AMapLocation List 转为TraceLocation list
     *
     * @param list
     * @return
     */
    public static List<TraceLocation> parseTraceLocationList(
            List<NodemoMapLocation> list) {
        List<TraceLocation> traceList = new ArrayList<TraceLocation>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0; i < list.size(); i++) {
            TraceLocation location = new TraceLocation();
            NodemoMapLocation amapLocation = list.get(i);
            location.setBearing(amapLocation.getBearing());
            location.setLatitude(amapLocation.getLatitude());
            location.setLongitude(amapLocation.getLongitude());
            location.setSpeed(amapLocation.getTotalSpeed());
            location.setTime(amapLocation.getTime());
            traceList.add(location);
        }
        return traceList;
    }

    public static TraceLocation parseTraceLocation(AMapLocation amapLocation) {
        TraceLocation location = new TraceLocation();
        location.setBearing(amapLocation.getBearing());
        location.setLatitude(amapLocation.getLatitude());
        location.setLongitude(amapLocation.getLongitude());
        location.setSpeed(amapLocation.getSpeed());
        location.setTime(amapLocation.getTime());
        return  location;
    }

    /**
     * 将AMapLocation List 转为LatLng list
     * @param list
     * @return
     */
    public static List<LatLng> parseLatLngList(List<NodemoMapLocation> list) {
        List<LatLng> traceList = new ArrayList<LatLng>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0; i < list.size(); i++) {
            NodemoMapLocation loc = list.get(i);
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            LatLng latlng = new LatLng(lat, lng);
            traceList.add(latlng);
        }
        return traceList;
    }

    public static NodemoMapLocation parseLocation(String latLonStr) {
        if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
            return null;
        }
        String[] loc = latLonStr.split(",");
        NodemoMapLocation location = null;
        if (loc.length == 7) {
            location = new NodemoMapLocation();
            location.setProvider(loc[2]);
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
            location.setTime(Long.parseLong(loc[3]));
            location.setTotalSpeed(Float.parseFloat(loc[4]));
            location.setBearing(Float.parseFloat(loc[5]));
            location.setStreet(loc[6]);
        }else if(loc.length == 2){
            location = new NodemoMapLocation();
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
        }

        return location;
    }

    public static ArrayList<NodemoMapLocation> parseLocations(String latLonStr) {
        ArrayList<NodemoMapLocation> locations = new ArrayList<NodemoMapLocation>();
        String[] latLonStrs = latLonStr.split(";");
        for (int i = 0; i < latLonStrs.length; i++) {
            NodemoMapLocation location = MapUtils.parseLocation(latLonStrs[i]);
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    public static List<Float> parseSpeedList(String speedList) {
        ArrayList<Float> speed = new ArrayList<Float>();
        String[] speedArray = speedList.split(";");
        for (int i = 0; i < speedArray.length; i++) {
            speed.add(Float.parseFloat(speedArray[i]));
        }
        return speed;
    }
}
