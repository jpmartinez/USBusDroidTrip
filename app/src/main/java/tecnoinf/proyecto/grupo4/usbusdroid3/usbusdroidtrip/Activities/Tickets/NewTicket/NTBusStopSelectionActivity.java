package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.NewTicket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.RouteStop;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class NTBusStopSelectionActivity extends AppCompatActivity {

    private String ticketPriceRest;
    private JSONObject journey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntbus_stop_selection);
        Intent father = getIntent();

        final String selectedSeat = father.getStringExtra("seat");

        try {
            journey = new JSONObject(father.getStringExtra("journey"));
            JSONArray routeStops = journey.getJSONObject("service").getJSONObject("route").getJSONArray("busStops");

            List<RouteStop> routeStopList = RouteStop.fromJson(routeStops);
            ArrayList<String> routeStopsNames = new ArrayList<>();
            for (RouteStop rs: routeStopList) {
                routeStopsNames.add(rs.getBusStop());
            }

            final Spinner spinnerFrom = (Spinner) findViewById(R.id.busStopFromSpn);
            ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this, R.layout.simple_usbus_spinner_item, routeStopsNames);
            assert spinnerFrom != null;
            spinnerFrom.setAdapter(fromAdapter);

            final Spinner spinnerTo = (Spinner) findViewById(R.id.busStopToSpn);
            ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this, R.layout.simple_usbus_spinner_item, routeStopsNames);
            assert spinnerTo != null;
            spinnerTo.setAdapter(toAdapter);
            spinnerTo.setSelection(routeStopsNames.size() - 1);

            Button submitBtn = (Button) findViewById(R.id.busStopConfirmBtn);
            assert submitBtn != null;
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String origin = spinnerFrom.getSelectedItem().toString();
                        String destination = spinnerTo.getSelectedItem().toString();

                        if(origin.equalsIgnoreCase(destination)) {
                            Toast.makeText(getBaseContext(), "Debe seleccionar paradas diferentes", Toast.LENGTH_LONG).show();
                        } else {

                            ticketPriceRest = getString(R.string.URLticketPrice,
                                    getString(R.string.URL_REST_API),
                                    getString(R.string.tenantId),
                                    journey.get("id").toString(),
                                    origin.replace(" ", "+"),
                                    destination.replace(" ", "+"));

                            AsyncTask<Void, Void, JSONObject> priceResult = new RestCallAsync(getApplicationContext(), ticketPriceRest, "GET", null).execute();
                            JSONObject priceData = priceResult.get();
                            Double ticketPriceDouble = new JSONObject(priceData.getString("data")).getDouble("price");
                            String ticketPrice = String.format("%.2f", ticketPriceDouble);

                            Intent confirmationIntent = new Intent(v.getContext(), NTConfirmationActivity.class);
                            confirmationIntent.putExtra("journey", journey.toString());
                            confirmationIntent.putExtra("ticketPrice", ticketPrice);
                            confirmationIntent.putExtra("origin", origin);
                            confirmationIntent.putExtra("destination", destination);
                            confirmationIntent.putExtra("seat", selectedSeat);
                            startActivity(confirmationIntent);
                        }
                    } catch (JSONException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
