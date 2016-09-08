package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.JourneyParcels;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Dimension;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Parcel;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.RouteStop;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class JourneyParcelsListActivity extends ListActivity {

//    private JSONArray routeStops;
//    private JSONArray ticketsArray;
//    private JSONObject journey;
    private ArrayList<HashMap<String, String>> parcelsMap;
    private ListAdapter adapter;
//    private String updateJourneyREST;
//    private String onCourseJourney;
//    private String selectedBusStop;
//    private Integer standingCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_parcels_list);

        Intent father = getIntent();
        try {
            JSONArray parcelsArray = new JSONArray(father.getStringExtra("parcels"));

            final List<Parcel> parcelList = Parcel.fromJson(parcelsArray);
            //routeStopsList.remove(0);

            parcelsMap = new ArrayList<>();

            for (Parcel pcl : parcelList) {
                Dimension dim = pcl.getDimensions();
                HashMap<String, String> p = new HashMap<>();
                p.put("id", pcl.getId().toString());
                p.put("dimensions", dim.toString());
                p.put("weight", pcl.getWeight().toString());
                parcelsMap.add(p);
            }

            adapter = new SimpleAdapter(
                    getApplicationContext(),
                    parcelsMap,
                    R.layout.activity_journey_parcels_list_item,
                    new String[] { "id", "dimensions", "weight" },
                    new int[] { R.id.parcelIdTV, R.id.dimensionTV, R.id.weightTV })
            {
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    return view;
                }

                @Override
                public boolean areAllItemsEnabled() {
                    return false;
                }

                @Override
                public boolean isEnabled(int position) {
                    return false;
                }
            };

            setListAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
