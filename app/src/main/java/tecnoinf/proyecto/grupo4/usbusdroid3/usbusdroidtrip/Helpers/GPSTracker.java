package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.RouteStop.RouteStopListActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.RouteStop;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

import static android.location.Location.distanceBetween;
import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by Kavesa on 17/07/16.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    private ArrayList<RouteStop> routeStops;
    private JSONArray routeStopsJSON;
    private Double routeStopMinDistance = 5.0;
    private final int routeStopNotificationId = 142;
    private String onCourseJourney;
    private Integer standingCurrent=0;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 metros

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30; // 30 segundos

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) throws JSONException {
        this.mContext = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        routeStopsJSON = new JSONArray(sharedPreferences.getString("routeStops", ""));
        routeStops = RouteStop.fromJson(routeStopsJSON);
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                //TODO: agregar message para que habilite gps
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null; //TODO
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    @Override
    public void onLocationChanged(Location location) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        double prevLat = Double.parseDouble(sharedPreferences.getString("prevLat", "0.0"));
        double prevLng = Double.parseDouble(sharedPreferences.getString("prevLng", "0.0"));

        System.out.println("prevLat:"+prevLat);
        System.out.println("prevLng:"+prevLng);

        //double distance = distance(prevLat, prevLng, location.getLatitude(), location.getLongitude());
        float[] distance = new float[1];

        System.out.println("min distance: " + String.valueOf(routeStopMinDistance));
        for (RouteStop rs : routeStops) {
            //System.out.println("=)=)=)=)=)=)=)=)rs: "+rs.getBusStop()+rs.getStatus()+rs.getLatitude());
            if(rs.getStatus().equalsIgnoreCase("PENDIENTE")) {
                distanceBetween(rs.getLatitude(), rs.getLongitude(), location.getLatitude(), location.getLongitude(), distance);

                System.out.println(rs.getBusStop() + "Lat|Lng: " + rs.getLatitude() + "|" + rs.getLongitude());
                System.out.println("location Lat|Lng: " + location.getLatitude() + "|" + location.getLongitude());
                System.out.println("distance to " + rs.getBusStop() + ": " + String.valueOf(distance[0]/1000));

                if (distance[0]/1000 <= routeStopMinDistance) {
                    System.out.println("arribando a: " + rs.getBusStop());

                    //TODO: Llamar a los métodos de RouteStopListActivity.java:124-170 para actualizar el seatstate
                    //TODO: No haría falta con el nuevo método de occupiedSeats ya implementado en cliente (pasarlo a trip)

                    rs.setStatus("ARRIBADO");

                    // =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
                    standingCurrent = sharedPreferences.getInt("standingCurrent", 0);
                    //Integer standingTotal = sharedPreferences.getInt("standingTotal", 0);

                    try {
                        String ticketsREST = mContext.getString(R.string.URLupdateTickets,
                                mContext.getString(R.string.URL_REST_API),
                                mContext.getString(R.string.tenantId),
                                "ROUTESTOP",
                                onCourseJourney,
                                rs.getBusStop().replace(" ", "+"));

                        AsyncTask<Void, Void, JSONObject> updTicketsResult = new RestCallAsync(mContext, ticketsREST, "GET", null).execute();
                        JSONObject updTicketsData = updTicketsResult.get();
                        JSONArray ticketsArray = new JSONArray(updTicketsData.getString("data"));

                        for (int k = 0; k < ticketsArray.length(); k++) {
                            if (ticketsArray.getJSONObject(k).getJSONObject("getsOff").getString("name")
                                    .equalsIgnoreCase(rs.getBusStop())) {
                                if( ticketsArray.getJSONObject(k).getInt("seat") == 999) {
                                    standingCurrent--;
                                }
                            }
                        }

                        editor.putInt("standingCurrent", standingCurrent);
                        editor.apply();

                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    // =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+

                    //NOTIFICATION
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(mContext)
                                    .setSmallIcon(R.drawable.bus_stop_sign)
                                    .setWhen(System.currentTimeMillis())
                                    .setTicker("Llegando a " + rs.getBusStop() + "!!!")
                                    .setContentTitle("Próxima Parada a 5Km !")
                                    .setContentText("Arribando a " + rs.getBusStop())
                                    .setAutoCancel(true);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(mContext, RouteStopListActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(RouteStopListActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(routeStopNotificationId, mBuilder.build());

                    //TODO: guardar las routeStops con status actualizado en sharedPreferences, para verlas bien en RouteStopListActivity
                    //TODO: verificar que en RouteStopListActivity se esten tomando de las shared preferences (o tomarlas y si es "" usar las del journey)

                    try {
                        JSONArray jsonArray = new JSONArray();
                        //jsonArray.put(routeStops.get(0).getJSONObject());
                        for (int i=0; i < routeStops.size(); i++) {
                            jsonArray.put(routeStops.get(i).getJSONObject());
                        }

                        editor.putString("routeStops", jsonArray.toString());
                        editor.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }

//        distanceBetween(prevLat, prevLng, location.getLatitude(), location.getLongitude(), distance);
//        Toast.makeText(mContext, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude + "\ndist: " + distance[0]/1000, Toast.LENGTH_LONG).show();
//
//        editor.putString("prevLat", String.valueOf(location.getLatitude()));
//        editor.putString("prevLng", String.valueOf(location.getLongitude()));
//        editor.apply();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometers

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }
}