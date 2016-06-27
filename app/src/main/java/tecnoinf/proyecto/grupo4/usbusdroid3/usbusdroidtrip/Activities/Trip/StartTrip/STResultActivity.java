package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Trip.StartTrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class STResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stresult);
        Intent father = getIntent();
        try {
            JSONObject journey = new JSONObject(father.getStringExtra("journey"));

            System.out.println(journey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
