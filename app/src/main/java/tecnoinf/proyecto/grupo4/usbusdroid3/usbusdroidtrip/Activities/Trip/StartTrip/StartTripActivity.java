package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.StartTrip;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.DayConverter_ES;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.JourneyShort;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.JourneyStatus;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class StartTripActivity extends ListActivity {

    private JSONArray journeysJsonArray;
    private String startJourneyREST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        Intent father = getIntent();
        try {
            journeysJsonArray = new JSONArray(father.getStringExtra("journeys"));

            final List<JourneyShort> journeyList = JourneyShort.fromJson(journeysJsonArray);

            ArrayList<HashMap<String, String>> journeyMap = new ArrayList<>();

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setTimeZone(TimeZone.getTimeZone("America/Montevideo"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Montevideo"));

            for (JourneyShort js2 : journeyList) {
                HashMap<String, String> j = new HashMap<>();
                j.put("id", js2.getId().toString());
                j.put("name", js2.getName());
                j.put("day", DayConverter_ES.convertES(js2.getDay()));
                j.put("date", dateFormat.format(js2.getDate()));
                j.put("time", timeFormat.format(js2.getTime()));
                j.put("busNumber", js2.getBusNumber().toString());

                journeyMap.add(j);
            }

            ListAdapter adapter = new SimpleAdapter(
                    getApplicationContext(),
                    journeyMap,
                    R.layout.activity_start_trip_list_item,
                    new String[] { "id", "name", "day", "date", "time", "busNumber" },
                    new int[] { R.id.id, R.id.journeyNameTV, R.id.journeyDayTV, R.id.journeyDateTV, R.id.journeyTimeTV, R.id.busNumberTV });
            setListAdapter(adapter);

            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    try {
                        String journeyId = ((TextView) view.findViewById(R.id.id)).getText().toString();

                        startJourneyREST = getString(R.string.URLstartJourney,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                journeyId);

                        JSONObject journey = journeysJsonArray.getJSONObject(position);
                        journey.put("status", JourneyStatus.LEFT);

                        AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), startJourneyREST, "PUT", journey).execute();
                        Intent resultIntent = new Intent(getBaseContext(), STResultActivity.class);
                        resultIntent.putExtra("journey", journeysJsonArray.get(position).toString());
                        startActivity(resultIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

//        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
//        tenantId = sharedPreferences.getString("tenantId", "");





    }
}
