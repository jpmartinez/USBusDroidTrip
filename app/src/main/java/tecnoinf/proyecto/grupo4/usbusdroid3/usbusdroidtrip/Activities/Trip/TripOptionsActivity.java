package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.CloseTrip.CTConfirmationActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.Odometer.SetOdometerActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.RouteStop.RouteStopListActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.StartTrip.StartTripActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.JourneyStatus;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class TripOptionsActivity extends AppCompatActivity {

    private String onCourseJourney;
    private String username;
    private Boolean odometerSet;
    private String journeysREST;
    private String getJourneyREST;
    private String ticketsREST;
    private String date_today;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_options);

        final SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");
        odometerSet = sharedPreferences.getBoolean("odometerSet", false);
        username = sharedPreferences.getString("username", "");

        ImageButton startTripBt = (ImageButton) findViewById(R.id.startTripBtn);
        ImageButton endTripBt = (ImageButton) findViewById(R.id.endTripBtn);
        ImageButton odometerBt = (ImageButton) findViewById(R.id.odometerBtn);
        ImageButton routeStopBt = (ImageButton) findViewById(R.id.routeStopBtn);

        assert startTripBt != null;
        startTripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    date_today = dateFormat.format(new Date());
                    journeysREST = getString(R.string.URLjourneys,
                            getString(R.string.URL_REST_API),
                            getString(R.string.tenantId),
                            "USERNAME",
                            username);

                    AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), journeysREST, "GET", null).execute();
                    try {
                        JSONObject journeyData = journeyResult.get();
                        JSONObject journeysObject = new JSONObject(journeyData.get("data").toString());

                        Intent startTripIntent = new Intent(getApplicationContext(), StartTripActivity.class);
                        startTripIntent.putExtra("journeys", journeysObject.get("asAssistant").toString());
                        startActivity(startTripIntent);

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                } else if (onCourseJourney != null && !onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.journey_already_open, Toast.LENGTH_LONG).show();
                }
            }
        });

        assert endTripBt != null;
        endTripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else if (odometerSet) {
                    Intent confirmationIntent = new Intent(getApplicationContext(), CTConfirmationActivity.class);
                    startActivity(confirmationIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.odometer_not_set, Toast.LENGTH_LONG).show();
                }
            }
        });

        assert odometerBt != null;
        odometerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else {
                    Intent setOdometerIntent = new Intent(getApplicationContext(), SetOdometerActivity.class);
                    startActivity(setOdometerIntent);
                }
            }
        });

        assert routeStopBt != null;
        routeStopBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        getJourneyREST = getString(R.string.URLgetJourney,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                onCourseJourney);

                        AsyncTask<Void, Void, JSONObject> thisJourneyResult = new RestCallAsync(getApplicationContext(), getJourneyREST, "GET", null).execute();
                        JSONObject thisJourneyData = thisJourneyResult.get();
                        editor.putString("journey", thisJourneyData.get("data").toString());

                        ticketsREST = getString(R.string.URLgetTickets,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                "JOURNEY",
                                onCourseJourney,
                                "INUSE");

                        AsyncTask<Void, Void, JSONObject> ticketsResult = new RestCallAsync(getApplicationContext(), ticketsREST, "GET", null).execute();
                        JSONObject ticketsData = ticketsResult.get();
                        JSONArray ticketsArray = new JSONArray(ticketsData.get("data").toString());

                        for(int i = 0; i < ticketsArray.length(); i++) {
                            if (ticketsArray.getJSONObject(i).getString("status").equalsIgnoreCase("CANCELED")) {
                                ticketsArray.remove(i);
                                i--;
                            }
                        }

                        if (ticketsArray.length() > 0) {
                            editor.putString("ticketsArray", ticketsArray.toString());
                        } else {
                            editor.putString("ticketsArray", "[]");
                        }



                        editor.apply();

                        Intent routeStopIntent = new Intent(getApplicationContext(), RouteStopListActivity.class);
                        startActivity(routeStopIntent);

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
