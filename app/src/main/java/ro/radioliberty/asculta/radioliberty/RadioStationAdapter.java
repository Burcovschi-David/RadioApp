package ro.radioliberty.asculta.radioliberty;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by davidburcovschi on 06/04/2017.
 */


public class RadioStationAdapter extends RecyclerView.Adapter<RadioStationAdapter.RadioStationViewHolder> {

    static String curUrl = "";

    public static List<RadioStationInfo> radioStationList;

    public RadioStationAdapter(List<RadioStationInfo> radioStationList) {
        this.radioStationList = radioStationList;
    }


    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
        private ImageView logo;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }


    @Override
    public int getItemCount() {
        return radioStationList.size();
    }

    static String currentPlayingURLJSON="";

    static RadioStationInfo ci;
    @Override
    public void onBindViewHolder(final RadioStationViewHolder radioStationViewHolder, int i) {

        ci = radioStationList.get(i);
        final String titlu = ci.name;
        final String imageURL = ci.pictograma;
        final String url = ci.url;
        curUrl = ci.url;



        radioStationViewHolder.vTitle.setText(ci.name);


        new ImageLoadTask(imageURL, radioStationViewHolder.thumbnail).execute();


        radioStationViewHolder.playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MainActivity.curPlayngURL=url;
                //notice I implemented onClickListener here
                // so I can associate this click with final Item item
                if(Functions.isReachableByTcp(Config.server_domain, 80, 400)==true) {
                    MainActivity.isPlay = true;

                    Log.d("Clicked!", titlu);
                    if (MainActivity.exoPlayer != null) {
                        if (MainActivity.exoPlayer.getPlayWhenReady()) {

                            Log.d("MEDIA PLAYER:", "STOPPED!!");
                            MainActivity.exoPlayer.stop();
                            MainActivity.exoPlayer.release();
                        }
                    }

                    MainActivity.currentRadioChannelPlaying=titlu;

                    Context context = view.getContext();


// show The Image in a ImageView
                    // show The Image in a ImageView

                    // new RadioStationAdapter.PlayStreamTask(view.getContext()).execute(url);
                    initMediaPlayer(view.getContext(), url);
                    currentPlayingURLJSON=url;

                    MainActivity.notificationView.setImageViewResource(R.id.closeOnFlash, R.mipmap.ic_stop);
                    MainActivity.mPlayButton.setBackgroundResource(R.mipmap.ic_stop);
                    Functions.getCurrentPlaying(view.getContext());
                    //VErific din 10 in 10 secunde daca sunt solicitari noi
//Display notification

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(21061999);
                    MainActivity.notificationManager.notify(21061999, MainActivity.notification);



                }

            }


        });


        radioStationViewHolder.vTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //notice I implemented onClickListener here
                // so I can associate this click with final Item item
                MainActivity.isPlay = true;
                MainActivity.curPlayngURL=url;
                MainActivity.currentRadioChannelPlaying=titlu;
                initMediaPlayer(view.getContext(), url);
                Log.d("Clicked!", titlu);
                if (MainActivity.exoPlayer != null) {
                    if (MainActivity.exoPlayer.getPlayWhenReady()) {

                        Log.d("MEDIA PLAYER:", "STOPPED!!");
                        MainActivity.exoPlayer.stop();
                        MainActivity.exoPlayer.release();
                    }
                }
                Context context = view.getContext();
// show The Image in a ImageView
                // show The Image in a ImageView

                // new RadioStationAdapter.PlayStreamTask(view.getContext()).execute(url);
                initMediaPlayer(view.getContext(), url);
                MainActivity.mPlayButton.setBackgroundResource(R.mipmap.ic_stop);

                MainActivity.notificationView.setImageViewResource(R.id.closeOnFlash, R.mipmap.ic_stop);
                if(Functions.isReachableByTcp(Config.server_domain, 80, 400)==true) {
                    Functions.getCurrentPlaying(view.getContext());
                }

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(21061999);
                MainActivity.notificationManager.notify(21061999, MainActivity.notification);



            }

        });


    }





    static View.OnClickListener mTogglePlayButton = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // change your button background
            if (MainActivity.isPlay && MainActivity.exoPlayer != null) {

                v.setBackgroundResource(R.mipmap.ic_play);
                MainActivity.notification.flags |= Notification.FLAG_AUTO_CANCEL;
                MainActivity.notificationView.setImageViewResource(R.id.closeOnFlash, R.mipmap.ic_play);
                MainActivity.webview.setVisibility(View.INVISIBLE);
                MainActivity.exoPlayer.stop();
            } else if (MainActivity.exoPlayer != null) {
                v.setBackgroundResource(R.mipmap.ic_play);
                MainActivity.webview.setVisibility(View.VISIBLE);
                v.setBackgroundResource(R.mipmap.ic_stop);

                MainActivity.notificationView.setImageViewResource(R.id.closeOnFlash, R.mipmap.ic_stop);
                initMediaPlayer(v.getContext(), curUrl);

            }

            NotificationManager notificationManager = (NotificationManager) v.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(21061999);
            MainActivity.notificationManager.notify(21061999, MainActivity.notification);


            MainActivity.isPlay = !MainActivity.isPlay; // reverse

        }

    };


    @Override
    public RadioStationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.canale_cell, viewGroup, false);
        Log.d("Adauga card-urile", "CARDURI!");
        return new RadioStationViewHolder(itemView);
    }


    public static void initMediaPlayer(Context context, String url1) {
        Handler mHandler = new Handler();

        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";
        Uri uri = Uri.parse(url1);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                mHandler, null);

        TrackSelector trackSelector = new DefaultTrackSelector(mHandler);
        DefaultLoadControl loadControl = new DefaultLoadControl();
        MainActivity.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
        //exoPlayer.addListener(context);

        MainActivity.exoPlayer.prepare(mediaSource);

        MainActivity.exoPlayer.setPlayWhenReady(true);
        MainActivity.webview.setVisibility(View.VISIBLE);


    }

    private class PlayStreamTask extends AsyncTask<String, Void, Void> {
        private Context mContext;

        public PlayStreamTask(Context context) {
            mContext = context;
        }

        protected Void doInBackground(String... urls) {
            String url = urls[0];
           /* MainActivity.mediaPlayer = MediaPlayer.create(mContext, Uri.parse(url));
            MainActivity.mediaPlayer.start();
*/
            if(Functions.isReachableByTcp(Config.server_domain, 80, 400)==true) {
                initMediaPlayer(mContext, url);
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {


        }
    }


    public static class RadioStationViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;

        protected ImageView thumbnail;

        protected ImageButton playButton;

        public RadioStationViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.titlu);

            thumbnail = (ImageView) v.findViewById(R.id.pictograma);
            playButton = (ImageButton) v.findViewById(R.id.playButton);


        }
    }


    public static class getDataURL extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            String jsonStr = "";


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










