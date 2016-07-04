package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.CancelTicket;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class CancelTicketListActivity extends ListActivity {

    private static JSONArray ticketsArray;
    private String ticketId;
    private String deleteTicketREST;
    private Integer standingCurrent;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelticket_list);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        standingCurrent = sharedPreferences.getInt("standingCurrent", 0);

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
                    R.layout.activity_cancelticket_list_item,
                    new String[] { "id",
                            "amount",
                            "status",
                            "journeyName",
                            "journeyDate",
                            "journeyTime",
                            "busNumber",
                            "seat"},
                    new int[] { R.id.ctItemid,
                            R.id.ctAmountTV,
                            R.id.ctStatusTV,
                            R.id.ctJourneyNameTV,
                            R.id.ctJourneyDateTV,
                            R.id.ctJourneyTimeTV,
                            R.id.ctBusNumberTV,
                            R.id.ctSeatTV});

            setListAdapter(adapter);
            ListView lv = getListView();
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        ticketId = ticketsList.get(position).getId().toString();
                        deleteTicketREST = getString(R.string.URLdeleteTicket,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                ticketId);

                        AsyncTask<Void, Void, JSONObject> deleteTicketResult =
                                new RestCallAsync(getApplicationContext(), deleteTicketREST, "DELETE", null)
                                        .execute();
                        JSONObject deleteTicketData = deleteTicketResult.get();

                        if (deleteTicketData.getString("result").equalsIgnoreCase("OK")) {
                            view.setBackground(getDrawable(R.drawable.ticket_list_bg_red));
                            ((TextView) view.findViewById(R.id.ctStatusTV)).setText("CANCELADO");
                            ((TextView) view.findViewById(R.id.ctStatusTV)).setTextColor(Color.RED);
                            ((TextView) view.findViewById(R.id.ctStatusTV)).setTypeface(null, Typeface.BOLD);
                            view.setClickable(false);
                            view.setLongClickable(false);

                            Toast.makeText(getBaseContext(), "Ticket cancelado correctamente", Toast.LENGTH_LONG).show();

                            if (deleteTicketData.getInt("seat") == 999) {
                                standingCurrent--;
                                editor.putInt("standingCurrent", standingCurrent);
                                editor.apply();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Error al eliminar Ticket", Toast.LENGTH_LONG).show();
                        }

                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}
