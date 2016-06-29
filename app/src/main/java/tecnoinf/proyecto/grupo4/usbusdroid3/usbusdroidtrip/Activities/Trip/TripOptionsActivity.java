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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.MainActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.CloseTrip.CTConfirmationActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.Odometer.SetOdometerActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.StartTrip.StartTripActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.JourneyStatus;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class TripOptionsActivity extends AppCompatActivity {

    private String onCourseJourney;
    private Boolean odometerSet;
    private String journeysREST;
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

        ImageButton startTripBt = (ImageButton) findViewById(R.id.startTripBtn);
        ImageButton endTripBt = (ImageButton) findViewById(R.id.endTripBtn);
        ImageButton odometerBt = (ImageButton) findViewById(R.id.odometerBtn);

        assert startTripBt != null;
        startTripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    date_today = dateFormat.format(new Date());

                    System.out.println(date_today);
                    journeysREST = getString(R.string.URLjourneys,
                            getString(R.string.URL_REST_API),
                            getString(R.string.tenantId),
                            "DATE_STATUS",
                            date_today,
                            JourneyStatus.ACTIVE);

                    AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), journeysREST, "GET", null).execute();
                    try {
                        JSONObject journeyData = journeyResult.get();


                        Intent startTripIntent = new Intent(getApplicationContext(), StartTripActivity.class);
                        startTripIntent.putExtra("journeys", journeyData.get("data").toString());
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
    }
}