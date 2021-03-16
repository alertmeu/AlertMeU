package in.alertmeu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;
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

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.alertmeu.R;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Constant;

public class OTPForChangePasswordActivity extends AppCompatActivity {
    EditText edtCode;
    Button btnNext,reSend;
    String id;
    TextView txtId;
    String code = "", mobile = "";
    Resources res ;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    private String verificationId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_otpfor_change_password);
        mAuth = FirebaseAuth.getInstance();
        edtCode = (EditText) findViewById(R.id.edtCode);
        btnNext = (Button) findViewById(R.id.btnNext);
        reSend= (Button) findViewById(R.id.reSend);
        txtId = (TextView) findViewById(R.id.txtId);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
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
                        Intent intent = new Intent(OTPForChangePasswordActivity.this, CreateChangePassActivity.class);
                        intent.putExtra("mobile", mobile);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();
                    }*/

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
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
            Toast.makeText(OTPForChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                            Intent intent = new Intent(OTPForChangePasswordActivity.this, CreateChangePassActivity.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }
}

