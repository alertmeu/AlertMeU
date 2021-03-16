package in.alertmeu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.WebClient;

public class OTPforChangeNumberActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    EditText edtCode;
    Button btnNext, reSend;
    String id;
    TextView txtId;
    String code = "", mobile = "";
    private JSONObject jsonLeadObj, jsonSchedule;
    ProgressDialog mProgressDialog;
    boolean status;
    String msg = "";
    String updateStatusResponse = "", imagePathResponse = "";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_otpfor_change_number);
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        edtCode = (EditText) findViewById(R.id.edtCode);
        btnNext = (Button) findViewById(R.id.btnNext);
        reSend = (Button) findViewById(R.id.reSend);
        txtId = (TextView) findViewById(R.id.txtId);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("new_number");
        // Random random = new Random();
        // id = String.format("%06d", random.nextInt(1000000));
        //  txtId.setText(id);
        txtId.setText(res.getString(R.string.jsentto) + " " + mobile);
        sendVerificationCode(mobile);
        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    sendVerificationCode(mobile);

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    code = edtCode.getText().toString().trim();
                    verifyCode(code);
                   /* if (code.equals(id)) {
                        new updateMobileNo().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();
                    }*/

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class updateMobileNo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(OTPforChangeNumberActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.judn));
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
                        put("mobile_no", mobile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEMOBILENO, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(updateStatusResponse);
                                msg = jsonObject.getString("message");
                                status = jsonObject.getBoolean("status");

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
                            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (status) {
                prefEditor.putString("userMobile", mobile);
                prefEditor.commit();
                Intent intent = new Intent(OTPforChangeNumberActivity.this, UserProfileSettingActivity.class);
                startActivity(intent);
                finish();


            } else {

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();


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

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(OTPforChangeNumberActivity.this, UserProfileSettingActivity.class);
        startActivity(setIntent);
        finish();
    }

    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                edtCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTPforChangeNumberActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            new updateMobileNo().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
