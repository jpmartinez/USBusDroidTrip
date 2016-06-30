package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.RouteStop;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.RouteStop;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class RouteStopListActivity extends ListActivity {

    private JSONArray routeStops;
    private ArrayList<HashMap<String, String>> routeStopsMap;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routestop_list);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        try {
            routeStops = new JSONArray(sharedPreferences.getString("routeStops", ""));
            final List<RouteStop> routeStopsList = RouteStop.fromJson(routeStops);

            routeStopsMap = new ArrayList<>();

            for (RouteStop rs2 : routeStopsList) {
                HashMap<String, String> j = new HashMap<>();
                j.put("name", rs2.getBusStop());
                if(rs2.getStatus() == null || rs2.getStatus().isEmpty()) {
                    j.put("status", "PENDIENTE");
                } else {
                    j.put("status", rs2.getStatus());
                }
                routeStopsMap.add(j);
            }

            adapter = new SimpleAdapter(
                    getApplicationContext(),
                    routeStopsMap,
                    R.layout.activity_routestop_list_item,
                    new String[] { "name", "status" },
                    new int[] { R.id.routeStopNameTV, R.id.routeStopStatusTV });
            setListAdapter(adapter);

            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO: confirmar en popup?
                    //TODO: cambiar color del imageView bus_stop_sign a GRIS
                    HashMap<String, String> t = routeStopsMap.get(position);
                    t.put("status", "ARRIBADO");
                    routeStopsMap.set(position, t);
                    adapter.notifyDataSetChanged();
                }
            });

//            List<RouteStop> routeStopList = RouteStop.fromJson(routeStops);
//            ArrayList<String> routeStopsNames = new ArrayList<>();
//            for (RouteStop rs: routeStopList) {
//                routeStopsNames.add(rs.getBusStop());
//            }
//
//            final Spinner spinnerStops = (Spinner) findViewById(R.id.routeStopSetSpn);
//            ArrayAdapter<String> stopsAdapter = new ArrayAdapter<>(this, R.layout.simple_usbus_spinner_item, routeStopsNames);
//            assert spinnerStops != null;
//            spinnerStops.setAdapter(stopsAdapter);
//
//            spinnerStops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getBaseContext(), spinnerStops.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
