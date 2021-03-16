package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

import in.alertmeu.R;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class GetNotifyDataActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String localTime;
    int days = 0;
    JSONObject jsonLeadObj1;
    int id;
    String advertisementListResponse = "";
    boolean status;
    List<AdvertisementAllBusDAO> advertisementDAOList;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        res = getResources();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notify_data);
        Intent intent = getIntent();
        id = Integer.parseInt(intent.getStringExtra("id"));
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
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
        advertisementDAOList = new ArrayList<>();
        if (AppStatus.getInstance(GetNotifyDataActivity.this).isOnline()) {
            new getAdvertisement().execute();
        } else {

            Toast.makeText(GetNotifyDataActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (days == 0) {
                days = 1;
            }
            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("latitude", preferences.getString("favlat", ""));
                        put("longitude", preferences.getString("favlong", ""));
                        put("distance", "" + preferences.getInt("units_for_area", 0));
                        put("t_zone", localTime);
                        put("currentdays", days);
                        put("id", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETSINGLECURRENTACTADSOTHCATBUSINESSID, jsonLeadObj1);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            try {
                if (isJSONValid(advertisementListResponse)) {

                    try {
                        JSONObject jsonObject = new JSONObject(advertisementListResponse);
                        status = jsonObject.getBoolean("status");
                        if (status) {
                            JsonHelper jsonHelper = new JsonHelper();
                            advertisementDAOList = jsonHelper.parseAllAdvertisementList(advertisementListResponse);
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
            if (advertisementDAOList.size() > 0) {
                Intent intent = new Intent(GetNotifyDataActivity.this, ViewImageDescriptionActivity.class);
                intent.putExtra("id", advertisementDAOList.get(0).getId());
                intent.putExtra("bid", advertisementDAOList.get(0).getBusiness_user_id());
                intent.putExtra("mcatid", advertisementDAOList.get(0).getBusiness_main_category_id());
                intent.putExtra("qrCode", advertisementDAOList.get(0).getRq_code());
                intent.putExtra("lat", "" + advertisementDAOList.get(0).getLatitude());
                intent.putExtra("long", "" + advertisementDAOList.get(0).getLongitude());
                intent.putExtra("imagePath", advertisementDAOList.get(0).getOriginal_image_path());
                intent.putExtra("imageCompanyPath", advertisementDAOList.get(0).getCompany_logo());
                intent.putExtra("bname", advertisementDAOList.get(0).getBusiness_name());
                intent.putExtra("title", advertisementDAOList.get(0).getTitle());
                intent.putExtra("description", advertisementDAOList.get(0).getDescription());
                intent.putExtra("business", advertisementDAOList.get(0).getBusiness_name() + "\n" + advertisementDAOList.get(0).getAddress());
                intent.putExtra("stime", "" + advertisementDAOList.get(0).getS_time());
                intent.putExtra("etime", "" + advertisementDAOList.get(0).getE_time());
                intent.putExtra("sdate", advertisementDAOList.get(0).getS_date());
                intent.putExtra("edate", advertisementDAOList.get(0).getE_date());
                intent.putExtra("likecnt", advertisementDAOList.get(0).getLikecnt());
                intent.putExtra("dislikecnt", advertisementDAOList.get(0).getDislikecnt());
                intent.putExtra("mobile_no", advertisementDAOList.get(0).getBusiness_number());
                intent.putExtra("email", advertisementDAOList.get(0).getBusiness_email());
                intent.putExtra("price_status", advertisementDAOList.get(0).getPrice_status());
                intent.putExtra("describe_limitations", advertisementDAOList.get(0).getDescribe_limitations());
                if (preferences.getString("ulang", "").equals("en")) {
                    intent.putExtra("main_cat_name", advertisementDAOList.get(0).getMain_cat_name());
                    intent.putExtra("subcategory_name", advertisementDAOList.get(0).getSubcategory_name());
                    intent.putExtra("maincat", advertisementDAOList.get(0).getBus_main_cat_name());
                    intent.putExtra("subcat", advertisementDAOList.get(0).getBus_subcategory_name());
                } else if (preferences.getString("ulang", "").equals("hi")) {
                    intent.putExtra("main_cat_name", advertisementDAOList.get(0).getMain_cat_name_hindi());
                    intent.putExtra("subcategory_name", advertisementDAOList.get(0).getSubcategory_name_hindi());
                    intent.putExtra("maincat", advertisementDAOList.get(0).getBus_main_cat_name_hindi());
                    intent.putExtra("subcat", advertisementDAOList.get(0).getBus_subcategory_name_hindi());
                }
                startActivity(intent);
                finish();
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