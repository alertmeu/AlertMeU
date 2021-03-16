package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.Fragment.IncidentTypeListAdpter;
import in.alertmeu.R;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.IncidentTypeModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;
import in.alertmeu.view.IncidentSubTypeView;

public class IncidentTypeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    RecyclerView mainCatList;
    JSONObject jsonLeadObj, jsonSchedule;
    JSONArray jsonArray;
    String myPlaceListResponse = "", imagePathResponse = "";
    List<IncidentTypeModeDAO> data;
    IncidentTypeListAdpter incidentTypeListAdpter;
    LinearLayout btnNext, shmsg;
    Resources res;
    boolean status;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_incident_type);
        mainCatList = (RecyclerView) findViewById(R.id.mainCatList);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
        shmsg = (LinearLayout) findViewById(R.id.shmsg);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        data = new ArrayList<>();
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        swipeRefresh.setOnRefreshListener(this);
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            swipeRefresh.setRefreshing(true);
            synchronized (this) {
                new getMyPlaceList().execute();
                getImagePath();
            }
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(IncidentTypeActivity.this, HomePageActivity.class);
                    startActivity(intent);

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        IncidentSubTypeView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    swipeRefresh.setRefreshing(true);
                    synchronized (this) {
                        new getMyPlaceList().execute();
                        getImagePath();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            // swipeRefresh.setRefreshing(true);
            synchronized (this) {
                new getMyPlaceList().execute();
                getImagePath();
            }
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private class getMyPlaceList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("country_code", preferences.getString("country_code", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLINCIDENTTYPE, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            data.clear();
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {
                    try {
                        JsonHelper jsonHelper = new JsonHelper();
                        data = jsonHelper.parseMyIncTypeList(myPlaceListResponse);
                        jsonArray = new JSONArray(myPlaceListResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    return null;
                }
            } else {
                Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            swipeRefresh.setRefreshing(false);
            if (data.size() > 0) {
                incidentTypeListAdpter = new IncidentTypeListAdpter(IncidentTypeActivity.this, data);
                mainCatList.setAdapter(incidentTypeListAdpter);
                mainCatList.setLayoutManager(new LinearLayoutManager(IncidentTypeActivity.this));
                incidentTypeListAdpter.notifyDataSetChanged();

            } else {

            }
        }
    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("user_id", preferences.getString("user_id", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("json exception", "json exception" + e);
                }
            }
        };


        Thread objectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonSchedule);
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_CUIPID, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                shmsg.setVisibility(View.GONE);
                                btnNext.setVisibility(View.VISIBLE);
                            } else {
                                btnNext.setVisibility(View.GONE);
                                shmsg.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });


            }
        });

        objectThread.start();

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
