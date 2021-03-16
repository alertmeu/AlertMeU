package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.R;
import in.alertmeu.adapter.IncidentListAdpter;
import in.alertmeu.adapter.SubCatListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class CheckIngLoadActivity extends AppCompatActivity {
    JSONObject jsonLeadObj1;
    String incListResponse = "";
    boolean status;
    List<SubCatModeDAO> incidentDAOList;
    SubCatListAdpter subCatListAdpter;
    RecyclerView incidentList;
    int page_no = 1, total_pages, count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_ing_load);
        incidentList = (RecyclerView) findViewById(R.id.incidentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CheckIngLoadActivity.this);
        incidentList.setLayoutManager(layoutManager);

        subCatListAdpter = new SubCatListAdpter(CheckIngLoadActivity.this, new ArrayList<>());
        incidentList.setAdapter(subCatListAdpter);

        if (AppStatus.getInstance(CheckIngLoadActivity.this).isOnline()) {
            synchronized (this) {
                new getAdvertisement().execute();
            }

        } else {

            //Toast.makeText(CheckIngLoadActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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

            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("page", page_no);
                        put("row_per_page", "20");


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            incListResponse = serviceAccess.SendHttpPost(Config.URL_GETRECODBYPAGEDEMO, jsonLeadObj1);
            Log.i("resp", "incListResponse" + incListResponse);

            try {
                if (isJSONValid(incListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(incListResponse);
                                status = jsonObject.getBoolean("status");
                                total_pages = jsonObject.getInt("total_pages");
                                if (status) {
                                    JsonHelper jsonHelper = new JsonHelper();
                                    incidentDAOList = jsonHelper.parsePageNationList(incListResponse);
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
                subCatListAdpter.addItems(incidentDAOList);
            } else {

            }
            count++;
            if (count <= total_pages) {
                page_no++;
                //  Toast.makeText(getApplicationContext(), "" + count, Toast.LENGTH_SHORT).show();
                if (AppStatus.getInstance(CheckIngLoadActivity.this).isOnline()) {
                    synchronized (this) {
                        new getAdvertisement().execute();
                    }

                }
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