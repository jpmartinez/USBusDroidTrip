package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.NewTicket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.TicketOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.Ticket;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.TicketStatus;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class NTConfirmationActivity extends AppCompatActivity {

    private Button confirmBtn;
    private Button cancelBtn;
    private String paymentAmount;
    private String username;
    private JSONObject journey;
    private JSONObject newTicket;
    private String buyTicketRest;
    private Intent father;
    JSONObject ticketData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntconfirmation);
        father = getIntent();
        try {
            journey = new JSONObject(father.getStringExtra("journey"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        buyTicketRest = getString(R.string.URLbuyTicket, getString(R.string.URL_REST_API), getString(R.string.tenantId));
        username = sharedPreferences.getString("username", "");

        String selectedSeat = father.getStringExtra("seat");
        paymentAmount = father.getStringExtra("ticketPrice");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            journey = new JSONObject(father.getStringExtra("journey"));

            TextView ticketOriginTV = (TextView) findViewById(R.id.ticketOriginTV);
            TextView ticketDestinationTV = (TextView) findViewById(R.id.ticketDestinationTV);
            TextView ticketDateTV = (TextView) findViewById(R.id.ticketDateTV);
            TextView ticketBusIdTV = (TextView) findViewById(R.id.ticketBusIdTV);
            TextView ticketSeatTV = (TextView) findViewById(R.id.ticketSeatTV);
            TextView ticketCostTV = (TextView) findViewById(R.id.ticketCostTV);
            assert ticketCostTV != null;
            ticketCostTV.setText(paymentAmount);
            assert ticketSeatTV != null;
            ticketSeatTV.setText(selectedSeat);
            assert ticketOriginTV != null;
            ticketOriginTV.setText(father.getStringExtra("origin"));
            assert ticketDestinationTV != null;
            ticketDestinationTV.setText(father.getStringExtra("destination"));
            assert ticketDateTV != null;
            ticketDateTV.setText(dateFormat.format(journey.get("date")));
            assert ticketBusIdTV != null;
            ticketBusIdTV.setText(journey.get("busNumber").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        confirmBtn = (Button) findViewById(R.id.ntConfirmOKBtn);
        cancelBtn = (Button) findViewById(R.id.ntConfirmCancelBtn);

        assert confirmBtn != null;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTicket = new JSONObject();
                //journeyId, getOnStopName, getOffStopName, passengerName, seat, closed (true)
                try {
                    newTicket.put("tenantId", getString(R.string.tenantId));
                    newTicket.put("journeyId", journey.get("id"));
                    newTicket.put("hasCombination", false);
                    newTicket.put("combination", null);
                    newTicket.put("combinationId", null);
                    newTicket.put("amount", paymentAmount);
                    newTicket.put("getOnStopName", father.getStringExtra("origin"));
                    newTicket.put("getOffStopName", father.getStringExtra("destination"));
                    newTicket.put("sellerName", username);
                    newTicket.put("seat", father.getStringExtra("seat"));
                    newTicket.put("closed", true);
                    newTicket.put("status", TicketStatus.CONFIRMED);
                    newTicket.put("routeId", journey.getJSONObject("service").getJSONObject("route").get("id"));

                    AsyncTask<Void, Void, JSONObject> ticketResult = new RestCallAsync(getApplicationContext(), buyTicketRest, "POST", newTicket).execute();
                    ticketData = ticketResult.get();

                    View myView = getWindow().getDecorView().getRootView().findViewById(R.id.tableLayout);


                    //**********************PRINT*************************************************
                    Bitmap screen;
                    //View v1 = myView.getRootView();
                    View v1 = myView;
                    //TODO: hide/remove buttons ?
                    v1.setDrawingCacheEnabled(true);
                    screen = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    PrintHelper photoPrinter = new PrintHelper(getBaseContext());
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    photoPrinter.printBitmap("droids.jpg - test print", screen);
                    //**********************PRINT*************************************************

                    Intent ticketOptionsIntent = new Intent(getApplicationContext(), TicketOptionsActivity.class);
                    startService(ticketOptionsIntent);

                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        assert cancelBtn != null;
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Ticket Cancelado", Toast.LENGTH_LONG).show();

                Intent ticketOptionsIntent = new Intent(getApplicationContext(), TicketOptionsActivity.class);
                startService(ticketOptionsIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            startActivity(new Intent(this, TicketOptionsActivity.class));
        }
    }
}
