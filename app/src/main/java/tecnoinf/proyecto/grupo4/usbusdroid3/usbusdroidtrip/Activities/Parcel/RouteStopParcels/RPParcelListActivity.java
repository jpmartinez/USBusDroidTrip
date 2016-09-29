package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.RouteStopParcels;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Dimension;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Parcel;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class RPParcelListActivity extends ListActivity {

    private ArrayList<HashMap<String, String>> parcelsMap;
    private ListAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpparcel_list);
        Button unloadAll = (Button) findViewById(R.id.parcelUnloadAllBtn);

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
                p.put("ondestination", pcl.getOnDestination().toString());
                parcelsMap.add(p);
                System.out.println("id:"+pcl.getId().toString()+" "+pcl.getOnDestination());
            }

            adapter = new SimpleAdapter(
                    getApplicationContext(),
                    parcelsMap,
                    R.layout.activity_rpparcel_list_item,
                    new String[] { "id", "dimensions", "weight", "ondestination" },
                    new int[] { R.id.rpparcelIdTV, R.id.rpdimensionTV, R.id.rpweightTV, R.id.rpOnDestinationTV })
                {
                    @Override
                    public View getView (int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        Boolean unloaded = parcelList.get(position).getOnDestination();
                        if(unloaded){
                            view.findViewById(R.id.parcelUnloadCheckIV).setVisibility(View.VISIBLE);
                            view.setLongClickable(false);
                            view.setClickable(false);
                            view.setEnabled(false);
                        } else {
                            view.findViewById(R.id.parcelUnloadCheckIV).setVisibility(View.GONE);
                        }


                        return view;
                    }

                    @Override
                    public boolean areAllItemsEnabled() {
                        return false;
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        return !parcelList.get(position).getOnDestination();
                    }
                };

            setListAdapter(adapter);

            lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        //System.out.println(position);
                        //TODO: call rest to mark parcel as unloaded (or on destination)
                        String parcelId = ((TextView)(parent.getChildAt(position)).findViewById(R.id.rpparcelIdTV)).getText().toString();
                        String parcelURL = getString(R.string.URLgetParcel,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                parcelId);

                        AsyncTask<Void, Void, JSONObject> parcelResult = new RestCallAsync(getApplicationContext(), parcelURL, "GET", null).execute();
                        JSONObject parcelData = parcelResult.get();
                        JSONObject parcel = new JSONObject(parcelData.getString("data"));

                        parcel.put("onDestination", true);

                        AsyncTask<Void, Void, JSONObject> updParcelResult = new RestCallAsync(getApplicationContext(), parcelURL, "PUT", parcel).execute();
                        JSONObject updParcelData = updParcelResult.get();
                        JSONObject updParcel = new JSONObject(updParcelData.getString("data"));

                        //TODO: change unload icon visibility
                        if(updParcel.getBoolean("onDestination")) {
                            //TextView statusTV = (TextView) parent.getChildAt(position).findViewById(R.id.rpOnDestinationTV);
                            //statusTV.setText("DESCARGADO");
                            //statusTV.setVisibility(View.VISIBLE);

                            View view = parent.getChildAt(position);
                            view.findViewById(R.id.parcelUnloadCheckIV).setVisibility(View.VISIBLE);
                            ((TextView)view.findViewById(R.id.rpOnDestinationTV)).setText("DESCARGADO");
                            view.findViewById(R.id.rpOnDestinationTV).setVisibility(View.VISIBLE);
                            //ImageView checkIV = (ImageView) v.findViewById(R.id.parcelUnloadCheckIV);
                            //checkIV.setVisibility(View.VISIBLE);
                        }

                        //TODO: update item in list and update listview
                        parcelsMap.get(position).put("ondestination", "DESCARGADO");
                        parcelList.get(position).setOnDestination(true);
                        ((BaseAdapter)adapter).notifyDataSetChanged();

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            assert unloadAll != null;
            unloadAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        for (Parcel p : parcelList) {
                            String parcelURL = getString(R.string.URLgetParcel,
                                    getString(R.string.URL_REST_API),
                                    getString(R.string.tenantId),
                                    p.getId().toString());
                            p.setOnDestination(true);

                            AsyncTask<Void, Void, JSONObject> parcelResult = new RestCallAsync(getApplicationContext(), parcelURL, "GET", null).execute();
                            JSONObject parcelData = parcelResult.get();
                            JSONObject parcel = new JSONObject(parcelData.getString("data"));

                            parcel.put("onDestination", true);

                            AsyncTask<Void, Void, JSONObject> updParcelResult = new RestCallAsync(getApplicationContext(), parcelURL, "PUT", parcel).execute();
                            JSONObject updParcelData = updParcelResult.get();
                            JSONObject updParcel = new JSONObject(updParcelData.getString("data"));

                            //TODO: update item in list and update listview
                            parcelsMap.get(parcelList.indexOf(p)).put("ondestination", "DESCARGADO");

                           //View view = (View)adapter.getItem(parcelList.indexOf(p));
                            //view.findViewById(R.id.parcelUnloadCheckIV).setVisibility(View.VISIBLE);

                        }
                        //TODO: foreach parcel {
                        //TODO: call rest to mark parcel as unloaded (or on destination)
                        //TODO: change unload icon visibility
                        //TODO: }

                        for (int i=0; i<lv.getChildCount(); i++) {
                            lv.getChildAt(i).findViewById(R.id.parcelUnloadCheckIV).setVisibility(View.VISIBLE);
                        }

                        ((BaseAdapter)adapter).notifyDataSetChanged();

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
