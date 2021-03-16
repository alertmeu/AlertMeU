package in.alertmeu.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.FirebaseNotification.SharedPrefManager;
import in.alertmeu.R;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.MyBroadcastReceiver;
import in.alertmeu.utils.WebClient;

public class RegisterNGetStartActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    EditText u_mobile, uReferral;
    Button btnNext, signGuestButton, btnApply;
    String mobileNumber = "";
    CountryCodePicker ccp;
    String deviceId = "";
    TelephonyManager telephonyManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;

    boolean status;
    ProgressDialog mProgressDialog;
    JSONObject jsonObj, jsonObject, jsonUserSubCat;
    String personPhotoUrl = "";
    String first_name = "", last_name = "", email_id = "";
    String msg = "", registrationResponse = "", verifyMobileResponse = "", referral_code = "", referral_status = "0", r_country_code = "", usersubcatResponse = "";
    TextView warning;
    TextView failCode, successCode, usedCode, tnc;
    String localTime, refer_user_id = "", flag = "0";
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    String lf = "";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    //facebook
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    String account_status = "";
    private EditText editText1, editText2, editText3, editText4, editText5, editText6, editText7, editText8, editText9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_register_nget_start);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
        //Setting the permission that we need to read
        loginButton.setReadPermissions("user_photos", "email", "user_birthday");

        //Registering callback!
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Sign in completed
                Log.i(TAG, "onSuccess: logged in successfully");

                //handling the token for Firebase Auth
                handleFacebookAccessToken(loginResult.getAccessToken());

                //Getting the user information
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        Log.i(TAG, "onCompleted: response: " + response.toString());
                        try {
                            String name = object.getString("name");
                            String id = object.getString("id");
                            Log.i(TAG, "onCompleted: Email: " + id);

                            if (!name.equals("")) {
                                String columns[] = name.split(" ");
                                first_name = columns[0];
                                last_name = columns[1];

                            }
                            email_id = id;
                            prefEditor.putString("user_name", name);
                            prefEditor.putString("userEmail", id);
                            prefEditor.commit();
                            account_status = "4";
                            new userRegistration().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onCompleted: JSON exception");
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });


