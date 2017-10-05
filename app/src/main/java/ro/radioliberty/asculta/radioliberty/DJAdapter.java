package ro.radioliberty.asculta.radioliberty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;


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
import java.util.List;


/**
 * Created by davidburcovschi on 06/04/2017.
 */


public class DJAdapter extends RecyclerView.Adapter<DJAdapter.DJViewHolder> {

    static String curUrl = "";

    public static List<DJInfo> DJList;

    public DJAdapter(List<DJInfo> DJList) {
        this.DJList = DJList;
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
        return DJList.size();
    }

    static String currentPlayingURLJSON="";

    static DJInfo ci;
    @Override
    public void onBindViewHolder(final DJViewHolder DJViewHolder, int i) {

        ci = DJList.get(i);
        final String nameDJ=ci.nameDJ;
        final String descriptionDJ=ci.descriptionDJ;
        final String pictureURLDJ = ci.pictureUrlDJ;



        DJViewHolder.DJnume.setText(ci.nameDJ);

        DJViewHolder.DJdescriere.setText(ci.descriptionDJ);
        new ImageLoadTask(pictureURLDJ, DJViewHolder.DJpoza).execute();





    }






    @Override
    public DJViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.dj_cell, viewGroup, false);
        Log.d("Adauga card-urile", "CARDURI!");
        return new DJViewHolder(itemView);
    }








    public static class DJViewHolder extends RecyclerView.ViewHolder {

        protected TextView DJnume;

        protected ImageView DJpoza;

        protected TextView DJdescriere;

        public DJViewHolder(View v) {
            super(v);
            DJnume = (TextView) v.findViewById(R.id.nume);

            DJpoza = (ImageView) v.findViewById(R.id.poza);
            DJdescriere = (TextView) v.findViewById(R.id.descriere);


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










