package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities.Tickets.ScanTicket.ScanTicketActivity;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class TicketOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_options);

        ImageButton scanQRBtn = (ImageButton) findViewById(R.id.scanQRBtn);
        ImageButton newTicketBtn = (ImageButton) findViewById(R.id.newTicketBtn);
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

            }
        });

        assert cancelTicketBtn != null;
        cancelTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        assert printTicketBtn != null;
        printTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
