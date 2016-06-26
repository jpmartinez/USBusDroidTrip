package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class TripOptionsActivity extends AppCompatActivity {

    private String onCourseJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_options);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");

        ImageButton startTripBt = (ImageButton) findViewById(R.id.startTripBtn);
        ImageButton endTripBt = (ImageButton) findViewById(R.id.endTripBtn);
        ImageButton odometerBt = (ImageButton) findViewById(R.id.odometerBtn);

        assert startTripBt != null;
        startTripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    //TODO: call activity para abrir journey. Listarle los journeys de hoy para elegir?
                } else if (onCourseJourney != null && !onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.journey_already_open, Toast.LENGTH_LONG).show();
                }
            }
        });

        assert endTripBt != null;
        endTripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else {
                    //TODO: Otra activity para mostrar data del trip para confirmar cierre.
                }
            }
        });

        assert odometerBt != null;
        odometerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney == null || onCourseJourney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                } else {
                    //TODO: Otra activity para mostrar input para cargar odometer. Existe esto?
                }
            }
        });
    }
}
