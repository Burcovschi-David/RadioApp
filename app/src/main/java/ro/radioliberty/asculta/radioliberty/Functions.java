package ro.radioliberty.asculta.radioliberty;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by davidburcovschi on 08/05/2017.
 */

public class Functions {


        public static String getBaseURL(String url) throws URISyntaxException {
            URI uri = new URI(url);
            String domain = uri.getHost();
            int port = uri.getPort();
            return domain+":"+port;
        }


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isReachableByTcp(String host, int port, int timeout) {
        String port2=""+port;
        String timeout2=""+timeout;
        try {
            return new IsReachableByTcp().execute(host,port2,timeout2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;

        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;

        }
    }




    public static class IsReachableByTcp extends AsyncTask<String, Integer,Boolean> {



        protected Boolean doInBackground(String... params) {


            try {
                Socket socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(params[0], Integer.parseInt(params[1]));
                socket.connect(socketAddress, Integer.parseInt(params[2]));
                socket.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            // here you have the result

            super.onPostExecute(result);
        }
    }




    public static boolean isReachableByPing(String host) {
        try{
            String cmd = "";

            if(System.getProperty("os.name").startsWith("Windows"))
                cmd = "cmd /C ping -n 1 " + host + " | find \"TTL\"";
            else
                cmd = "ping -c 1 " + host;

            Process myProcess = Runtime.getRuntime().exec(cmd);
            myProcess.waitFor();

            return myProcess.exitValue() == 0;
        } catch( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    public static void requestAllPermissions(Context activity, Activity activity2){



        /********Permisiune INTERNET**********/
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity2,
                    Manifest.permission.INTERNET)) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Aplicatia are nevoie de internet pentru a functiona corect");
                alertDialog.setMessage("Te rugam sa permiti aplicatiei sa aiba acces la internet");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity2,
                        new String[]{Manifest.permission.INTERNET},
                        2106);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }










        /********Permisiune accesare stare retea**********/
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity2,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Aplicatia are nevoie de starea retelei pentru a functiona corect");
                alertDialog.setMessage("Te rugam sa permiti aplicatiei sa aiba acces la starea retelei");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity2,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        2106);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        /********Permisiune accesare locatie precisa**********/
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity2,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Aplicatia are nevoie de locatia ta precisa pentru a functiona corect");
                alertDialog.setMessage("Te rugam sa permiti aplicatiei sa aiba acces la locatia ta precisa");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity2,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2106);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }




        /********Permisiune accesare locatie aproximativa**********/
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity2,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Aplicatia are nevoie de locatia ta aproximativa pentru a functiona corect");
                alertDialog.setMessage("Te rugam sa permiti aplicatiei sa aiba acces la locatia ta aproximativa");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity2,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2106);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



    }




    public static class PostDataUrl extends AsyncTask<String, Integer, String> {

        List<PostParameter> params2 = new ArrayList<PostParameter>();
        String url2;


        public PostDataUrl(String url2, List<PostParameter> params2) {
            this.url2 = url2;

            this.params2 = params2;
        }


        protected String doInBackground(String... params) {


            String output = null;
            MultipartPost post = new MultipartPost(params2);
            try {
                output = post.send(url2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return output;
        }

        protected void onPostExecute(String result) {
            // here you have the result

            super.onPostExecute(result);
        }
    }


    static String postRequest(String url2, List<PostParameter> data2) {
        Functions.PostDataUrl postData = new Functions.PostDataUrl(url2, data2);
        String output = "none";
        try {
            output = postData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("OUTPUT POST:", "" + output);

        return output;
    }


    String getRequest(String url) {
        String result = "none";
        try {
            result = new getDataURL().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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


    public class getDataURL extends AsyncTask<String, Integer, String> {

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


    void LongLog(String TAG, String veryLongString) {
        int maxLogSize = 1000;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.v(TAG, veryLongString.substring(start, end));
        }

    }

    public static boolean isConnectedToServer(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    public static int isInternetAvailable(Context context) {
        String TAG = "Check internet: ";
        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            return 0;
        } else {
            if (info.isConnected()) {
                Log.d(TAG, " internet connection available...");
                return 1;
            } else if (isConnectedToServer(Config.getBase_url(), 60000)) {
                Log.d(TAG, "No server connection2");
                return -1;
            } else {
                Log.d(TAG, " internet connection");
                return 0;
            }

        }
    }

    static String dataNow() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy--HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }



    static void getCurrentPlaying()  {

//http://asculta.radioliberty.ro:1989/statistics?json=1


        JSONObject jsonobject = null;
        String jsonStr = "";




        try {
            MainActivity.baseurljson=Functions.getBaseURL(RadioStationAdapter.currentPlayingURLJSON);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        Log.d("CURRENT PLAYING:","CURRENT PLAYING: "+"http://"+MainActivity.baseurljson+"/statistics?json=1");
        try {
            jsonStr = new RadioStationAdapter.getDataURL().execute("http://"+MainActivity.baseurljson+"/statistics?json=1").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        Log.d("JSON PLAIN TEXT:", jsonStr);


        if (jsonStr != null  && Functions.isJSONValid(jsonStr)) {

            try {
                jsonobject = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray jsonArray = null;
            try {
                jsonArray = jsonobject.getJSONArray("streams");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject streamsObject = null;

            try {
                streamsObject = jsonArray.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();

            }

            String currentPlayingString = null;

            try {
                currentPlayingString = streamsObject.getString("songtitle");
                Log.d("SONG TITLE:", streamsObject.getString("songtitle"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(RadioStationAdapter.currentPlayingURLJSON==""){
                MainActivity.currentplaying.setText("---");
            }else {
                MainActivity.currentplaying.setText(currentPlayingString);

            }
        }
    }

}
