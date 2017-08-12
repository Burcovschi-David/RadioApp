package ro.radioliberty.asculta.radioliberty;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.exoplayer2.ExoPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static ro.radioliberty.asculta.radioliberty.R.id.toolbar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static MediaPlayer mediaPlayer;
    static boolean isPlay=false;
    static Button mPlayButton;
    static WebView webview;
    SeekBar seekBar;
    static TextView currentplaying;
    private AudioManager audioManager = null;
    static ExoPlayer exoPlayer;
    static String baseurljson=null;
    static Handler timerHandler2 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Afisez toggle menu-ul
        //Creez toolbarul
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        initControls();
        currentplaying = (TextView) findViewById(R.id.currentPlaying);
        currentplaying.setSelected(true);
        webview=(WebView) findViewById(R.id.waveWebview);
        webview.loadDataWithBaseURL(null, "<html><head></head><body><img src=\"" + Config.getBase_url()+"/assets/images/animation.gif" + "\" style='width: 100%; height: auto;'></body></html>", "html/css", "utf-8", null);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        //disable scroll on touch
        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });



        mPlayButton = (Button) findViewById(R.id.controllPlay);
        // Default button, if need set it in xml via background="@drawable/default"
        mPlayButton.setBackgroundResource(R.mipmap.ic_play);
        mPlayButton.setOnClickListener(RadioStationAdapter.mTogglePlayButton);


        Functions.requestAllPermissions(MainActivity.this,MainActivity.this );

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        if(Functions.isReachableByTcp(Config.server_domain, 80, 4000)){
            RadioStationAdapter ca = new RadioStationAdapter(createList());
            recList.setAdapter(ca);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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

        //VErific din 10 in 10 secunde daca sunt solicitari noi
        final Handler timerHandler;
        timerHandler = new Handler();

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);


                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recList.setLayoutManager(llm);
                if(Functions.isReachableByTcp(Config.server_domain, 80, 400)==true) {
                    RadioStationAdapter ca = new RadioStationAdapter(createList());
                    recList.setAdapter(ca);
                }
                timerHandler.postDelayed(this, 30000);
            }
        };

        timerHandler.postDelayed(timerRunnable, 30000); //Start timer after 1 sec


        Runnable timerRunnable2 = new Runnable() {
            @Override
            public void run() {

                Functions.getCurrentPlaying();
                MainActivity.timerHandler2.postDelayed(this, 10000); //Start timer after 1 sec

            }
        };

        //MainActivity.timerHandler.removeCallbacks(timerRunnable);

        MainActivity.timerHandler2.postDelayed(timerRunnable, 10000); //Start timer after 1 sec

        /*// show The Image in a ImageView
        new DownloadImageTask((ImageView) findViewById(R.id.banner))
                .execute(Config.getBase_url()+"/assets/images/banner.png");*/


    }




    private void initControls()
    {
        try
        {
            seekBar = (SeekBar)findViewById(R.id.seekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            seekBar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            seekBar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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







    private List<RadioStationInfo> createList() {

        List<RadioStationInfo> result = new ArrayList<RadioStationInfo>();
        JSONArray jsonarray = null;
        String jsonStr="";


        try {
            jsonStr = new MainActivity.getDataURL().execute(Config.getBase_url()+"/get-channels").get();
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


                RadioStationInfo ci = new RadioStationInfo();
                try {
                    ci.name = jsonobject.getString("nume");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    ci.pictograma = jsonobject.getString("imagine");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    ci.url = jsonobject.getString("url");
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












    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.contact){
            Intent i=new Intent(MainActivity.this,Contact.class);
            startActivity(i);
        }else if(id == R.id.dedicatii){
            Intent i=new Intent(MainActivity.this,Dedicatii.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
