package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.CloseTrip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.MainActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCallAsync;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.JourneyStatus;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class CTConfirmationActivity extends AppCompatActivity {

    private String endJourneyREST;
    private String onCourseJourney;
    private SharedPreferences.Editor editor;
    private Button confirmationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctconfirmation);

        final SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");

        confirmationButton = (Button) findViewById(R.id.closeTripConfirmationBtn);
        assert confirmationButton != null;

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    endJourneyREST = getString(R.string.URLupdateJourney,
                            getString(R.string.URL_REST_API),
                            getString(R.string.tenantId),
                            onCourseJourney);
                    JSONObject journey = new JSONObject();
                    journey.put("status", JourneyStatus.ARRIVED);


                    AsyncTask<Void, Void, JSONObject> journeyResult = new RestCallAsync(getApplicationContext(), endJourneyREST, "PATCH", journey).execute();

                    Toast.makeText(getApplicationContext(), "Viaje finalizado correctamente", Toast.LENGTH_LONG).show();

                    editor.putString("onCourseJourney", "");
                    editor.putString("journey", "");
                    editor.putString("busId", "");
                    editor.putString("journeyId", "");
                    editor.apply();

                    Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(resultIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
