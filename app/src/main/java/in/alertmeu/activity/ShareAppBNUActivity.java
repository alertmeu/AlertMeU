package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.alertmeu.R;
import in.alertmeu.adapter.ShareAppConListAdpter;
import in.alertmeu.utils.Contact;
import in.alertmeu.utils.ContactFetcher;
import in.alertmeu.utils.GMailSender;
import in.alertmeu.view.AddEEntryView;

public class ShareAppBNUActivity extends AppCompatActivity {
    ArrayList<Contact> listContacts;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";
    RecyclerView studentsList;
    ShareAppConListAdpter shareAppConListAdpter;
    CheckBox chkAll;
    LinearLayout btnNext;
    ImageView back_arrow1;
    ArrayList<String> contactArrayList;
    ArrayList<String> emailArrayList;
    //  private static final String username = "outreach@alertmeu.com";
    private static final String username = "email-verification@alertmeu.com";
    private static String password = "ZSAM@2020";
    GMailSender sender;
    String email = "", sub = "AlertMeU", body = "";
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_share_app_b_n_u);
        back_arrow1 = (ImageView) findViewById(R.id.back_arrow1);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
        Intent intent = getIntent();
        body = intent.getStringExtra("shareappm");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sender = new GMailSender(username, password);
        if (checkAndRequestPermissions()) {
            listContacts = new ContactFetcher(this).fetchAll();
            studentsList = (RecyclerView) findViewById(R.id.contactList);
            shareAppConListAdpter = new ShareAppConListAdpter(ShareAppBNUActivity.this, listContacts);
            studentsList.setAdapter(shareAppConListAdpter);
            studentsList.setLayoutManager(new LinearLayoutManager(ShareAppBNUActivity.this));
            shareAppConListAdpter.notifyDataSetChanged();  // data set changed
        }

        back_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chkAll = (CheckBox) findViewById(R.id.chkAllSelected);
        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    List<Contact> list = ((ShareAppConListAdpter) shareAppConListAdpter).getSservicelist();
                    for (Contact workout : list) {
                        workout.setSelected(true);
                        workout.setChecked_status("1");
                    }

                    ((ShareAppConListAdpter) studentsList.getAdapter()).notifyDataSetChanged();
                } else {
                    List<Contact> list = ((ShareAppConListAdpter) shareAppConListAdpter).getSservicelist();
                    for (Contact workout : list) {
                        workout.setSelected(false);
                        workout.setChecked_status("0");
                    }

                    ((ShareAppConListAdpter) studentsList.getAdapter()).notifyDataSetChanged();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Contact> stList = ((ShareAppConListAdpter) shareAppConListAdpter).getSservicelist();
                contactArrayList = new ArrayList<>();
                emailArrayList = new ArrayList<>();
                for (int i = 0; i < stList.size(); i++) {
                    Contact serviceListDAO = stList.get(i);
                    if (serviceListDAO.isSelected() == true) {
                        if (serviceListDAO.numbers.size() > 0 && serviceListDAO.numbers.get(0) != null) {
                            //contactArrayList.add(serviceListDAO.numbers.get(0).number);
                            sendSMS(serviceListDAO.numbers.get(0).number, body);
                            //  sendSMS("+91 9657816221", body);
                        }
                        if (serviceListDAO.emails.size() > 0 && serviceListDAO.emails.get(0) != null) {
                            // emailArrayList.add(serviceListDAO.emails.get(0).address);
                            email = serviceListDAO.emails.get(0).address;
                            // email = "ashokkumawat708@gmail.com";
                            new MyAsyncClass().execute();
                        }

                    } else {
                        System.out.println("not selected");

                    }
                }
                for (Contact workout : stList) {
                    workout.setSelected(false);
                    workout.setChecked_status("0");
                }
                chkAll.setChecked(false);
                ((ShareAppConListAdpter) studentsList.getAdapter()).notifyDataSetChanged();
                // Toast.makeText(ShareAppBNUActivity.this, "" + contactArrayList+""+emailArrayList, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean checkAndRequestPermissions() {


        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionSendSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();


        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (permissionSendSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions

                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        listContacts = new ContactFetcher(this).fetchAll();
                        studentsList = (RecyclerView) findViewById(R.id.contactList);
                        shareAppConListAdpter = new ShareAppConListAdpter(ShareAppBNUActivity.this, listContacts);
                        studentsList.setAdapter(shareAppConListAdpter);
                        studentsList.setLayoutManager(new LinearLayoutManager(ShareAppBNUActivity.this));
                        shareAppConListAdpter.notifyDataSetChanged();  // data set changed
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:in.alertmeu")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            // smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ShareAppBNUActivity.this);
            pDialog.setMessage(res.getString(R.string.jpw));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Toast.makeText(getActivity(), "mail id is"+preferences.getString("sendingmailid", ""), Toast.LENGTH_SHORT).show();
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail(sub, body, username, email);

            } catch (Exception ex) {
                Log.d("Error", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(ShareAppBNUActivity.this, res.getString(R.string.jes), Toast.LENGTH_LONG).show();

        }


    }
}