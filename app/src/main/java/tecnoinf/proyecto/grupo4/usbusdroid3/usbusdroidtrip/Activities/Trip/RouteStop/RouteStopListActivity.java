package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.RouteStop;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.MainActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.TicketOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.RouteStop;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class RouteStopListActivity extends ListActivity {

    private JSONArray routeStops;
    private JSONArray ticketsArray;
    private JSONObject journey;
    private ArrayList<HashMap<String, String>> routeStopsMap;
    private ListAdapter adapter;
    private SharedPreferences.Editor editor;
    private String updateJourneyREST;
    private String onCourseJourney;
    private String selectedBusStop;
    private Integer standingCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routestop_list);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");
        standingCurrent = sharedPreferences.getInt("standingCurrent", 0);

        try {
            journey = new JSONObject(sharedPreferences.getString("journey", ""));
            ticketsArray = new JSONArray(sharedPreferences.getString("ticketsArray", ""));

            routeStops = new JSONArray(sharedPreferences.getString("routeStops", ""));
            Log.d("routeStops: ", routeStops.toString());
            final List<RouteStop> routeStopsList = RouteStop.fromJson(routeStops);
            //routeStopsList.remove(0);

            routeStopsMap = new ArrayList<>();

            for (RouteStop rs2 : routeStopsList) {
                HashMap<String, String> j = new HashMap<>();
                j.put("name", rs2.getBusStop());
                j.put("status", rs2.getStatus());
                routeStopsMap.add(j);
            }

            adapter = new SimpleAdapter(
                    getApplicationContext(),
                    routeStopsMap,
                    R.layout.activity_routestop_list_item,
                    new String[] { "name", "status" },
                    new int[] { R.id.routeStopNameTV, R.id.routeStopStatusTV })
                {
                    @Override
                    public View getView (int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        if(((TextView)view.findViewById(R.id.routeStopStatusTV)).getText().toString().equalsIgnoreCase("ARRIBADO")) {
                            view.findViewById(R.id.routeStopCheckIV).setVisibility(View.VISIBLE);
                            view.setLongClickable(false);
                            view.setEnabled(false);
                        }
                        return view;
                    }

                    @Override
                    public boolean areAllItemsEnabled() {
                        return false;
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        return !((TextView) this.getView(position, null, null).findViewById(R.id.routeStopStatusTV))
                                .getText().toString().equalsIgnoreCase("ARRIBADO");
                    }
                };

            setListAdapter(adapter);

            ListView lv = getListView();
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String previousStatus = "";
                    View previousStop = parent.getChildAt(position-1);
                    if (previousStop != null) {
                        TextView previousStopTV = (TextView) previousStop.findViewById(R.id.routeStopStatusTV);
                        previousStatus = previousStopTV.getText().toString();
                    }
                    if (previousStatus.equalsIgnoreCase("PENDIENTE")) {
                        Toast.makeText(getBaseContext(), "Debe seleccionar las paradas en orden", Toast.LENGTH_LONG).show();
                    } else {
                        TextView busStopNameTV = (TextView) view.findViewById(R.id.routeStopNameTV);
                        selectedBusStop = busStopNameTV.getText().toString();

                        TextView statusTV = (TextView) view.findViewById(R.id.routeStopStatusTV);
                        statusTV.setText("ARRIBADO");
                        ImageView checkIV = (ImageView) view.findViewById(R.id.routeStopCheckIV);
                        checkIV.setVisibility(View.VISIBLE);
                        try {
                            routeStops.getJSONObject(position+1).put("status", "ARRIBADO");
                            editor.putString("routeStops", routeStops.toString());
                            editor.apply();

                            updateJourneyREST = getString(R.string.URLupdateJourney,
                                    getString(R.string.URL_REST_API),
                                    getString(R.string.tenantId),
                                    onCourseJourney);

                            JSONArray updatedSeatState;
                            if (!journey.isNull("seatsState")) {
                                updatedSeatState = new JSONArray(journey.get("seatsState").toString());
                            } else {
                                updatedSeatState = new JSONArray();
                            }

                            //Tomo solo los tickets que NO terminan en esta parada (para ser enviados al journey REST)
                            for (int k = 0; k < ticketsArray.length(); k++) {
                                if (ticketsArray.getJSONObject(k).getJSONObject("getsOff").getString("name")
                                        .equalsIgnoreCase(selectedBusStop)) {
                                    if( ticketsArray.getJSONObject(k).getInt("seat") == 999) {
                                        standingCurrent--;
                                    } else {
                                        for (int j = 0; j < updatedSeatState.length(); j++) {
                                            if (updatedSeatState.getJSONObject(j).getInt("number")
                                                    == ticketsArray.getJSONObject(k).getInt("seat")) {
                                                updatedSeatState.remove(j);
                                            }
                                        }
                                    }
                                }
                            }

                            editor.putInt("standingCurrent", standingCurrent);
                            editor.apply();

                            JSONObject patchData = new JSONObject();
                            patchData.put("seatsState", updatedSeatState);
                            AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), updateJourneyREST, "PATCH", patchData).execute();
                            JSONObject journeyData = journeyResult.get();

                            System.out.println(journeyData);

                        } catch (JSONException | ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
