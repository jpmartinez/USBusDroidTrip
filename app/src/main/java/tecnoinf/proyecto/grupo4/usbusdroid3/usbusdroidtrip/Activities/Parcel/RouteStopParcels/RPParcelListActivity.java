package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.RouteStopParcels;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Dimension;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Parcel;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class RPParcelListActivity extends ListActivity {

    private ArrayList<HashMap<String, String>> parcelsMap;
    private ListAdapter adapter;

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

            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    System.out.println(position);
                    //TODO: call rest to mark parcel as unloaded (or on destination)
                    //TODO: change unload icon visibility
                }
            });

            assert unloadAll != null;
            unloadAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: foreach parcel {
                    //TODO: call rest to mark parcel as unloaded (or on destination)
                    //TODO: change unload icon visibility
                    //TODO: }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
