package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.NewTicket.NewTicketActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.ScanTicket.ScanTicketActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class TicketOptionsActivity extends AppCompatActivity {

    private String getJourneyREST;
    private String journeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_options);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        journeyId = sharedPreferences.getString("journeyId", "");

        ImageButton scanQRBtn = (ImageButton) findViewById(R.id.scanQRBtn);
        final ImageButton newTicketBtn = (ImageButton) findViewById(R.id.newTicketBtn);
        ImageButton cancelTicketBtn = (ImageButton) findViewById(R.id.cancelTicketBtn);
        ImageButton printTicketBtn = (ImageButton) findViewById(R.id.printTicketBtn);

        assert scanQRBtn != null;
        scanQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanTicketIntent = new Intent(getBaseContext(), ScanTicketActivity.class);
                startActivity(scanTicketIntent);
            }
        });

        assert newTicketBtn != null;
        newTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getJourneyREST = getString(R.string.URLgetJourney,
                            getString(R.string.URL_REST_API),
                            getString(R.string.tenantId),
                            journeyId);

                    AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), getJourneyREST, "GET", null).execute();
                    JSONObject journeyData = journeyResult.get();

                    Intent newTicketIntent = new Intent(getApplicationContext(), NewTicketActivity.class);
                    newTicketIntent.putExtra("journey", journeyData.getString("data"));
                    startActivity(newTicketIntent);

                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        assert cancelTicketBtn != null;
        cancelTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        assert printTicketBtn != null;
        printTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
