package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import in.alertmeu.R;
import in.alertmeu.adapter.BusinessReviewListAdpter;
import in.alertmeu.adapter.BusinessReviewListAdpterPage;
import in.alertmeu.adapter.OtherBusinessListAdpterPage;
import in.alertmeu.adapter.YouTubeListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.ShowAllReviewsDAO;
import in.alertmeu.models.YouTubeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.PaginationListener;
import in.alertmeu.utils.WebClient;

import static in.alertmeu.utils.PaginationListener.PAGE_START;

public class ShowAllReviewsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String business_id;
    TextView titleTxt;
    Resources res;
    RecyclerView faqList;
    JSONObject jsonLeadObj;
    JSONArray jsonArray;
    String myPlaceListResponse = "";
    List<ShowAllReviewsDAO> data;
    // BusinessReviewListAdpter businessReviewListAdpter;
    BusinessReviewListAdpterPage businessReviewListAdpter;
    ProgressDialog mProgressDialog;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    boolean status;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    @BindView(R.id.othBuinessList)
    RecyclerView mList;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_show_all_reviews);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        Intent intent = getIntent();
        business_id = intent.getStringExtra("business_id");
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText(intent.getStringExtra("title"));
        faqList = (RecyclerView) findViewById(R.id.learnMoreList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        data = new ArrayList<>();
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            synchronized (this) {
                swipeRefresh.setRefreshing(true);
                new getYouTubeList().execute();
            }
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }

        swipeRefresh.setOnRefreshListener(this);

        faqList.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(ShowAllReviewsActivity.this);
        faqList.setLayoutManager(layoutManager);

        businessReviewListAdpter = new BusinessReviewListAdpterPage(ShowAllReviewsActivity.this, new ArrayList<>());
        faqList.setAdapter(businessReviewListAdpter);

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        faqList.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                // doApiCall();
                if (AppStatus.getInstance(ShowAllReviewsActivity.this).isOnline()) {
                    synchronized (this) {
                        new getYouTubeList().execute();
                    }

                } else {

                    Toast.makeText(ShowAllReviewsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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

    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }

    @Override
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        businessReviewListAdpter.clear();
        if (AppStatus.getInstance(ShowAllReviewsActivity.this).isOnline()) {
            synchronized (this) {
                new getYouTubeList().execute();
            }

        } else {
            Toast.makeText(ShowAllReviewsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private class getYouTubeList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(ShowAllReviewsActivity.this);
            // Set progressdialog title
            //    mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //   mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("bid", business_id);
                        put("page", currentPage);
                        put("row_per_page", "20");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLREVIEWBUSINESSIDPAGE, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            data.clear();
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(myPlaceListResponse);
                                status = jsonObject.getBoolean("status");
                                totalPage = jsonObject.getInt("last_page");
                                JsonHelper jsonHelper = new JsonHelper();
                                if (status) {
                                    data = jsonHelper.parseReviewList(myPlaceListResponse);
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
                            Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            swipeRefresh.setRefreshing(false);
            if (data.size() > 0) {
                if (currentPage != PAGE_START) businessReviewListAdpter.removeLoading();
                businessReviewListAdpter.addItems(data);
                // check weather is last page or not
                if (currentPage < totalPage) {
                    businessReviewListAdpter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
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