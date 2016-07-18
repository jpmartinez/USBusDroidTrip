package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.PrintTicket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
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

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.TicketOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.QRCodeEncoder;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class PTConfirmationActivity extends AppCompatActivity {

    private Button confirmBtn;
    private Button cancelBtn;
    private JSONObject ticket;
    private String ticketIdEncrypted;
    private ImageButton qrCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptconfirmation);
        Intent father = getIntent();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            ticket = new JSONObject(father.getStringExtra("ticket"));

            TextView ticketOriginTV = (TextView) findViewById(R.id.ticketOriginTV);
            TextView ticketDestinationTV = (TextView) findViewById(R.id.ticketDestinationTV);
            TextView ticketDateTV = (TextView) findViewById(R.id.ticketDateTV);
            TextView ticketBusIdTV = (TextView) findViewById(R.id.ticketBusIdTV);
            TextView ticketSeatTV = (TextView) findViewById(R.id.ticketSeatTV);
            TextView ticketCostTV = (TextView) findViewById(R.id.ticketCostTV);
            qrCodeBtn = (ImageButton) findViewById(R.id.qrCodeBtn);
            assert ticketCostTV != null;
            ticketCostTV.setText(String.format("%.2f", ticket.getDouble("amount")));
            assert ticketSeatTV != null;
            ticketSeatTV.setText(ticket.getInt("seat") == 999?"De pie" : String.valueOf(ticket.getInt("seat")));
            assert ticketOriginTV != null;
            ticketOriginTV.setText(ticket.getJSONObject("getsOn").getString("name"));
            assert ticketDestinationTV != null;
            ticketDestinationTV.setText(ticket.getJSONObject("getsOff").getString("name"));
            assert ticketDateTV != null;
            ticketDateTV.setText(dateFormat.format(ticket.getJSONObject("journey").get("date")));
            assert ticketBusIdTV != null;
            ticketBusIdTV.setText(ticket.getJSONObject("journey").get("busNumber").toString());
            assert  qrCodeBtn != null;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        confirmBtn = (Button) findViewById(R.id.ptConfirmOKBtn);
        cancelBtn = (Button) findViewById(R.id.ptConfirmCancelBtn);

        assert confirmBtn != null;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject qrTicket = new JSONObject();

                    qrTicket.put("tenantId", ticket.get("tenantId"));
                    qrTicket.put("id", ticket.get("id"));

                    ticketIdEncrypted = qrTicket.toString();
                    Bitmap bitmap = encodeAsBitmap(ticketIdEncrypted);
                    qrCodeBtn.setImageBitmap(bitmap);

                    View myView = getWindow().getDecorView().getRootView().findViewById(R.id.tableLayout);

                    //**********************PRINT*************************************************
                    Bitmap screen;
                    myView.setDrawingCacheEnabled(true);
                    screen = Bitmap.createBitmap(myView.getDrawingCache());
                    myView.setDrawingCacheEnabled(false);

                    PrintHelper photoPrinter = new PrintHelper(getBaseContext());
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    photoPrinter.printBitmap("usbus_ticket.jpg", screen);
                    //**********************PRINT*************************************************

                    confirmBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);

                } catch (JSONException | WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        assert cancelBtn != null;
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Impresión Cancelada", Toast.LENGTH_LONG).show();
                Intent ticketOptionsIntent = new Intent(getApplicationContext(), TicketOptionsActivity.class);
                startActivity(ticketOptionsIntent);
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            startActivity(new Intent(this, TicketOptionsActivity.class));
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(this, TicketOptionsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Integer WIDTH = point.x;
        Integer HEIGHT = point.y - 400;
        int smallerDimension = WIDTH < HEIGHT ? WIDTH : HEIGHT;
        smallerDimension = smallerDimension * 3/4;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(ticketIdEncrypted,
                null,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            qrCodeBtn.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Formato no soportado
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
