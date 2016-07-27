package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kavesa on 31/05/16.
 */
public class RouteStop {
    private String busStop;
    private Double km;
    private boolean combinationPoint;
    private String status;
    private Double latitude;
    private Double longitude;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusStop() {
        return busStop;
    }

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    public boolean isCombinationPoint() {
        return combinationPoint;
    }

    public void setCombinationPoint(boolean combinationPoint) {
        this.combinationPoint = combinationPoint;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public RouteStop(JSONObject object){
        try {
            busStop = object.getString("busStop");
            km = object.getDouble("km");
            combinationPoint = object.getBoolean("combinationPoint");
            latitude = object.getDouble("latitude");
            longitude = object.getDouble("longitude");
            if(object.has("status")) {
                status = object.getString("status");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<RouteStop> fromJson(JSONArray jsonObjects) {
        ArrayList<RouteStop> routeStops = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                routeStops.add(new RouteStop(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return routeStops;
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("busStop", busStop);
        obj.put("km", km);
        obj.put("combinationPoint", combinationPoint);
        obj.put("status", status);
        obj.put("latitude", latitude);
        obj.put("longitude", longitude);
        return obj;
    }
}
