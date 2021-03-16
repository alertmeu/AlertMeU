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
import android.widget.TextView;
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
import in.alertmeu.adapter.AllActiveAdsMidListAdpterPage;
import in.alertmeu.adapter.OtherBusinessListAdpterPage;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.PaginationListener;
import in.alertmeu.utils.WebClient;

import static in.alertmeu.utils.PaginationListener.PAGE_START;

public class DisplayAllActiveAdsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    JSONObject jsonLeadObjOthBus;
    int days = 0;
    String googlePlacesData = "";
    String localTime;
    boolean statusob;
    List<AdvertisementAllBusDAO> data = null;
    AllActiveAdsMidListAdpterPage allActiveAdsMidListAdpterPage;
    RecyclerView othBuinessList;
    Resources res;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    @BindView(R.id.othBuinessList)
    RecyclerView mList;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    TextView titleTxt;
    String mainCatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_display_all_active_ads);
        Intent intent = getIntent();
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText(intent.getStringExtra("name"));
        mainCatId = intent.getStringExtra("mainCatId");
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        data = new ArrayList<>();
        othBuinessList = (RecyclerView) findViewById(R.id.allActiveAdsList);
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
        if (AppStatus.getInstance(DisplayAllActiveAdsActivity.this).isOnline()) {
            synchronized (this) {
                swipeRefresh.setRefreshing(true);
                new getAdvertisement().execute();
            }

        } else {

            Toast.makeText(DisplayAllActiveAdsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }

        swipeRefresh.setOnRefreshListener(this);

        othBuinessList.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(DisplayAllActiveAdsActivity.this);
        othBuinessList.setLayoutManager(layoutManager);

        allActiveAdsMidListAdpterPage = new AllActiveAdsMidListAdpterPage(DisplayAllActiveAdsActivity.this, new ArrayList<>());
        othBuinessList.setAdapter(allActiveAdsMidListAdpterPage);

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        othBuinessList.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                // doApiCall();
                if (AppStatus.getInstance(DisplayAllActiveAdsActivity.this).isOnline()) {
                    synchronized (this) {
                        new getAdvertisement().execute();
                    }

                } else {

                    Toast.makeText(DisplayAllActiveAdsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        allActiveAdsMidListAdpterPage.clear();
        if (AppStatus.getInstance(DisplayAllActiveAdsActivity.this).isOnline()) {
            synchronized (this) {
                new getAdvertisement().execute();
            }

        } else {

            Toast.makeText(DisplayAllActiveAdsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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
                        put("page", currentPage);
                        put("mcatid", mainCatId);
                        put("row_per_page", "20");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            googlePlacesData = serviceAccess.SendHttpPost(Config.URL_GETALLACTIVEADSMIDDATAPAGE, jsonLeadObjOthBus);
            Log.i("resp", "googlePlacesData" + googlePlacesData);

            try {

                if (isJSONValid(googlePlacesData)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(googlePlacesData);
                                statusob = jsonObject.getBoolean("status");
                                // totalPage = jsonObject.getInt("total_pages");
                                totalPage = jsonObject.getInt("last_page");
                                // data.clear();
                                if (statusob) {
                                    JsonHelper jsonHelper = new JsonHelper();
                                    //  data = jsonHelper.parseAllOtherBusineesList(googlePlacesData);
                                    data = jsonHelper.parseAllAdvertisementList(googlePlacesData);
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
            swipeRefresh.setRefreshing(false);
            if (data.size() > 0) {
                if (currentPage != PAGE_START) allActiveAdsMidListAdpterPage.removeLoading();
                allActiveAdsMidListAdpterPage.addItems(data);
                // check weather is last page or not
                if (currentPage < totalPage) {
                    allActiveAdsMidListAdpterPage.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            } else {
                // swipeRefresh.setVisibility(View.GONE);
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