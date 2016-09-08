package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.RouteStopParcels;

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
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.RouteStop;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class RPRouteStopListActivity extends ListActivity {

    private JSONArray routeStops;
    private JSONObject journey;
    private ArrayList<HashMap<String, String>> routeStopsMap;
    private ListAdapter adapter;
    private String onCourseJourney;
    private String selectedBusStop;
    private String getParcelsREST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routestop_list);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);

        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");

        try {
            journey = new JSONObject(sharedPreferences.getString("journey", ""));

            routeStops = new JSONArray(sharedPreferences.getString("routeStops", ""));
            Log.d("routeStops: ", routeStops.toString());
            final List<RouteStop> routeStopsList = RouteStop.fromJson(routeStops);

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

                        if(((TextView)view.findViewById(R.id.routeStopStatusTV)).getText().toString().equalsIgnoreCase("ARRIBADO") ||
                                ((TextView)view.findViewById(R.id.routeStopStatusTV)).getText().toString().equalsIgnoreCase("PARTIÓ")) {
                            view.findViewById(R.id.routeStopCheckIV).setVisibility(View.VISIBLE);
                            view.setLongClickable(false);
                            view.setEnabled(false);
                        }
                        return view;
                    }

//                    @Override
//                    public boolean areAllItemsEnabled() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean isEnabled(int position) {
//                        return !(((TextView) this.getView(position, null, null).findViewById(R.id.routeStopStatusTV))
//                                .getText().toString().equalsIgnoreCase("ARRIBADO")
//                        || ((TextView) this.getView(position, null, null).findViewById(R.id.routeStopStatusTV))
//                                .getText().toString().equalsIgnoreCase("PARTIÓ"));
//                    }
                };

            setListAdapter(adapter);

            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        TextView busStopNameTV = (TextView) v.findViewById(R.id.routeStopNameTV);
                        selectedBusStop = busStopNameTV.getText().toString();

                        getParcelsREST = getString(R.string.URLgetJourneyParcels,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                "JOURNEY",
                                onCourseJourney);

                        AsyncTask<Void, Void, JSONObject> journeyParcelsResult = new RestCallAsync(getApplicationContext(), getParcelsREST, "GET", null).execute();
                        JSONObject journeyParcelsData = journeyParcelsResult.get();

                        JSONArray journeyParcels = new JSONArray(journeyParcelsData.getString("data"));
                        JSONObject parcel;
                        JSONArray routeStopParcels = new JSONArray();
                        for (int i = 0; i < journeyParcels.length(); i++) {
                            parcel = journeyParcels.getJSONObject(i);
                            if (parcel.getJSONObject("destination").getString("name").equalsIgnoreCase(selectedBusStop)) {
                                routeStopParcels.put(parcel);
                            }
                        }

                        if(routeStopParcels.length() > 0) {
                            Intent rpParcelsIntent = new Intent(getBaseContext(), RPParcelListActivity.class);
                            rpParcelsIntent.putExtra("parcels", routeStopParcels.toString());
                            startActivity(rpParcelsIntent);
                        } else {
                            Toast.makeText(getBaseContext(), "No hay encomiendas para esta parada", Toast.LENGTH_LONG).show();
                        }

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//    }
}
