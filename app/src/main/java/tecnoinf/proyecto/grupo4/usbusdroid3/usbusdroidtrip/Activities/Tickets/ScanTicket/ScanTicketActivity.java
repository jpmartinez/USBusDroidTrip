package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.ScanTicket;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class ScanTicketActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanBtn;
    //private TextView formatTxt, contentTxt;
    private String getTicketREST;
    private String ticketId;

    private static final int REQUEST_CAMERA_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ticket);


        scanBtn = (Button)findViewById(R.id.scan_button);
        //formatTxt = (TextView)findViewById(R.id.scan_format);
        //contentTxt = (TextView)findViewById(R.id.scan_content);

        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.scan_button){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);
            } else {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                List<String> scanFormats = new ArrayList<>();
                scanFormats.add("QR_CODE");
                scanIntegrator.initiateScan(scanFormats);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_RESULT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                    scanIntegrator.initiateScan();

                } else {
                    //TODO: Permiso denegado, reintentar?
                }
                return;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null){
            try {
                String scanContent = scanningResult.getContents();

                JSONObject ticketScannedData = new JSONObject(scanContent);
                ticketId = ticketScannedData.getString("id");

                getTicketREST = getString(R.string.URLgetTicket,
                        getString(R.string.URL_REST_API),
                        getString(R.string.tenantId),
                        ticketId);

                Intent ticketDetailsIntent = new Intent(this, STShowDetailsActivity.class);
                AsyncTask<Void, Void, JSONObject> ticketResult = new RestCallAsync(getApplicationContext(), getTicketREST, "GET", null).execute();
                JSONObject ticketData = ticketResult.get();
                //TODO: ver si el result es OK y ahí llamar al siguiente activity, caso contrario mostrar error correspondiente
                JSONObject ticketJSON = new JSONObject(ticketData.getString("data"));

                ticketDetailsIntent.putExtra("ticket", ticketJSON.toString());
                startActivity(ticketDetailsIntent);

            } catch (JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se pudo escanear", Toast.LENGTH_LONG);
            toast.show();
        }
    }


}
