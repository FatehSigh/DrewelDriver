package com.octalsoftware.drewel.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.interfaces.AsyncGetDirection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class GetdirectionAsync extends AsyncTask<Void, ProgressDialog, String> {

    public Context mcontext;
    public AsyncGetDirection onTaskCompleted;
    public String mUrl;
    public Double sLatitude, sLongitude;
    public Double dLatitude, dLongitude;
    String mode;
    private ArrayList<LatLng> listLatLng;
    private String state = "Successful main";

    public GetdirectionAsync(Context con, AsyncGetDirection mAsyncGetDirection,
                             Double sLatitude, Double sLongitude, Double dLatitude,
                             Double dLongitude, String mode) {
        this.mcontext = con;
        this.onTaskCompleted = mAsyncGetDirection;
        this.sLatitude = sLatitude;
        this.sLongitude = sLongitude;
        this.dLatitude = dLatitude;
        this.dLongitude = dLongitude;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... param) {
        try {
            mUrl = "http://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + sLatitude + "," + sLongitude
                    + "&destination=" + dLatitude + "," + dLongitude
                    + "&sensor=false&units=metric&mode=driving";

            HttpClient mHttpClient = new DefaultHttpClient(
                    getHttpParameters());
            HttpContext mHttpContext = new BasicHttpContext();

            mUrl = mUrl.replace(" ", "%20");
            HttpGet mHttpget = new HttpGet(mUrl.trim());

            HttpResponse mHttpResponse = mHttpClient.execute(mHttpget,
                    mHttpContext);
            if (mHttpResponse != null) {
                HttpEntity mHttpEntity = mHttpResponse.getEntity();
                InputStream inStream = mHttpEntity.getContent();
                if (inStream != null) {
                    String result = convertStreamToString(inStream);

                    if (result != null && result.length() > 0) {

                        return result;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        listLatLng = new ArrayList<LatLng>();

        if (result == null) {
            state = "UnSuceessful";
            onTaskCompleted.OnDirectionPathCompleted(listLatLng, state);
            return;
        }
        try {
            JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            listLatLng.addAll(decodePoly(encodedString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listLatLng != null && listLatLng.size() != 0) {
            onTaskCompleted.OnDirectionPathCompleted(listLatLng, state);
        } else {
            state = "UnSuceessful";
            onTaskCompleted.OnDirectionPathCompleted(listLatLng, state);
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public static HttpParams getHttpParameters() {
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, 20000);
        return httpParameters;
    }
}
