package in.alertmeu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.activity.CheckIngLoadActivity;
import in.alertmeu.jsonparser.JsonHelper;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    JSONObject jsonLeadObj;
    String latitude;
    String longitude;
    String title;
    String distance;
    String days;
    String user_id;
    String redo_s = "0";
    Context context;
    String localTime;
    int page_no = 1, total_pages;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    boolean status;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            preferences = MyApplicatio.getInstance().getSharedPreferences("Prefrence", MyApplicatio.getInstance().MODE_PRIVATE);
            prefEditor = preferences.edit();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
            Date currentLocalTime = calendar.getTime();
            DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
            localTime = date.format(currentLocalTime);
            googleMap = (GoogleMap) inputObj[0];
            title = (String) inputObj[1];
            distance = (String) inputObj[2];
            days = (String) inputObj[3];
            context = (Context) inputObj[4];
            user_id = (String) inputObj[5];
            if (days.equals("0")) {
                days = "1";
            }
            jsonLeadObj = new JSONObject() {
                {
                    try {
                        // put("latitude", latitude);
                        // put("longitude", longitude);
                        put("latitude", preferences.getString("clat", ""));
                        put("longitude", preferences.getString("clong", ""));
                        put("title", title);
                        put("distance", distance);
                        put("currentdays", days);
                        put("user_id", user_id);
                        put("t_zone", localTime);
                        put("page", preferences.getInt("page_no", 0));
                        put("row_per_page", 20);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            googlePlacesData = serviceAccess.SendHttpPost(Config.URL_GETALLBUSINESSLOCATIONDATAPAGE, jsonLeadObj);
            // If the AsyncTask cancelled

            try {

                JSONObject jsonObject = new JSONObject(googlePlacesData);
                total_pages = jsonObject.getInt("total_pages");
                status = jsonObject.getBoolean("status");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        if (status == true) {
            PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
            Object[] toPass = new Object[3];
            toPass[0] = googleMap;
            toPass[1] = result;
            toPass[2] = context;
            placesDisplayTask.execute(toPass);

            if (preferences.getString("stop", "").equals("1")) {
                if (placesDisplayTask != null && placesDisplayTask.getStatus() != AsyncTask.Status.FINISHED)
                    placesDisplayTask.cancel(true);
            }
            page_no = preferences.getInt("page_no", 0);

            //  Toast.makeText(context, "" + page_no, Toast.LENGTH_SHORT).show();
            if (page_no <= total_pages) {
                page_no = preferences.getInt("page_no", 0) + 1;
                prefEditor.putInt("page_no", page_no);
                prefEditor.commit();
                if (AppStatus.getInstance(context).isOnline()) {
                    synchronized (this) {
                        try {
                            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                            Object[] toPass1 = new Object[6];
                            toPass1[0] = googleMap;
                            toPass1[1] = title;
                            toPass1[2] = "" + distance;
                            toPass1[3] = "" + days;
                            toPass1[4] = context;
                            toPass1[5] = user_id;

                            googlePlacesReadTask.execute(toPass1);
                            if (preferences.getString("stop", "").equals("1")) {
                                if (googlePlacesReadTask != null && googlePlacesReadTask.getStatus() != AsyncTask.Status.FINISHED)
                                    googlePlacesReadTask.cancel(true);
                            }
                        } catch (Exception e) {

                        }
                    }

                }

            } else {

            }
        } else {
            if (total_pages == 0) {
                PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
                Object[] toPass = new Object[3];
                toPass[0] = googleMap;
                toPass[1] = result;
                toPass[2] = context;
                placesDisplayTask.execute(toPass);
            }
           /* if (total_pages > 1) {
                total_pages = 0;
                googleMap.clear();
            }*/
        }


    }
}
