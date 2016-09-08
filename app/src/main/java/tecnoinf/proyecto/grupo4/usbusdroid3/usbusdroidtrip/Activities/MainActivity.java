package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Parcel.ParcelOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.TicketOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.TripOptionsActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.GPSTracker;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class MainActivity extends AppCompatActivity {

    private String onCourseJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
        onCourseJourney = sharedPreferences.getString("onCourseJourney", "");

        ImageButton ticketOptionsBt = (ImageButton) findViewById(R.id.ticketOptionsButton);
        ImageButton tripOptionsBt = (ImageButton) findViewById(R.id.tripOptionsButton);
        ImageButton parcelOptionsBt = (ImageButton) findViewById(R.id.parcelOptionsButton);

        assert ticketOptionsBt != null;
        ticketOptionsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney != null && !onCourseJourney.isEmpty()){
                    Intent newTicketIntent = new Intent(v.getContext(), TicketOptionsActivity.class);
                    startActivity(newTicketIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                }
            }
        });

        assert tripOptionsBt != null;
        tripOptionsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myTicketsIntent = new Intent(v.getContext(), TripOptionsActivity.class);
                startActivity(myTicketsIntent);
            }
        });

        assert parcelOptionsBt != null;
        parcelOptionsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCourseJourney != null && !onCourseJourney.isEmpty()){
                    Intent parcelsIntent = new Intent(getBaseContext(), ParcelOptionsActivity.class);
                    startActivity(parcelsIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_open_trip, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
