package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.NewTicket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.TicketOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.QRCodeEncoder;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
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
    private JSONObject ticketData;
    private String ticketIdEncrypted;
    private ImageButton qrCodeBtn;
    private Integer standingCurrent;
    private Integer WIDTH;
    private Integer HEIGHT;
    private SharedPreferences.Editor editor;
    private String tenantId;

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
        editor = sharedPreferences.edit();
        buyTicketRest = getString(R.string.URLbuyTicket, getString(R.string.URL_REST_API), getString(R.string.tenantId));
        username = sharedPreferences.getString("username", "");
        standingCurrent = sharedPreferences.getInt("standingCurrent", 0);
        tenantId = sharedPreferences.getString("tenantId", "");

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
            qrCodeBtn = (ImageButton) findViewById(R.id.qrCodeBtn);
            assert ticketCostTV != null;
            ticketCostTV.setText(paymentAmount);
            assert ticketSeatTV != null;
            ticketSeatTV.setText(selectedSeat.equalsIgnoreCase("999")?"De pie":selectedSeat);
            assert ticketOriginTV != null;
            ticketOriginTV.setText(father.getStringExtra("origin"));
            assert ticketDestinationTV != null;
            ticketDestinationTV.setText(father.getStringExtra("destination"));
            assert ticketDateTV != null;
            ticketDateTV.setText(dateFormat.format(journey.get("date")));
            assert ticketBusIdTV != null;
            ticketBusIdTV.setText(journey.get("busNumber").toString());
            assert  qrCodeBtn != null;

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
                    newTicket.put("tenantId", tenantId);
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
                    newTicket.put("status", TicketStatus.INUSE);
                    newTicket.put("routeId", journey.getJSONObject("service").getJSONObject("route").get("id"));
                    newTicket.put("paymentToken", "droid_cash");
                    newTicket.put("branchId", 0);
                    newTicket.put("windowId", 0);
                    newTicket.put("kmGetsOn", father.getDoubleExtra("originKm", 0.0));
                    newTicket.put("kmGetsOff", father.getDoubleExtra("destinationKm", 0.0));

                    AsyncTask<Void, Void, JSONObject> ticketResult = new RestCallAsync(getApplicationContext(), buyTicketRest, "POST", newTicket).execute();
                    ticketData = ticketResult.get();

                    JSONObject ticket = new JSONObject(ticketData.getString("data"));
                    JSONObject qrTicket = new JSONObject();

                    if(ticket.getInt("seat") == 999) {
                        editor.putInt("standingCurrent", ++standingCurrent);
                        editor.apply();
                    }

                    qrTicket.put("tenantId", ticket.get("tenantId"));
                    qrTicket.put("id", ticket.get("id"));

                    ticketIdEncrypted = qrTicket.toString();
                    Bitmap bitmap = encodeAsBitmap(ticketIdEncrypted);
                    qrCodeBtn.setImageBitmap(bitmap);

                    View myView = getWindow().getDecorView().getRootView().findViewById(R.id.tableLayout);
                    //**********************PRINT*************************************************
                    Bitmap screen;
                    //View v1 = myView.getRootView();
                    View v1 = myView;
                    v1.setDrawingCacheEnabled(true);
                    screen = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    PrintHelper photoPrinter = new PrintHelper(getBaseContext());
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    photoPrinter.printBitmap("usbus_ticket.jpg", screen);
                    //**********************PRINT*************************************************

//                    Intent ticketOptionsIntent = new Intent(getApplicationContext(), TicketOptionsActivity.class);
//                    startActivity(ticketOptionsIntent);
                    confirmBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);

                } catch (JSONException | InterruptedException | ExecutionException | WriterException e) {
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
                startActivity(ticketOptionsIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            startActivity(new Intent(this, TicketOptionsActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TicketOptionsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;

        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        WIDTH = point.x;
        HEIGHT = point.y - 400;
        int smallerDimension = WIDTH < HEIGHT ? WIDTH : HEIGHT;
        smallerDimension = smallerDimension * 3/4;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(ticketIdEncrypted,
                null,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
//            ImageView myImage = (ImageView) findViewById(R.id.imageView1);
            qrCodeBtn.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

}
