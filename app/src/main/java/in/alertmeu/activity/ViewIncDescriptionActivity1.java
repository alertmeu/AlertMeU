package in.alertmeu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.alertmeu.R;
import in.alertmeu.adapter.IncidentListAdpter;
import in.alertmeu.adapter.IncidentMoreInfoListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.models.MoreIncInfoModel;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.AppUtils;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;

public class ViewIncDescriptionActivity1 extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private JSONObject jsonLeadObj, jsonSchedule, jsonLeadObj1, jsonLeadObjOthBus;
    ProgressDialog mProgressDialog;
    boolean status, statusob;
    String msg = "", googlePlacesData = "";
    String updateStatusResponse = "", imagePathResponse = "", advertisementListResponse = "";

    String userreview = "", imgDes = "", mobile_no = "", imagePath = "", starrating = "", userStar = "", add = "", id = "", qrCode = "", describe_limitations = "", description = "", emailid = "", title = "", avgrating = "";
    ImageView image, calling, navigation, share, takebarCode, email, emailh, callingh, pricelist, confirm, refute, refuted, confirmed, addinfo;
    TextView dis, address, editReview, writeReview, torcountr, validity, confirmtl, refutetl;
    //bar code
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";

    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    String torcount = "", inappropriate = "", confirm_status = "", confRefuteResponse = "", confRefuteResponse1 = "", refutetext = "";
    RatingBar ratingStar, avgratingStar;
    //map

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Context mContext;
    TextView mLocationMarkerText, titleTxt, subCat, mainCat;
    Double lat, lng;
    LinearLayout limithideshow, deshideshow, hideothbusiness;
    // private LatLng mCenterLatLong;
    String localTime;
    Intent intent;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    int foa = 0;
    RecyclerView recyclerView, othBuinessList;
    int days = 0;
    List<IncidentDAO> advertisementDAOList;
    IncidentListAdpter incidentListAdpter;
    List<MoreIncInfoModel> data = null;
    IncidentMoreInfoListAdpter incidentMoreInfoListAdpter;

    private RadioButton radioRefuteButton1, radioRefuteButton2, radioRefuteButton3, radioRefuteButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        intent = getIntent();
        setContentView(R.layout.activity_view_inc_description1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mContext = this;
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        image = (ImageView) findViewById(R.id.image);
        calling = (ImageView) findViewById(R.id.calling);
        callingh = (ImageView) findViewById(R.id.callingh);
        email = (ImageView) findViewById(R.id.email);
        emailh = (ImageView) findViewById(R.id.emailh);
        navigation = (ImageView) findViewById(R.id.navigation);
        share = (ImageView) findViewById(R.id.share);
        takebarCode = (ImageView) findViewById(R.id.takebarCode);
        pricelist = (ImageView) findViewById(R.id.pricelist);
        confirm = (ImageView) findViewById(R.id.confirm);
        refute = (ImageView) findViewById(R.id.refute);
        confirmed = (ImageView) findViewById(R.id.confirmed);
        refuted = (ImageView) findViewById(R.id.refuted);
        addinfo = (ImageView) findViewById(R.id.addinfo);
        dis = (TextView) findViewById(R.id.dis);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        address = (TextView) findViewById(R.id.address);
        torcountr = (TextView) findViewById(R.id.torcountr);
        validity = (TextView) findViewById(R.id.validity);
        ratingStar = (RatingBar) findViewById(R.id.ratingStar);
        avgratingStar = (RatingBar) findViewById(R.id.avgratingStar);
        hideothbusiness = (LinearLayout) findViewById(R.id.hideothbusiness);
        mainCat = (TextView) findViewById(R.id.mainCat);
        subCat = (TextView) findViewById(R.id.subCat);
        image.setVisibility(View.VISIBLE);
        id = intent.getStringExtra("id");
        mobile_no = intent.getStringExtra("mobile_no");
        dis.setText(mobile_no);
        emailid = intent.getStringExtra("email");
        mainCat.setText(res.getString(R.string.jadcat) + intent.getStringExtra("maincat"));
        validity.setText(formateDateFromstring("yyyy-MM-dd HH:mm", "dd-MMM-yyyy hh:mm aa", intent.getStringExtra("maincat")));
        subCat.setText(res.getString(R.string.jadscat) + intent.getStringExtra("subcat"));
        titleTxt.setText(intent.getStringExtra("title"));
        title = intent.getStringExtra("title");
        imgDes = intent.getStringExtra("title");
        address.setText(intent.getStringExtra("business"));
        qrCode = intent.getStringExtra("referral_code");
        lat = Double.parseDouble(intent.getStringExtra("lat"));
        lng = Double.parseDouble(intent.getStringExtra("long"));
        imagePath = intent.getStringExtra("imagePath");
        description = intent.getStringExtra("sponsor_flag");
        writeReview = (TextView) findViewById(R.id.writeReview);
        editReview = (TextView) findViewById(R.id.editReview);
        confirmtl = (TextView) findViewById(R.id.confirmtl);
        refutetl = (TextView) findViewById(R.id.refutetl);
        recyclerView = (RecyclerView) findViewById(R.id.advertisementList);
        othBuinessList = (RecyclerView) findViewById(R.id.othBuinessList);
        advertisementDAOList = new ArrayList<>();
        data = new ArrayList<>();
        if (mobile_no.trim().equals("")) {
            calling.setVisibility(View.GONE);
            callingh.setVisibility(View.VISIBLE);
        } else {
            callingh.setVisibility(View.GONE);
            calling.setVisibility(View.VISIBLE);

        }
        if (emailid.trim().equals("")) {
            email.setVisibility(View.GONE);
            emailh.setVisibility(View.VISIBLE);
        } else {
            emailh.setVisibility(View.GONE);
            email.setVisibility(View.VISIBLE);

        }


        try {
            Picasso.get().load(imagePath).into(image);
        } catch (Exception e) {

        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }


        calling.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkAndRequestPermissions()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mobile_no));
                    if (ActivityCompat.checkSelfPermission(ViewIncDescriptionActivity1.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailid));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AlertMeU");
                // emailIntent.putExtra(Intent.EXTRA_TEXT, body);
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

                startActivity(Intent.createChooser(emailIntent, "AlertMeU"));
            }
        });
        navigation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + add + ")"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
                    String mapsPackageName = "com.google.android.apps.maps";

                    i.setClassName(mapsPackageName, "com.google.android.maps.MapsActivity");
                    i.setPackage(mapsPackageName);

                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }


            }
        });
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkAndRequestPermissions()) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        Uri bmpUri = getLocalBitmapUri(image);
                        if (bmpUri != null) {
                            // Construct a ShareIntent with link to image
                            Intent shareIntent = new Intent();
                            shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            // shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on AlertMeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/store/apps/details?id=in.alertmeu).\n\n" + imgDes + " \n" + description);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, imgDes + " \n" + mobile_no);

                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/jpg");
                            startActivity(Intent.createChooser(shareIntent, "Share with"));

                        } else {
                            // ...sharing failed, handle error
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                    builder.setMessage(res.getString(R.string.jprqr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(ViewIncDescriptionActivity1.this, RegisterNGetStartActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(res.getString(R.string.helpCan), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();

                } else {

                    Intent intent1 = new Intent(ViewIncDescriptionActivity1.this, FullScreenViewActivity.class);
                    intent1.putExtra("path", imagePath);
                    intent1.putExtra("flag", "1");
                    intent1.putExtra("title", intent.getStringExtra("title"));
                    intent1.putExtra("description", description);
                    intent1.putExtra("describe_limitations", describe_limitations);
                    intent1.putExtra("sdate", intent.getStringExtra("sdate"));
                    intent1.putExtra("stime", intent.getStringExtra("stime"));
                    intent1.putExtra("edate", intent.getStringExtra("edate"));
                    intent1.putExtra("etime", intent.getStringExtra("etime"));
                    intent1.putExtra("main_cat_name", intent.getStringExtra("main_cat_name"));
                    intent1.putExtra("subcategory_name", intent.getStringExtra("subcategory_name"));
                    startActivity(intent1);
                }

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewIncDescriptionActivity1.this, RegisterNGetStartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton(res.getString(R.string.helpCan)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  Action for 'NO' Button
                                            dialog.cancel();
                                        }
                                    });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();
                } else {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        confirm_status = "1";
                        if (confirm_status.equals("1")) {
                       /* liked.setVisibility(View.GONE);
                        dislike.setVisibility(View.GONE);
                        like.setVisibility(View.VISIBLE);
                        disliked.setVisibility(View.VISIBLE);*/
                            refuted.setVisibility(View.GONE);
                            confirm.setVisibility(View.GONE);
                            confirmed.setVisibility(View.VISIBLE);
                            refute.setVisibility(View.VISIBLE);

                        }
                        new initStatusUpdate().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        refute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewIncDescriptionActivity1.this, RegisterNGetStartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton(res.getString(R.string.helpCan)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  Action for 'NO' Button
                                            dialog.cancel();
                                        }
                                    });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();
                } else {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.write_refutecustom_layout, null);
                        alertDialogBuilder.setView(view);
                        alertDialogBuilder.setCancelable(true);
                        final AlertDialog dialog = alertDialogBuilder.create();
                        dialog.show();
                        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
                        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                        EditText descEdtTxt = (EditText) dialog.findViewById(R.id.descEdtTxt);
                        ImageView back_arrow1 = dialog.findViewById(R.id.back_arrow1);

                        radioRefuteButton1 = (RadioButton) dialog.findViewById(R.id.refBtn1);
                        radioRefuteButton2 = (RadioButton) dialog.findViewById(R.id.refBtn2);
                        radioRefuteButton3 = (RadioButton) dialog.findViewById(R.id.refBtn3);
                        radioRefuteButton4 = (RadioButton) dialog.findViewById(R.id.refBtn4);
                        radioRefuteButton1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getApplicationContext(),radioRefuteButton1.getText(),Toast.LENGTH_SHORT).show();
                                refutetext = "" + radioRefuteButton1.getText();
                            }
                        });
                        radioRefuteButton2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(getApplicationContext(),radioRefuteButton2.getText(),Toast.LENGTH_SHORT).show();
                                refutetext = "" + radioRefuteButton2.getText();
                            }
                        });
                        radioRefuteButton3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(getApplicationContext(),radioRefuteButton3.getText(),Toast.LENGTH_SHORT).show();
                                refutetext = "" + radioRefuteButton3.getText();
                            }
                        });
                        radioRefuteButton4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getApplicationContext(),radioRefuteButton4.getText(),Toast.LENGTH_SHORT).show();
                                refutetext = "" + radioRefuteButton4.getText();
                            }
                        });
                        back_arrow1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }

                        });
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                            }
                        });

                        btnSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getApplicationContext(), refutetext, Toast.LENGTH_SHORT).show();
                                if (!refutetext.equals("")) {
                                    dialog.dismiss();
                                    userreview = descEdtTxt.getText().toString();
                                    confirm_status = "0";
                                    if (confirm_status.equals("0")) {
                                        confirmed.setVisibility(View.GONE);
                                        refute.setVisibility(View.GONE);
                                        confirm.setVisibility(View.VISIBLE);
                                        refuted.setVisibility(View.VISIBLE);

                                    }
                                    new initStatusUpdate().execute();
                                    //  Toast.makeText(getApplicationContext(), descEdtTxt.getText().toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), res.getString(R.string.xrefopt), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        dialog.show();


                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        addinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewIncDescriptionActivity1.this, RegisterNGetStartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton(res.getString(R.string.helpCan)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  Action for 'NO' Button
                                            dialog.cancel();
                                        }
                                    });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();
                } else {
                    Intent intent = new Intent(ViewIncDescriptionActivity1.this, IncidentMoreInfoReportActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

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
        if (AppStatus.getInstance(ViewIncDescriptionActivity1.this).isOnline()) {
            synchronized (this) {
                getImagePath();
                new getAdvertisement().execute();
                new getMoreOtherInc().execute();
            }

        } else {

            Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }

        IncidentMoreInfoReportActivity.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(ViewIncDescriptionActivity1.this).isOnline()) {
                    synchronized (this) {
                        new getMoreOtherInc().execute();
                    }

                } else {

                    Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            @SuppressLint("RestrictedApi")
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            //LatLng latLong;

            MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng));
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(10f).tilt(70).build();
            mMap.addMarker(marker);
            // latLong = new LatLng(Double.parseDouble(preferences.getString("lat", "")), Double.parseDouble(preferences.getString("long", "")));
            //  CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(10f).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // mLocationMarkerText.setText("Lat : " + String.format("%.06f", location.getLatitude()) + "," + "Long : " + String.format("%.06f", location.getLongitude()));
            // startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
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


    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class initRatingUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ViewIncDescriptionActivity1.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.juds));
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
                        put("business_id", id);
                        put("rating_star", starrating);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERRATINGSTAR, jsonLeadObj);
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
                            Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
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
                //  Toast.makeText(ViewIncDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                getImagePath();
            } else {
                // Toast.makeText(ViewIncDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();

            }


        }
    }

    private class initReviewUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(ViewIncDescriptionActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.juds));
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
                        put("business_id", id);
                        put("user_review", userreview);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERREVIEW, jsonLeadObj);
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
                            Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // mProgressDialog.dismiss();
            if (status) {
                // Toast.makeText(ViewIncDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                getImagePath();
            } else {
                // Toast.makeText(ViewIncDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();

            }


        }
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    //
    private boolean checkAndRequestPermissions() {


        int permissionReadPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permissionReadPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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


                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                            Uri bmpUri = getLocalBitmapUri(image);
                            if (bmpUri != null) {
                                // Construct a ShareIntent with link to image
                                Intent shareIntent = new Intent();
                                shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                // shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on AlertMeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/store/apps/details?id=in.alertmeu).\n\n" + imgDes + " \n" + description);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, imgDes + " \n" + mobile_no);

                                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                shareIntent.setType("image/jpg");
                                startActivity(Intent.createChooser(shareIntent, "Share with"));

                            } else {
                                // ...sharing failed, handle error
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:in.alertmeu")));
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        if (preferences.getString("incback", "").equals("1")) {
            Intent setIntent = new Intent(ViewIncDescriptionActivity1.this, HomePageActivity.class);
            startActivity(setIntent);
            finish();
        } else {
            finish();
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

    public static String parseTime(String time, String inFormat, String outFormat) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat(outFormat).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("bid", id);
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
                Log.i("json", "json" + jsonSchedule);

                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_GETRATINGREVIEWBUSINESS, jsonSchedule);
                confRefuteResponse = serviceAccess.SendHttpPost(Config.URL_GETCONFIRMREFUTESTATUS, jsonSchedule);
                confRefuteResponse1 = serviceAccess.SendHttpPost(Config.URL_GETCONFIRMREFUTESTATUSCNT, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            msg = jsonObject.getString("message");
                            if (status) {
                                if (!jsonObject.isNull("dataList")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        userStar = object.getString("rating_star");
                                        if (!userStar.equals("null") && !userStar.equals("")) {
                                            ratingStar.setRating(Float.parseFloat(userStar));
                                        }
                                        userreview = object.getString("user_review");
                                        avgrating = object.getString("avgrating");
                                        torcount = object.getString("torcount");
                                        avgratingStar.setVisibility(View.VISIBLE);
                                        avgratingStar.setRating(Float.parseFloat(avgrating));
                                        torcountr.setVisibility(View.VISIBLE);
                                        torcountr.setText(torcount);
                                        if (!userreview.equals("")) {
                                            writeReview.setVisibility(View.GONE);
                                            editReview.setVisibility(View.VISIBLE);
                                        } else {
                                            editReview.setVisibility(View.GONE);
                                            writeReview.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    foa = 1;
                                }
                            } else {
                                foa = 1;
                            }
                            JSONObject jsonObject1 = new JSONObject(confRefuteResponse);

                            boolean status1 = jsonObject1.getBoolean("status");
                            if (status1) {
                                String likestatus = jsonObject1.getString("likestatus");
                                if (likestatus.equals("0")) {
                                    confirmed.setVisibility(View.GONE);
                                    refute.setVisibility(View.GONE);
                                    confirm.setVisibility(View.VISIBLE);
                                    refuted.setVisibility(View.VISIBLE);
                                } else if (likestatus.equals("1")) {
                                    refuted.setVisibility(View.GONE);
                                    confirm.setVisibility(View.GONE);
                                    confirmed.setVisibility(View.VISIBLE);
                                    refute.setVisibility(View.VISIBLE);
                                }
                            }

                            JSONObject jsonObject2 = new JSONObject(confRefuteResponse1);
                            boolean status2 = jsonObject2.getBoolean("status");
                            if (status2) {
                                String likestatus2 = jsonObject2.getString("likestatus");
                                String a[] = likestatus2.split("#");
                                if (Integer.parseInt(a[0]) > 0) {
                                    confirmtl.setText(res.getString(R.string.xocnby) + " " + a[0]);
                                } else {
                                    confirmtl.setText("");
                                }
                                if (Integer.parseInt(a[1]) > 0) {
                                    refutetl.setText(res.getString(R.string.xorefby) + " " + a[1]);
                                } else {
                                    refutetl.setText("");
                                }

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });


            }
        });

        objectThread.start();

    }

    private class getMoreOtherInc extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {


            jsonLeadObjOthBus = new JSONObject() {
                {
                    try {
                        put("incid", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObjOthBus);
            googlePlacesData = serviceAccess.SendHttpPost(Config.URL_GETALLOTHERMOREINCADSBYID, jsonLeadObjOthBus);
            Log.i("resp", "advertisementListResponse" + googlePlacesData);
            data.clear();
            try {

                if (isJSONValid(googlePlacesData)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(googlePlacesData);
                                statusob = jsonObject.getBoolean("status");
                                if (statusob) {
                                    JsonHelper jsonHelper = new JsonHelper();
                                    data = jsonHelper.parseAllOtherMoreIncList(googlePlacesData);
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
            if (data.size() > 0) {
                hideothbusiness.setVisibility(View.VISIBLE);
                incidentMoreInfoListAdpter = new IncidentMoreInfoListAdpter(ViewIncDescriptionActivity1.this, data);
                othBuinessList.setAdapter(incidentMoreInfoListAdpter);
                othBuinessList.setLayoutManager(new LinearLayoutManager(ViewIncDescriptionActivity1.this));
            } else {
                hideothbusiness.setVisibility(View.GONE);
            }
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
            if (days == 0) {
                days = 1;
            }
            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("latitude", preferences.getString("favlat", ""));
                        put("longitude", preferences.getString("favlong", ""));
                        put("title", "");
                        put("distance", "" + preferences.getInt("units_for_area", 0));
                        put("currentdays", days);
                        put("user_id", preferences.getString("user_id", ""));
                        put("t_zone", localTime);
                        put("incid", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLOTHERINCADSBYID, jsonLeadObj1);
            //googlePlacesData = serviceAccess.SendHttpPost(Config.URL_GETALLCURRENTACTOTHERBUSINESSID, jsonLeadObjOthBus);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);

            try {
                if (isJSONValid(advertisementListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(advertisementListResponse);
                                status = jsonObject.getBoolean("status");
                                if (status) {
                                    JsonHelper jsonHelper = new JsonHelper();
                                    advertisementDAOList = jsonHelper.parseAllIncList(advertisementListResponse);
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
            if (advertisementDAOList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                incidentListAdpter = new IncidentListAdpter(ViewIncDescriptionActivity1.this, advertisementDAOList);
                recyclerView.setAdapter(incidentListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewIncDescriptionActivity1.this));
            } else {
                recyclerView.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.inappropriate:
                //  Toast.makeText(getApplicationContext(), "inappropriate", Toast.LENGTH_LONG).show();
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewIncDescriptionActivity1.this, RegisterNGetStartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton(res.getString(R.string.helpCan)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  Action for 'NO' Button
                                            dialog.cancel();
                                        }
                                    });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();
                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewIncDescriptionActivity1.this);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.write_inappropinc_layout, null);
                    alertDialogBuilder.setView(view);
                    alertDialogBuilder.setCancelable(true);
                    final AlertDialog dialog = alertDialogBuilder.create();
                    dialog.show();
                    Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
                    Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                    EditText descEdtTxt = (EditText) dialog.findViewById(R.id.descEdtTxt);
                    ImageView back_arrow1 = dialog.findViewById(R.id.back_arrow1);
                    back_arrow1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }

                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            inappropriate = descEdtTxt.getText().toString();
                            new initInappropriateUpdate().execute();
                            //  Toast.makeText(getApplicationContext(), descEdtTxt.getText().toString(), Toast.LENGTH_SHORT).show();

                        }
                    });


                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class initInappropriateUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(ViewIncDescriptionActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.juds));
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
                        put("incident_id", id);
                        put("user_inappropriate", inappropriate);
                        put("t_zone", localTime);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERCONFRMREPUTEINAPRO, jsonLeadObj);
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
                            Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // mProgressDialog.dismiss();
            if (status) {
                Toast.makeText(ViewIncDescriptionActivity1.this, res.getString(R.string.jdus), Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(ViewIncDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();

            }


        }
    }

    private class initStatusUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(ViewImageDescriptionActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Updating Status...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("incident_id", id);
                        put("confirm_refute_status", confirm_status);
                        put("refute_reason", refutetext);
                        put("refute_description", userreview);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERCONFRMREPUTE, jsonLeadObj);
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
                            Toast.makeText(ViewIncDescriptionActivity1.this, "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewIncDescriptionActivity1.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                refutetext = "";
                userreview = "";
                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                getImagePath();
                //  mProgressDialog.dismiss();


            } else {

                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                //    mProgressDialog.dismiss();
                ;
            }
            //    mProgressDialog.dismiss();

        }

    }

}
