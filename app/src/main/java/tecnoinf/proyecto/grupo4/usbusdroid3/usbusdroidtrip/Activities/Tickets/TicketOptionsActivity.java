package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.CancelTicket.CancelTicketListActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.NewTicket.NTBusStopSelectionActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.NewTicket.NewTicketActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.PrintTicket.PrintTicketListActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.ScanTicket.ScanTicketActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class TicketOptionsActivity extends AppCompatActivity {

    private String getJourneyREST;
    private String ticketsREST;
    private String journeyId;
    private String onCourseJourney;
    private SharedPreferences.Editor editor;
    private Integer standingCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_options);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        journeyId = sharedPreferences.getString("journeyId", "");
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");
        standingCurrent = sharedPreferences.getInt("standingCurrent", 0);

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

//                ticketsREST = getString(R.string.URLgetTickets,
//                        getString(R.string.URL_REST_API),
//                        getString(R.string.tenantId),
//                        onCourseJourney);

//                AsyncTask<Void, Void, JSONObject> ticketsResult = new RestCallAsync(getApplicationContext(), ticketsREST, "GET", null).execute();
//                JSONObject ticketsData = ticketsResult.get();
//                JSONArray ticketsArray = new JSONArray(ticketsData.get("data").toString());
//                int standing = 0;
//                for (int k = 0; k < ticketsArray.length(); k++) {
//                    if(ticketsArray.getJSONObject(k).getInt("seat") == 999 &&
//                            ticketsArray.getJSONObject(k).getString("status").equalsIgnoreCase("CONFIRMED")) {
//                        standing++;
//                    }
//                }
                Intent selectStopsIntent = new Intent(getBaseContext(), NTBusStopSelectionActivity.class);
                selectStopsIntent.putExtra("journey", journeyData.getString("data"));
                selectStopsIntent.putExtra("standingCurrent", standingCurrent);
                startActivity(selectStopsIntent);
//
//                Intent newTicketIntent = new Intent(getApplicationContext(), NewTicketActivity.class);
//                newTicketIntent.putExtra("journey", journeyData.getString("data"));
//                newTicketIntent.putExtra("standingCurrent", standingCurrent);
//                startActivity(newTicketIntent);

            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
            }
        });

        assert cancelTicketBtn != null;
        cancelTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        ticketsREST = getString(R.string.URLgetTickets,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                "JOURNEY",
                                onCourseJourney,
                                "CONFIRMED");

                        AsyncTask<Void, Void, JSONObject> ticketsResult = new RestCallAsync(getApplicationContext(), ticketsREST, "GET", null).execute();
                        JSONObject ticketsData = ticketsResult.get();
                        JSONArray ticketsArray = new JSONArray(ticketsData.get("data").toString());

                        for(int i = 0; i < ticketsArray.length(); i++) {
                            if (ticketsArray.getJSONObject(i).getString("status").equalsIgnoreCase("CANCELED") ||
                                    !ticketsArray.getJSONObject(i).getString("paymentToken").equalsIgnoreCase("droid_cash")) {
                                ticketsArray.remove(i);
                                i--;
                            }
                        }

                        if (ticketsArray.length() > 0) {
                            editor.putString("ticketsArray", ticketsArray.toString());
                            editor.apply();

                            Intent cancelTicketIntent = new Intent(getApplicationContext(), CancelTicketListActivity.class);
                            startActivity(cancelTicketIntent);
                        } else {
                            Toast.makeText(getBaseContext(), "No hay tickets para cancelar", Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        assert printTicketBtn != null;
        printTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        ticketsREST = getString(R.string.URLgetTickets,
                                getString(R.string.URL_REST_API),
                                getString(R.string.tenantId),
                                "JOURNEY",
                                onCourseJourney,
                                "INUSE");

                        AsyncTask<Void, Void, JSONObject> ticketsResult = new RestCallAsync(getApplicationContext(), ticketsREST, "GET", null).execute();
                        JSONObject ticketsData = ticketsResult.get();
                        JSONArray ticketsArray = new JSONArray(ticketsData.get("data").toString());

                        for(int i = 0; i < ticketsArray.length(); i++) {
                            if (ticketsArray.getJSONObject(i).getString("status").equalsIgnoreCase("CANCELED") ||
                                    !ticketsArray.getJSONObject(i).getString("paymentToken").equalsIgnoreCase("droid_cash")) {
                                ticketsArray.remove(i);
                                i--;
                            }
                        }

                        if (ticketsArray.length() > 0) {
                            editor.putString("ticketsArray", ticketsArray.toString());
                            editor.apply();

                            Intent printTicketIntent = new Intent(getApplicationContext(), PrintTicketListActivity.class);
                            startActivity(printTicketIntent);
                        } else {
                            Toast.makeText(getBaseContext(), "No hay tickets para imprimir", Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
