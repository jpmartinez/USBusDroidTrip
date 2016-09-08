package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.Odometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.MainActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.TripOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class SetOdometerActivity extends AppCompatActivity {

    private Boolean odometerSet;
    private String busId;
    private SharedPreferences.Editor editor;
    private String odometerREST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_odometer);

        final SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        odometerSet = sharedPreferences.getBoolean("odometerSet", false);
        busId = sharedPreferences.getString("busId", "");

        if(odometerSet) {
            Toast.makeText(getApplicationContext(), R.string.odometer_already_set, Toast.LENGTH_LONG).show();
        }

        final EditText odometerET = (EditText) findViewById(R.id.odometerET);
        final Button odometerBtn = (Button) findViewById(R.id.odometerConfirmBtn);

        assert odometerET != null;
        assert odometerBtn != null;

//        odometerET.enable
        odometerET.setFocusableInTouchMode(true);
        odometerET.setKeyListener(new DigitsKeyListener());

        odometerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(odometerET.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.MustInputOdometerReading, Toast.LENGTH_LONG).show();
                } else {
                    if (!busId.isEmpty()) {
                        try {
                            odometerREST = getString(R.string.URLupdateBus,
                                    getString(R.string.URL_REST_API),
                                    getString(R.string.tenantId),
                                    busId);

                            Double odometerReading = Double.parseDouble(odometerET.getText().toString());
                            JSONObject postData = new JSONObject();
                            postData.put("kms", odometerReading);

                            AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), odometerREST, "PATCH", postData).execute();
                            JSONObject restData = journeyResult.get();
                            JSONObject busJSON = new JSONObject(restData.getString("data"));

                            if (busJSON.getDouble("kms") == odometerReading) {
                                Toast.makeText(getApplicationContext(), R.string.odometerReadingSaved, Toast.LENGTH_LONG).show();
                            }

                            editor.putBoolean("odometerSet", true);
                            editor.apply();

                            Intent mainIntent = new Intent(getBaseContext(), TripOptionsActivity.class);
                            startActivity(mainIntent);

                        } catch (JSONException | ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //TODO: Error getting busId from sharedPreferences
                    }
                }
            }
        });
    }
}