//       AppEventsLogger.activateApp(getApplicationContext());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        u_mobile = (EditText) findViewById(R.id.u_mobile);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnApply = (Button) findViewById(R.id.btnApply);
        uReferral = (EditText) findViewById(R.id.uReferral);
        failCode = (TextView) findViewById(R.id.failCode);
        tnc = (TextView) findViewById(R.id.tnc);
        successCode = (TextView) findViewById(R.id.successCode);
        signGuestButton = (Button) findViewById(R.id.signGuestButton);
        warning = (TextView) findViewById(R.id.warning);
        usedCode = (TextView) findViewById(R.id.usedCode);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        editText7 = (EditText) findViewById(R.id.editText7);
        editText8 = (EditText) findViewById(R.id.editText8);
        editText9 = (EditText) findViewById(R.id.editText9);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(u_mobile);
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //  String CountryCode = Locale.getDefault().getCountry();
                //  Toast.makeText(getApplicationContext(), "Updated " + CountryCode, Toast.LENGTH_SHORT).show();
                prefEditor.putString("country_code", ccp.getSelectedCountryCodeWithPlus());
                prefEditor.commit();
            }
        });
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code

                if (isValidNumber) {
                    mobileNumber = ccp.getFullNumberWithPlus();
                    //  Toast.makeText(getApplicationContext(), "Your mobile number is valid.", Toast.LENGTH_SHORT).show();
                    verifyMobileNumber();
                } else {
                    mobileNumber = "";
                    //Toast.makeText(getApplicationContext(), "Please Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText1.getText().toString().length() == 1) {
                    editText2.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText2.getText().toString().length() == 0) {
                    editText1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText2.getText().toString().length() == 1) {
                    editText3.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText3.getText().toString().length() == 0) {
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText3.getText().toString().length() == 1) {
                    editText4.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText4.getText().toString().length() == 0) {
                    editText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText4.getText().toString().length() == 1) {
                    editText5.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                // Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText5.getText().toString().length() == 0) {
                    editText4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText5.getText().toString().length() == 1) {
                    editText6.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                // Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText6.getText().toString().length() == 0) {
                    editText5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText6.getText().toString().length() == 1) {
                    editText7.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        editText7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText7.getText().toString().length() == 0) {
                    editText6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText7.getText().toString().length() == 1) {
                    editText8.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        editText8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText8.getText().toString().length() == 0) {
                    editText7.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText8.getText().toString().length() == 1) {
                    editText9.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length() == 9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        editText9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText9.getText().toString().length() == 0) {
                    editText8.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // We can call api to verify the OTP here or on an explicit button click
                if (!editText9.getText().toString().equals("")) {
                    referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                } else {
                    successCode.setVisibility(View.GONE);
                    usedCode.setVisibility(View.GONE);
                    failCode.setVisibility(View.GONE);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    if (!mobileNumber.equals("")) {
                        Intent intent = new Intent(RegisterNGetStartActivity.this, OTPForRegisterActivity.class);
                        intent.putExtra("mobile", mobileNumber);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpevmn), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterNGetStartActivity.this, TremsServiceActivity.class);
                startActivity(intent);
            }
        });
        signGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    new guestUserRegistration().execute();

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        uReferral.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referral_code = uReferral.getText().toString().trim();
                if (!referral_code.equals("")) {
                    new userReferralCode().execute();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jperc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        uReferral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(uReferral.getText())) {
                        referral_code = uReferral.getText().toString();
                        new userReferralCode().execute();

                    } else {
                        successCode.setVisibility(View.GONE);
                        usedCode.setVisibility(View.GONE);
                        failCode.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
        setGooglePlusButtonText(btnSignIn, res.getString(R.string.xsi));
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            personPhotoUrl = "" + acct.getPhotoUrl();
            String email = acct.getEmail();


            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: ");
            if (!personName.equals("")) {
                String columns[] = personName.split(" ");
                first_name = columns[0];
                last_name = columns[1];

            }
            email_id = acct.getEmail();
            prefEditor.putString("user_name", personName);
            prefEditor.putString("userEmail", email);
            prefEditor.putString("pic_name", personPhotoUrl);
            prefEditor.commit();
            account_status = "3";
            new userRegistration().execute();

           /* txtName.setText(personName);
            txtEmail.setText(email);
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);
*/
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            // showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        //
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            //   Log.i(TAG, "onStart: Someone logged in <3"+currentUser.getDisplayName()+" "+currentUser.getEmail());
            //  Log.i(TAG, "onStart: Someone logged in <3"+currentUser.getDisplayName()+" "+currentUser.getEmail());
        } else {
            Log.i(TAG, "onStart: No one logged in :/");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);

        } else {
            btnSignIn.setVisibility(View.VISIBLE);

        }
    }

    //facebook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Log.i(TAG, "onComplete: login completed with user: " + user.getDisplayName());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterNGetStartActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    //
    private class userRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Registration...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            jsonObj = new JSONObject() {
                {
                    try {

                        put("first_name", first_name);
                        put("last_name", last_name);
                        put("email_id", email_id);
                        put("mobile_device", deviceId);
                        put("fcm_id", token);
                        put("profilePath", personPhotoUrl);
                        put("referral_code", referral_code);
                        put("referral_status", referral_status);
                        put("referral_user_id", preferences.getString("referral_user_id", ""));
                        put("t_zone", localTime);
                        put("country_code", preferences.getString("country_code", ""));
                        put("r_country_code", preferences.getString("r_country_code", ""));
                        put("latitude", preferences.getString("ur_l", ""));
                        put("longitude", preferences.getString("ur_lo", ""));
                        put("account_status", account_status);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            registrationResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERBYGOOGLE, jsonObj);
            Log.i("resp", "registrationResponse" + registrationResponse);


            if (registrationResponse.compareTo("") != 0) {
                if (isJSONValid(registrationResponse)) {


                    try {

                        jsonObject = new JSONObject(registrationResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                //    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                if (!jsonObject.isNull("user_id")) {
                    try {
                        JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                        for (int i = 0; i < ujsonArray.length(); i++) {
                            JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                            lf = UJsonObject.getString("lf");
                            prefEditor.putString("user_id", UJsonObject.getString("id"));
                            prefEditor.putString("user_referral_code", UJsonObject.getString("referral_code"));
                            prefEditor.putString("npsdate", UJsonObject.getString("last_NPS_date"));
                            prefEditor.putString("user_name", UJsonObject.getString("first_name") + " " + UJsonObject.getString("last_name"));
                            prefEditor.putString("user_mobile", UJsonObject.getString("mobile_no"));
                            prefEditor.putString("userEmail", UJsonObject.getString("email_id"));
                            prefEditor.putString("first_name", UJsonObject.getString("first_name"));
                            prefEditor.putString("last_name", UJsonObject.getString("last_name"));
                            prefEditor.putString("user_email", UJsonObject.getString("user_email"));
                            prefEditor.putString("user_mobile", UJsonObject.getString("user_mobile"));
                            prefEditor.putString("pic_name", UJsonObject.getString("profilePath"));
                            prefEditor.putString("notifyonoff", "1");
                            prefEditor.putString("app_login", account_status);
                            prefEditor.putString("account_status", account_status);
                            prefEditor.putInt("units_for_area", 20);
                            prefEditor.remove("apply_u_referral_code");
                            prefEditor.remove("apply_U_referral_status");
                            prefEditor.remove("referral_user_id");
                            prefEditor.remove("r_country_code");
                            prefEditor.putString("notifyonoffads", UJsonObject.getString("statusonoff"));
                            prefEditor.putString("notifyonoffevent", UJsonObject.getString("eventsonoff"));
                            prefEditor.putString("notifyonoffinc", UJsonObject.getString("incidentonoff"));
                            prefEditor.putString("notifyonoffinfo", UJsonObject.getString("informationonoff"));
                            prefEditor.commit();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                Intent myIntent = new Intent(RegisterNGetStartActivity.this, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(RegisterNGetStartActivity.this, 0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 60); // first time
                long frequency = 10 * 1000; // in ms

                // We want the alarm to go off 30 seconds from now.
                long firstTime = SystemClock.elapsedRealtime();
                firstTime += 1 * 1000;
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 60 * 1000, pendingIntent);
                if (lf.equals("1")) {
                    insertmainsubcat();
                    Intent intent = new Intent(RegisterNGetStartActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(RegisterNGetStartActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                }
                // Close the progressdialog
                // mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                // mProgressDialog.dismiss();

            }
        }
    }

    private class guestUserRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Registration...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            jsonObj = new JSONObject() {
                {
                    try {


                        put("mobile_device", deviceId);
                        put("fcm_id", token);
                        put("t_zone", localTime);
                        put("latitude", preferences.getString("ur_l", ""));
                        put("longitude", preferences.getString("ur_lo", ""));
                        put("country_code", preferences.getString("country_code", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            registrationResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERASGUEST, jsonObj);
            Log.i("resp", "registrationResponse" + registrationResponse);


            if (registrationResponse.compareTo("") != 0) {
                if (isJSONValid(registrationResponse)) {

                    try {

                        jsonObject = new JSONObject(registrationResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                if (!jsonObject.isNull("user_id")) {
                    try {
                        JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                        for (int i = 0; i < ujsonArray.length(); i++) {
                            JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                            prefEditor.putString("user_id", UJsonObject.getString("id"));
                            prefEditor.putString("npsdate", UJsonObject.getString("last_NPS_date"));
                            prefEditor.putInt("units_for_area", 20);
                            prefEditor.putString("favloc", "0");
                            prefEditor.putString("user_name", res.getString(R.string.jguestlog));
                            prefEditor.putString("account_status", "1");
                            prefEditor.putString("notifyonoff", "1");
                            prefEditor.commit();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                Intent myIntent = new Intent(RegisterNGetStartActivity.this, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(RegisterNGetStartActivity.this, 0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 60); // first time
                long frequency = 10 * 1000; // in ms

                // We want the alarm to go off 30 seconds from now.
                long firstTime = SystemClock.elapsedRealtime();
                firstTime += 1 * 1000;
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 60 * 1000, pendingIntent);
                insertmainsubcat();
                Intent intent = new Intent(RegisterNGetStartActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();

                // Close the progressdialog
                // mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                // mProgressDialog.dismiss();

            }
        }
    }

    public void verifyMobileNumber() {


        jsonObj = new JSONObject() {
            {
                try {
                    put("mobile_no", mobileNumber);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread objectThread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                verifyMobileResponse = serviceAccess.SendHttpPost(Config.URL_GETAVAILABLEMOBILENUMBER, jsonObj);
                Log.i("loginResponse", "verifyMobileResponse" + verifyMobileResponse);
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() { // This thread runs in the UI
                            @Override
                            public void run() {
                                if (verifyMobileResponse.compareTo("") == 0) {

                                } else {

                                    try {
                                        JSONObject jObject = new JSONObject(verifyMobileResponse);
                                        status = jObject.getBoolean("status");
                                        if (status) {

                                            // Toast.makeText(getApplicationContext(), "Already Exist", Toast.LENGTH_LONG).show();
                                            warning.setVisibility(View.VISIBLE);
                                            btnNext.setVisibility(View.GONE);

                                        } else {
                                            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                            warning.setVisibility(View.GONE);
                                            btnNext.setVisibility(View.VISIBLE);

                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                };

                new Thread(runnable).start();
            }
        });
        objectThread.start();
    }

    private class userReferralCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(RegisterNGetStartActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //   mProgressDialog.setMessage("Apply Referral...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            jsonObj = new JSONObject() {
                {
                    try {

                        put("referral_code", referral_code);
                        put("referral_status", referral_code.substring(0, 1));
                        put("deviceId", deviceId);
                        put("app_type", 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            registrationResponse = serviceAccess.SendHttpPost(Config.URL_CHECKINGREFERRALCODE, jsonObj);
            Log.i("resp", "registrationResponse" + registrationResponse);


            if (registrationResponse.compareTo("") != 0) {
                if (isJSONValid(registrationResponse)) {


                    try {

                        jsonObject = new JSONObject(registrationResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        if (status) {
                            refer_user_id = jsonObject.getString("referral_user_id");
                            r_country_code = jsonObject.getString("r_country_code");
                        } else {
                            refer_user_id = "";
                            r_country_code = "";
                            flag = jsonObject.getString("referral_user_id");

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    //  Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                //  Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //  mProgressDialog.dismiss();
            if (status) {

                referral_status = referral_code.substring(0, 1);
                failCode.setVisibility(View.GONE);
                usedCode.setVisibility(View.GONE);
                successCode.setVisibility(View.VISIBLE);
                prefEditor.putString("apply_u_referral_code", referral_code);
                prefEditor.putString("apply_U_referral_status", referral_status);
                prefEditor.putString("referral_user_id", refer_user_id);
                prefEditor.putString("r_country_code", r_country_code);
                prefEditor.commit();
            } else {
                if (flag.equals("0")) {
                    successCode.setVisibility(View.GONE);
                    usedCode.setVisibility(View.GONE);
                    failCode.setVisibility(View.VISIBLE);
                } else {
                    failCode.setVisibility(View.GONE);
                    successCode.setVisibility(View.GONE);
                    usedCode.setVisibility(View.VISIBLE);

                }
                referral_status = "0";
                prefEditor.putString("apply_u_referral_code", referral_code);
                prefEditor.putString("apply_U_referral_status", referral_status);
                prefEditor.commit();
            }
        }
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

    protected boolean isJSONValid(String registrationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(registrationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(registrationResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(RegisterNGetStartActivity.this, MainActivity.class);
        startActivity(setIntent);
    }

    public void insertmainsubcat() {

        jsonUserSubCat = new JSONObject() {
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
                Log.i("json", "json" + jsonUserSubCat);
                usersubcatResponse = serviceAccess.SendHttpPost(Config.URL_GETALLUSERSUBCATFOFFLINE, jsonUserSubCat);
                Log.i("resp", "subcatResponse" + usersubcatResponse);
                try {
                    JSONObject jsonObject = new JSONObject(usersubcatResponse);
                    status = jsonObject.getBoolean("status");

                    if (status) {
                        JsonHelper jsonHelper = new JsonHelper();
                        long i = jsonHelper.parseMainSubCatOfflineList(usersubcatResponse);

                    } else {

                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        });

        objectThread.start();

    }
}
