package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.PrintTicket;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.TicketShort;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class PrintTicketListActivity extends ListActivity {

    private static JSONArray ticketsArray;
    private static String ticketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printticket_list);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);

        try {
            ticketsArray = new JSONArray(sharedPreferences.getString("ticketsArray", "").replace("\\", ""));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

            final List<TicketShort> ticketsList = TicketShort.fromJson(ticketsArray);
            ArrayList<HashMap<String, String>> ticketsMap = new ArrayList<>();

            for (TicketShort ts2 : ticketsList) {
                HashMap<String, String> t = new HashMap<>();
                t.put("id", ts2.getId().toString());
                t.put("amount", ts2.getAmount().toString());
                t.put("status", ts2.getStatus().toString());
                t.put("journeyName", ts2.getJourneyName());
                t.put("journeyDate", dateFormat.format(ts2.getJourneyDate()));
                t.put("journeyTime", timeFormat.format(ts2.getJourneyTime()));
                t.put("busNumber", ts2.getBusNumber().toString());
                t.put("seat", (ts2.getSeat() == 999)? "De pie" : ts2.getSeat().toString());

                ticketsMap.add(t);
            }

            ListAdapter adapter = new SimpleAdapter(
                    getApplicationContext(),
                    ticketsMap,
                    R.layout.activity_printticket_list_item,
                    new String[] { "id",
                            "amount",
                            "status",
                            "journeyName",
                            "journeyDate",
                            "journeyTime",
                            "busNumber",
                            "seat"},
                    new int[] { R.id.ptItemid,
                            R.id.ptAmountTV,
                            R.id.ptStatusTV,
                            R.id.ptJourneyNameTV,
                            R.id.ptJourneyDateTV,
                            R.id.ptJourneyTimeTV,
                            R.id.ptBusNumberTV,
                            R.id.ptSeatTV});

            setListAdapter(adapter);
            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        ticketId = ((TextView) view.findViewById(R.id.ptItemid)).getText().toString();

                        JSONObject ticket = new JSONObject();
                        for (int k = 0; k < ticketsArray.length(); k++) {
                            if(ticketsArray.getJSONObject(k).getInt("id") == Integer.valueOf(ticketId)) {
                                ticket = ticketsArray.getJSONObject(k);
                                break;
                            }
                        }

                        Intent confirmationIntent = new Intent(getBaseContext(), PTConfirmationActivity.class);

                        confirmationIntent.putExtra("ticket", ticket.toString());
                        startActivity(confirmationIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}
