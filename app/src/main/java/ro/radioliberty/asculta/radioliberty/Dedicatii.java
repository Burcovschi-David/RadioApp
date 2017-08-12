package ro.radioliberty.asculta.radioliberty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.text.StringPrepParseException;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dedicatii extends AppCompatActivity {



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dedicatii);

        final EditText nume = (EditText) findViewById(R.id.nume);
        final EditText catrecinededic = (EditText) findViewById(R.id.catrecinededic);
        final EditText melodie = (EditText) findViewById(R.id.melodie);
        final EditText textuldedicatiei = (EditText) findViewById(R.id.mesaj);
        final EditText candsedifuzeaza = (EditText) findViewById(R.id.candsedifuzeaza);
        final EditText email = (EditText) findViewById(R.id.email);
        Button trimite = (Button) findViewById(R.id.trimite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        candsedifuzeaza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Dedicatii.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        candsedifuzeaza.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        trimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nume_string=nume.getText().toString();
                String catrecinededic_string=catrecinededic.getText().toString();
                String melodie_string=melodie.getText().toString();
                String textuldedicatiei_string=textuldedicatiei.getText().toString();
                String candsedifuzeaza_string=candsedifuzeaza.getText().toString();
                String email_string=email.getText().toString();

                //Lista cu parametri
                if (email_string.trim()!="" && candsedifuzeaza_string.trim()!="" && nume_string.trim() != "" && catrecinededic_string.trim() != "" && melodie_string.trim() != "" && textuldedicatiei_string.trim() != "") {


                        List<PostParameter> data = new ArrayList<>();


                        //Adaug parametrii
                        data.add(new PostParameter<String>("email",email_string));
                        data.add(new PostParameter<String>("nume", nume_string));
                        data.add(new PostParameter<String>("catrecinededic", candsedifuzeaza_string));
                        data.add(new PostParameter<String>("melodie", melodie_string));
                        data.add(new PostParameter<String>("textuldedicatiei", textuldedicatiei_string));
                        data.add(new PostParameter<String>("candsedifuzeaza",candsedifuzeaza_string));

                        //Fac requestul
                        String response = Functions.postRequest(Config.getBase_url() + "/dedicatii", data);

                        AlertDialog alertDialog = new AlertDialog.Builder(Dedicatii.this).create();
                        alertDialog.setTitle("Radio Liberty");
                        alertDialog.setMessage(response);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();


                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(Dedicatii.this).create();
                    alertDialog.setTitle("Radio Libertyy");
                    alertDialog.setMessage("Nu ati completat toate spatiile!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

    }
}
