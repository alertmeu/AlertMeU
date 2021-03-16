package in.alertmeu.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import in.alertmeu.adapter.IncidentListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class IncidentReportListActivity1 extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    int days = 0;
    String localTime;
    Resources res;
    JSONObject jsonLeadObj1;
    String incListResponse = "";
    boolean status;
    RecyclerView incidentList;
    List<IncidentDAO> incidentDAOList;
    IncidentListAdpter incidentListAdpter;
    LinearLayout noadshid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_incident_report_list);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        incidentList = (RecyclerView) findViewById(R.id.incidentList);
        noadshid=(LinearLayout)findViewById(R.id.noadshid);
        incidentDAOList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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

        if (AppStatus.getInstance(IncidentReportListActivity1.this).isOnline()) {
            synchronized (this) {

                new getAdvertisement().execute();

            }

        } else {

            Toast.makeText(IncidentReportListActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(ViewIncDescriptionActivity.this);
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
            jsonLeadObj1 = new JSONObject() {
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
            Log.i("json", "json" + jsonLeadObj1);
            incListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLINCADSBYID, jsonLeadObj1);
            Log.i("resp", "incListResponse" + incListResponse);

            try {
                if (isJSONValid(incListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(incListResponse);
                                status = jsonObject.getBoolean("status");
                                if (status) {
                                    JsonHelper jsonHelper = new JsonHelper();
                                    incidentDAOList = jsonHelper.parseAllIncList(incListResponse);
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
            // mProgressDialog.dismiss();
            // Toast.makeText(getApplicationContext(), "" + advertisementDAOList.size(), Toast.LENGTH_SHORT).show();
            if (incidentDAOList.size() > 0) {
                noadshid.setVisibility(View.GONE);
                incidentListAdpter = new IncidentListAdpter(IncidentReportListActivity1.this, incidentDAOList);
                incidentList.setAdapter(incidentListAdpter);
                incidentList.setLayoutManager(new LinearLayoutManager(IncidentReportListActivity1.this));
            } else {
                noadshid.setVisibility(View.VISIBLE);
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
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
}