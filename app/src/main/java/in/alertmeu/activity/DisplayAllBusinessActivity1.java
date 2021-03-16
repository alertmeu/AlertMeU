package in.alertmeu.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.R;
import in.alertmeu.adapter.OtherBusinessListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class DisplayAllBusinessActivity1 extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    JSONObject jsonLeadObjOthBus;
    int days = 0;
    String googlePlacesData = "";
    String localTime;
    boolean statusob;
    List<LocationDAO> data = null;
    OtherBusinessListAdpter otherBusinessListAdpter;
    RecyclerView othBuinessList;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_display_all_business);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        data = new ArrayList<>();
        othBuinessList = (RecyclerView) findViewById(R.id.othBuinessList);
        if (preferences.getString("today", "").equals("true")) {
            days += 1;
        }
        if (preferences.getString("tomorrow", "").equals("true")) {
            days += 2;
        }
        if (preferences.getString("oneweek", "").equals("true")) {
            days = 7;
        }
        if (preferences.getString("twoweeks", "").equals("true")) {
            days = 14;
        }
        if (AppStatus.getInstance(DisplayAllBusinessActivity1.this).isOnline()) {
            synchronized (this) {
                new getAdvertisement().execute();
            }

        } else {

            Toast.makeText(DisplayAllBusinessActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }

    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(BusinessProfileActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (days == 0) {
                days = 1;
            }


            jsonLeadObjOthBus = new JSONObject() {
                {
                    try {
                        put("latitude", preferences.getString("favlat", ""));
                        put("longitude", preferences.getString("favlong", ""));
                        put("title", "");
                        put("distance", "" + preferences.getInt("units_for_area", 0));
                        put("currentdays", days);
                        put("user_id", preferences.getString("user_id", ""));
                        put("t_zone", localTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();

            googlePlacesData = serviceAccess.SendHttpPost(Config.URL_GETALLCURRENTACTOTHERBUSINESSES, jsonLeadObjOthBus);
            Log.i("resp", "googlePlacesData" + googlePlacesData);

            try {

                if (isJSONValid(googlePlacesData)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(googlePlacesData);
                                statusob = jsonObject.getBoolean("status");
                                if (statusob) {
                                    data.clear();
                                    JsonHelper jsonHelper = new JsonHelper();
                                    data = jsonHelper.parseAllOtherBusineesList(googlePlacesData);
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (data.size() > 0) {
                otherBusinessListAdpter = new OtherBusinessListAdpter(DisplayAllBusinessActivity1.this, data);
                othBuinessList.setAdapter(otherBusinessListAdpter);
                othBuinessList.setLayoutManager(new LinearLayoutManager(DisplayAllBusinessActivity1.this));
            } else {


            }
        }
    }

    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}