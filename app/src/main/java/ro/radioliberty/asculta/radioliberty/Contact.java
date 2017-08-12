package ro.radioliberty.asculta.radioliberty;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final EditText nume_input = (EditText) findViewById(R.id.nume);
        final EditText email_input = (EditText) findViewById(R.id.email);
        final EditText subiect_input = (EditText) findViewById(R.id.subiect);
        final EditText mesaj_input = (EditText) findViewById(R.id.mesaj);
        Button send = (Button) findViewById(R.id.trimite);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lista cu parametri
                if (nume_input.getText().toString().trim() != "" && email_input.getText().toString().trim() != "" && subiect_input.getText().toString().trim() != "" && mesaj_input.getText().toString().trim() != "") {

                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email_input.getText().toString()).matches()==true) {
                        List<PostParameter> data = new ArrayList<>();


                        //Adaug parametrii
                        data.add(new PostParameter<String>("nume", nume_input.getText().toString()));
                        data.add(new PostParameter<String>("email", email_input.getText().toString()));
                        data.add(new PostParameter<String>("subiect", subiect_input.getText().toString()));
                        data.add(new PostParameter<String>("mesaj", mesaj_input.getText().toString()));

                        //Fac requestul
                        String response = Functions.postRequest(Config.getBase_url() + "/contact", data);

                        AlertDialog alertDialog = new AlertDialog.Builder(Contact.this).create();
                        alertDialog.setTitle("Radio Liberty");
                        alertDialog.setMessage(response);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(Contact.this).create();
                        alertDialog.setTitle("Radio Libertyy");
                        alertDialog.setMessage("Email invalid!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }

                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(Contact.this).create();
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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
