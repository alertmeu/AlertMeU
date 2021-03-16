package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class NetPromotorScoreActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private RatingBar shopratingStar, inciratingStar, msgratingStar;
    private EditText descshopEdtTxt, descinciEdtTxt, descmsgEdtTxt;
    private Button submit, notnow;
    Resources res;
    int count = 0;
    String shopstarrating = "0.0", incistarrating = "0.0", msgstarrating = "0.0";
    String shopfeedback = "", incifeedback = "", msgsfeedback = "", addResponse = "", message = "", npsdate = "";
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj;
    boolean status;
    String localTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_net_promotor_score);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        Intent intent = getIntent();
        shopratingStar = (RatingBar) findViewById(R.id.shopratingStar);
        inciratingStar = (RatingBar) findViewById(R.id.inciratingStar);
        msgratingStar = (RatingBar) findViewById(R.id.msgratingStar);
        descshopEdtTxt = (EditText) findViewById(R.id.descshopEdtTxt);
        descinciEdtTxt = (EditText) findViewById(R.id.descinciEdtTxt);
        descmsgEdtTxt = (EditText) findViewById(R.id.descmsgEdtTxt);
        submit = (Button) findViewById(R.id.submit);
        notnow = (Button) findViewById(R.id.notnow);
        //Toast.makeText(getApplicationContext(), preferences.getString("npsdate",""), Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        notnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getStringExtra("uflagscreen").equals("1")) {
                    //  Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        new updateNPSData().execute();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
        shopratingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    //  Toast.makeText(getApplicationContext(), "rating" + rating, Toast.LENGTH_SHORT).show();
                    shopstarrating = "" + rating;

                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
            //
        });
        inciratingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    // Toast.makeText(getApplicationContext(), "rating" + rating, Toast.LENGTH_SHORT).show();
                    incistarrating = "" + rating;

                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
            //
        });
        msgratingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    //Toast.makeText(getApplicationContext(), "rating" + rating, Toast.LENGTH_SHORT).show();
                    msgstarrating = "" + rating;

                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
            //
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    // Toast.makeText(getApplicationContext(), shopstarrating+"/"+incistarrating+"/"+msgstarrating, Toast.LENGTH_SHORT).show();

                    if (!shopstarrating.equals("0.0") || !incistarrating.equals("0.0") || !msgstarrating.equals("0.0")) {
                        shopfeedback = descshopEdtTxt.getText().toString();
                        incifeedback = descinciEdtTxt.getText().toString();
                        msgsfeedback = descmsgEdtTxt.getText().toString();
                        new AddNPSData().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpratesub), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class AddNPSData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(NetPromotorScoreActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(R.string.jsd);
            // Set progressdialog message
            //   mProgressDialog.setMessage("Uploading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("score_shopping", shopstarrating);
                        put("feedback_shopping", shopfeedback);
                        put("score_incidents", incistarrating);
                        put("feedback_incidents", incifeedback);
                        put("score_messaging", msgstarrating);
                        put("feedback_messaging", msgsfeedback);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            addResponse = serviceAccess.SendHttpPost(Config.URL_ADDNPSUSER, jsonLeadObj);
            Log.i("resp", "addResponse" + addResponse);
            if (addResponse.compareTo("") != 0) {
                if (isJSONValid(addResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        npsdate = jObject.getString("date");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    // Toast.makeText(NetPromotorScoreActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                // Toast.makeText(NetPromotorScoreActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                prefEditor.putString("npsdate", npsdate);
                prefEditor.commit();
                Toast.makeText(NetPromotorScoreActivity.this, res.getString(R.string.jtcurate), Toast.LENGTH_LONG).show();
                finish();
            } else {


            }

        }
    }

    private class updateNPSData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //mProgressDialog = new ProgressDialog(NetPromotorScoreActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(R.string.jsd);
            // Set progressdialog message
            //   mProgressDialog.setMessage("Uploading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            addResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEDATENPSUSER, jsonLeadObj);
            Log.i("resp", "addResponse" + addResponse);
            if (addResponse.compareTo("") != 0) {
                if (isJSONValid(addResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addResponse);
                        status = jObject.getBoolean("status");
                        npsdate = jObject.getString("date");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    // Toast.makeText(NetPromotorScoreActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                // Toast.makeText(NetPromotorScoreActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
          //  mProgressDialog.dismiss();
            if (status) {
                prefEditor.putString("npsdate", npsdate);
                prefEditor.commit();
                finish();
            } else {
            }

        }
    }

    protected boolean isJSONValid(String addImageLocationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(addImageLocationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(addImageLocationResponse);
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