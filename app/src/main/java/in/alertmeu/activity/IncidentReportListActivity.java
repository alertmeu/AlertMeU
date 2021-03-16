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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import butterknife.BindView;
import in.alertmeu.R;
import in.alertmeu.adapter.IncidentListAdpter;
import in.alertmeu.adapter.IncidentListAdpterPage;
import in.alertmeu.adapter.OtherBusinessListAdpterPage;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.PaginationListener;
import in.alertmeu.utils.WebClient;

import static in.alertmeu.utils.PaginationListener.PAGE_START;

public class IncidentReportListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
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
    IncidentListAdpterPage incidentListAdpter;
    LinearLayout noadshid, btnshopPrec;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    @BindView(R.id.incidentList)
    RecyclerView mList;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    double latitude = 0.0;
    double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_incident_report_list);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        noadshid = (LinearLayout) findViewById(R.id.noadshid);
        incidentDAOList = new ArrayList<>();
        incidentList = (RecyclerView) findViewById(R.id.incidentList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        btnshopPrec = (LinearLayout) findViewById(R.id.btnshopPrec);
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

        if (AppStatus.getInstance(IncidentReportListActivity.this).isOnline()) {
            synchronized (this) {
                swipeRefresh.setRefreshing(true);
                new getAdvertisement().execute();

            }

        } else {

            Toast.makeText(IncidentReportListActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }

        swipeRefresh.setOnRefreshListener(this);

        incidentList.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(IncidentReportListActivity.this);
        incidentList.setLayoutManager(layoutManager);

        incidentListAdpter = new IncidentListAdpterPage(IncidentReportListActivity.this, new ArrayList<>());
        incidentList.setAdapter(incidentListAdpter);

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        incidentList.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                // doApiCall();
                if (AppStatus.getInstance(IncidentReportListActivity.this).isOnline()) {
                    synchronized (this) {
                        new getAdvertisement().execute();
                    }

                } else {

                    Toast.makeText(IncidentReportListActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        btnshopPrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(IncidentReportListActivity.this).isOnline()) {
                    Intent intent = new Intent(IncidentReportListActivity.this, IncidentTypeActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(IncidentReportListActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        incidentListAdpter.clear();
        if (AppStatus.getInstance(IncidentReportListActivity.this).isOnline()) {
            synchronized (this) {
                new getAdvertisement().execute();
            }

        } else {

            Toast.makeText(IncidentReportListActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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
                        put("latitude", latitude);
                        put("longitude", longitude);
                        put("title", "");
                        put("distance", "" + preferences.getInt("units_for_area", 0));
                        put("currentdays", days);
                        put("user_id", preferences.getString("user_id", ""));
                        put("t_zone", localTime);
                        put("page", currentPage);
                        put("row_per_page", "20");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            incListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLINCADSBYIDPAGE, jsonLeadObj1);
            Log.i("resp", "incListResponse" + incListResponse);

            try {
                if (isJSONValid(incListResponse)) {

                    try {
                        JSONObject jsonObject = new JSONObject(incListResponse);
                        status = jsonObject.getBoolean("status");
                        totalPage = jsonObject.getInt("last_page");
                        if (status) {
                            JsonHelper jsonHelper = new JsonHelper();
                            incidentDAOList = jsonHelper.parseAllIncList(incListResponse);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {


                    return null;
                }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            swipeRefresh.setRefreshing(false);
            if (incidentDAOList.size() > 0) {
                noadshid.setVisibility(View.GONE);
                if (currentPage != PAGE_START) incidentListAdpter.removeLoading();
                incidentListAdpter.addItems(incidentDAOList);
                // check weather is last page or not
                if (currentPage < totalPage) {
                    incidentListAdpter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            } else {
                swipeRefresh.setVisibility(View.GONE);
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