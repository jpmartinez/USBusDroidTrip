package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.ScanTicket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.TicketOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.TicketStatus;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class STShowDetailsActivity extends AppCompatActivity {

    private JSONObject ticket;
    private String ticketId;
    private String confirmTicketREST;
    private String onCourseJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stshow_details);
        Intent father = getIntent();

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");

        TextView journeyNameTV = (TextView) findViewById(R.id.journeyNameTV);
        TextView dateTV = (TextView) findViewById(R.id.dateTV);
        TextView timeTV = (TextView) findViewById(R.id.timeTV);
        TextView seatTV = (TextView) findViewById(R.id.seatTV);
        TextView statusTV = (TextView) findViewById(R.id.statusTV);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        try {
            ticket = new JSONObject(father.getStringExtra("ticket"));
            if(ticket.get("tenantId").toString().equals(getString(R.string.tenantId))){
                journeyNameTV.setText(ticket.getJSONObject("journey").getJSONObject("service").getString("name"));
                dateTV.setText(dateFormat.format(ticket.getJSONObject("journey").get("date")));
                timeTV.setText(timeFormat.format(ticket.getJSONObject("journey").getJSONObject("service").get("time")));
                seatTV.setText(ticket.get("seat").toString());
                statusTV.setText(ticket.getString("status"));

                ticketId = ticket.get("id").toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button confirmBtn = (Button) findViewById(R.id.confirmTicketBtn);
        assert confirmBtn != null;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(ticket.getString("status").equalsIgnoreCase(TicketStatus.CONFIRMED.toString())) {
                        confirmTicketREST = getString(R.string.URLupdateTicket,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                ticketId);

                        JSONObject ticketConfirmation = new JSONObject();
                        ticketConfirmation.put("tenantId", ticket.get("tenantId"));
                        ticketConfirmation.put("id", ticketId);
                        ticketConfirmation.put("status", TicketStatus.USED);

                        AsyncTask<Void, Void, JSONObject> ticketUpdateResult = new RestCallAsync(getApplicationContext(), confirmTicketREST, "PUT", ticketConfirmation).execute();
                        JSONObject ticketData = ticketUpdateResult.get();
                        JSONObject ticketUpdated = new JSONObject(ticketData.getString("data"));

                        Toast.makeText(getApplicationContext(), ticketUpdated.getString("status"), Toast.LENGTH_LONG).show();
                        finish();

                    } else if(ticket.getString("status").equalsIgnoreCase(TicketStatus.USED.toString()) ||
                              ticket.getString("status").equalsIgnoreCase(TicketStatus.CANCELED.toString()) ){
                        Toast.makeText(getApplicationContext(), R.string.used_canceled_ticket, Toast.LENGTH_LONG).show();
                    } else if(ticket.getJSONObject("journey").getString("id").equalsIgnoreCase(onCourseJourney)) {

                    }
                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), TicketOptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
