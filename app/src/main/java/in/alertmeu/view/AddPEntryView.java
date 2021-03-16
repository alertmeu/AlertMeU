package in.alertmeu.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.alertmeu.R;
import in.alertmeu.activity.CreatePassActivity;
import in.alertmeu.activity.OTPForRegisterActivity;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;


public class AddPEntryView extends DialogFragment {
    Context context;
    SharedPreferences preferences;
    Editor prefEditor;
    String addBusinessAcountResponse = "", businessMobile = "", message = "";
    JSONObject jsonObj;
    Boolean status;
    int count = 0;
    View registerView;
    private JSONObject jsonLeadObj;
    ProgressDialog mProgressDialog;
    JSONArray jsonArray;
    EditText edtMobileOb, otp;
    ImageView back_arrow1;
    Button placeBtn, verify,reSend;
    String email = "";
    private static Listener mListener;
    String id;
    CountryCodePicker ccp;
    LinearLayout hshow,hshow1;
    TextView txtId;
    Resources res;
    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        registerView = inflater.inflate(R.layout.dialog_add_phone, null);

        context = getActivity();
        Window window = getDialog().getWindow();

        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.CENTER | Gravity.CENTER);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();

        params.y = 50;
        window.setAttributes(params);
        res = getResources();
        mAuth = FirebaseAuth.getInstance();
        preferences = getActivity().getSharedPreferences("Prefrence", getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        edtMobileOb = (EditText) registerView.findViewById(R.id.edtMobileOb);
        otp = (EditText) registerView.findViewById(R.id.otp);
        placeBtn = (Button) registerView.findViewById(R.id.submit);
        verify = (Button) registerView.findViewById(R.id.verify);
        hshow = (LinearLayout) registerView.findViewById(R.id.hshow);
        hshow1= (LinearLayout) registerView.findViewById(R.id.hshow1);
        txtId = (TextView) registerView.findViewById(R.id.txtId);
        reSend=(Button) registerView.findViewById(R.id.reSend);
        ccp = (CountryCodePicker) registerView.findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(edtMobileOb);
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());
        prefEditor.putString("country_code", ccp.getSelectedCountryCodeWithPlus());
        prefEditor.commit();
        back_arrow1 = (ImageView) registerView.findViewById(R.id.back_arrow1);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        back_arrow1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    sendVerificationCode(businessMobile);

                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        placeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                email = edtMobileOb.getText().toString().trim();
                String s = otp.getText().toString().trim();

                if (!businessMobile.equals("")) {
                    verifyCode(s);
                 /*   if (id.equals(s)) {
                        if (AppStatus.getInstance(context).isOnline()) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(placeBtn.getWindowToken(), 0);
                            new addBusinessAccountDetails().execute();

                        } else {

                            Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    Toast.makeText(getActivity(), res.getString(R.string.jevmn), Toast.LENGTH_SHORT).show();

                }

            }
        });
        verify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                otp.setVisibility(View.VISIBLE);
                hshow.setVisibility(View.VISIBLE);
                hshow1.setVisibility(View.VISIBLE);
                txtId.setVisibility(View.VISIBLE);
                if (validate(businessMobile)) {
                    businessMobile = ccp.getFullNumberWithPlus();
                    Random random = new Random();
                    id = String.format("%06d", random.nextInt(1000000));
                    verify.setVisibility(View.GONE);
                    placeBtn.setVisibility(View.VISIBLE);
                    otp.setVisibility(View.VISIBLE);
                    hshow.setVisibility(View.VISIBLE);
                    hshow1.setVisibility(View.VISIBLE);
                    //   txtId.setText(id);
                    txtId.setText(res.getString(R.string.jsentto) + " " + businessMobile);
                    sendVerificationCode(businessMobile);
                }
            }
        });
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code

                if (isValidNumber) {
                    businessMobile = ccp.getFullNumberWithPlus();
                    placeBtn.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    //  Toast.makeText(getApplicationContext(), "Your mobile number is valid.", Toast.LENGTH_SHORT).show();
                    // verifyMobileNumber();
                } else {
                    businessMobile = "";
                    otp.setVisibility(View.GONE);
                    hshow.setVisibility(View.GONE);
                    hshow1.setVisibility(View.GONE);
                    txtId.setVisibility(View.GONE);

                    //Toast.makeText(getApplicationContext(), "Please Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getDialog().setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //Hide your keyboard here!!!
                    //Toast.makeText(getActivity(), "PLease enter your information to get us connected with you.", Toast.LENGTH_LONG).show();
                    return true; // pretend we've processed it
                } else
                    return false; // pass on to be processed as normal
            }
        });
        return registerView;
    }

    private boolean validate(String nob) {
        boolean isValidate = false;
        if (nob.equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jevmn), Toast.LENGTH_LONG).show();
        } else {
            isValidate = true;
        }
        return isValidate;
    }


    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        //Toast.makeText(getActivity(), "Your information is valuable for us and won't be misused.", Toast.LENGTH_SHORT).show();
                        count++;
                        if (count >= 1) {

                            dismiss();
                        }
                        return true;
                    } else {
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }

    //
    private class addBusinessAccountDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jud));
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
                        put("user_mobile", businessMobile);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            addBusinessAcountResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEUSERP, jsonLeadObj);
            Log.i("resp", "addBusinessAcountResponse" + addBusinessAcountResponse);
            if (addBusinessAcountResponse.compareTo("") != 0) {
                if (isJSONValid(addBusinessAcountResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addBusinessAcountResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(addBusinessAcountResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    // Toast.makeText(AccountSetupLocationActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                // Toast.makeText(AccountSetupLocationActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                dismiss();
                mListener.messageReceived(businessMobile);
            }

        }
    }

    public static void bindListener(Listener listener) {
        mListener = listener;
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
                otp.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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

                            if (AppStatus.getInstance(context).isOnline()) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(placeBtn.getWindowToken(), 0);
                                new addBusinessAccountDetails().execute();

                            } else {

                                Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}