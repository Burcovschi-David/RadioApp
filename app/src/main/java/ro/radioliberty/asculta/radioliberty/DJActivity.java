package ro.radioliberty.asculta.radioliberty;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DJActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj);


       getSupportActionBar().setDisplayHomeAsUpEnabled(true);






        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);



        if(Functions.isReachableByTcp(Config.server_domain, 80, 4000)){

            DJAdapter ca = new DJAdapter(createList());
            recList.setAdapter(ca);

        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(DJActivity.this).create();
            alertDialog.setTitle("Radio Liberty");
            alertDialog.setMessage("Server connection error!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }




    public class getDataURL extends AsyncTask<String,Integer,String> {

        protected String doInBackground(String... params) {

            String jsonStr="";


            try {

                URL url = new URL(params[0]);
                URLConnection conn = url.openConnection();

                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                InputStream is = httpConn.getInputStream();
                jsonStr = convertinputStreamToString(is);
                //Log.d("JSON:",jsonStr);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonStr;
        }

        protected void onPostExecute(String result) {
            // here you have the result
            super.onPostExecute(result);
        }
    }







    private List<DJInfo> createList() {

        List<DJInfo> result = new ArrayList<DJInfo>();
        JSONArray jsonarray = null;
        String jsonStr="";


        try {
            jsonStr = new DJActivity.getDataURL().execute(Config.getBase_url()+"/dj").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        Log.d("JSON PLAIN TEXT:",jsonStr);


        if( jsonStr != null && Functions.isJSONValid(jsonStr))
        {

            try {
                jsonarray = new JSONArray(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("JSON:",""+jsonarray);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = null;
                try {
                    jsonobject = jsonarray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                DJInfo ci = new DJInfo();
                try {
                    ci.nameDJ = jsonobject.getString("nume");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    ci.pictureUrlDJ = jsonobject.getString("poza");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    ci.descriptionDJ = jsonobject.getString("descriere");
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                result.add(ci);
            }

        }else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this.getApplicationContext()).create();

                alertDialog.setTitle("Alert! No internet!");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                });

                alertDialog.show();
            }
            catch(Exception e)
            {
                Log.d("Dialog no internet: ", "Show Dialog: "+e.getMessage());
            }
        }
        return result;
    }





    public static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
