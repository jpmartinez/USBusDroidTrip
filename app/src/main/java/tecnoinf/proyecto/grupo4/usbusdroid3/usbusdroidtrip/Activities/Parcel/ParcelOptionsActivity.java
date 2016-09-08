package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel;

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

import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.JourneyParcels.JourneyParcelsListActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.RouteStopParcels.RPRouteStopListActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class ParcelOptionsActivity extends AppCompatActivity {

    private String getParcelsREST;
    private String onCourseJourney;
    private String getJourneyREST;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_options);

        final SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");
        ImageButton fullListBt = (ImageButton) findViewById(R.id.parcelsListBtn);
        ImageButton stopListBt = (ImageButton) findViewById(R.id.parcelsByStopBtn);

        assert fullListBt != null;
        fullListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getParcelsREST = getString(R.string.URLgetJourneyParcels,
                            getString(R.string.URL_REST_API),
                            getString(R.string.tenantId),
                            "JOURNEY",
                            onCourseJourney);

                    AsyncTask<Void, Void, JSONObject> journeyParcelsResult = new RestCallAsync(getApplicationContext(), getParcelsREST, "GET", null).execute();
                    JSONObject journeyParcelsData = journeyParcelsResult.get();

                    if(!journeyParcelsData.getString("data").equalsIgnoreCase("204No Content")) {
                        Intent parcelsListIntent = new Intent(getBaseContext(), JourneyParcelsListActivity.class);
                        parcelsListIntent.putExtra("parcels", journeyParcelsData.getString("data"));
                        startActivity(parcelsListIntent);
                    } else {
                        Toast.makeText(getBaseContext(), "No hay encomiendas para este viaje", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        assert stopListBt != null;
        stopListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: display list of routestops (including journey destination)
                //TODO: display list of parcels by routestop (parcel.destination)
                //TODO: allow to click parcel to mark it as unloaded from bus
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

                        editor.apply();

                        Intent routeStopIntent = new Intent(getApplicationContext(), RPRouteStopListActivity.class);
                        startActivity(routeStopIntent);

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
