package in.alertmeu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Random;
import java.util.TimeZone;

import butterknife.BindView;
import in.alertmeu.R;

import in.alertmeu.adapter.ActiveAdvertisementListAdpter;
import in.alertmeu.adapter.AllActiveAdsMidListAdpterPage;
import in.alertmeu.adapter.OtherBusinessListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.AppUtils;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;

import in.alertmeu.utils.PaginationListener;
import in.alertmeu.utils.Utility;
import in.alertmeu.utils.WebClient;

import static in.alertmeu.utils.PaginationListener.PAGE_START;


public class ViewImageDescriptionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private JSONObject jsonLeadObj, jsonSchedule, jsonSchedule1, jsonLeadObj1;
    ProgressDialog mProgressDialog;
    boolean status;
    String msg = "";
    String updateStatusResponse = "", imagePathResponse = "";

    String path = "", imgDes = "", like_status = "", image_flag = "0", dis_like_status = "", mobile_no = "", imagePath = "", starrating = "", userStar = "", add = "", id = "", qrCode = "", describe_limitations = "", description = "", emailid = "", title = "", txtaddress = "", company_logo = "", bid = "", bname = "", busmaincat = "", bussubcat = "", mcatid = "", price_status = "";
    ImageView image, like, calling, navigation, share, dislike, liked, disliked, takebarCode, email, emailh, callingh, readmore;
    TextView dis, totalLikes, address, clickTxt, validity, subCat, mainCat;
    //bar code
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";

    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    String fname = "", userreview = "", advertisementListResponse = "";
    RatingBar ratingStar;
    //map

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Context mContext;
    TextView mLocationMarkerText, titleTxt, limitation;
    Double lat, lng;
    LinearLayout limithideshow, deshideshow;
    // private LatLng mCenterLatLong;
    String localTime;
    Intent intent;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    int days = 0;
    List<AdvertisementAllBusDAO> advertisementDAOList;
    ActiveAdvertisementListAdpter advertisementListAdpter;
    RecyclerView recyclerView;
    static final Integer CALL = 0x1;
    static final Integer WRITE_EXST = 0x2;
    static final Integer WRITE_EXSTQ = 0x3;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    @BindView(R.id.othBuinessList)
    RecyclerView mList;
    AllActiveAdsMidListAdpterPage allActiveAdsMidListAdpterPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        intent = getIntent();
        mobile_no = intent.getStringExtra("mobile_no");
        emailid = intent.getStringExtra("email");
        if (!mobile_no.equals("") && emailid.equals("") || emailid.equals(" ")) {
            setContentView(R.layout.activity_view_image_description);
        } else if (!emailid.equals("") && mobile_no.equals("") || mobile_no.equals(" ")) {
            setContentView(R.layout.activity_view_image_descriptione);
        } else if (!emailid.equals("") && !mobile_no.equals("")) {
            setContentView(R.layout.activity_view_image_descriptionep);
        }
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
        like = (ImageView) findViewById(R.id.like);
        calling = (ImageView) findViewById(R.id.calling);
        callingh = (ImageView) findViewById(R.id.callingh);
        email = (ImageView) findViewById(R.id.email);
        emailh = (ImageView) findViewById(R.id.emailh);
        navigation = (ImageView) findViewById(R.id.navigation);
        share = (ImageView) findViewById(R.id.share);
        dislike = (ImageView) findViewById(R.id.dislike);
        liked = (ImageView) findViewById(R.id.liked);
        disliked = (ImageView) findViewById(R.id.disliked);
        takebarCode = (ImageView) findViewById(R.id.takebarCode);
        readmore = (ImageView) findViewById(R.id.readmore);
        dis = (TextView) findViewById(R.id.dis);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        totalLikes = (TextView) findViewById(R.id.totalLikes);
        clickTxt = (TextView) findViewById(R.id.clickTxt);
        address = (TextView) findViewById(R.id.address);
        validity = (TextView) findViewById(R.id.validity);
        ratingStar = (RatingBar) findViewById(R.id.ratingStar);
        limithideshow = (LinearLayout) findViewById(R.id.limithideshow);
        deshideshow = (LinearLayout) findViewById(R.id.deshideshow);
        limitation = (TextView) findViewById(R.id.limitation);
        mainCat = (TextView) findViewById(R.id.mainCat);
        subCat = (TextView) findViewById(R.id.subCat);
        image.setVisibility(View.VISIBLE);

        id = intent.getStringExtra("id");
        price_status = intent.getStringExtra("price_status");
        bid = intent.getStringExtra("bid");
        bname = intent.getStringExtra("bname");
        busmaincat = intent.getStringExtra("maincat");
        bussubcat = intent.getStringExtra("subcat");
        mcatid = intent.getStringExtra("mcatid");
        titleTxt.setText(intent.getStringExtra("title"));
        title = intent.getStringExtra("title");
        imgDes = intent.getStringExtra("title");
        txtaddress = intent.getStringExtra("business");
        address.setText(intent.getStringExtra("business"));
        qrCode = intent.getStringExtra("qrCode");
        lat = Double.parseDouble(intent.getStringExtra("lat"));
        lng = Double.parseDouble(intent.getStringExtra("long"));
        imagePath = intent.getStringExtra("imagePath");
        company_logo = intent.getStringExtra("imageCompanyPath");
        describe_limitations = intent.getStringExtra("describe_limitations");
        mainCat.setText(res.getString(R.string.jadcat) + intent.getStringExtra("main_cat_name"));
        subCat.setText(res.getString(R.string.jadscat) + intent.getStringExtra("subcategory_name"));
        description = intent.getStringExtra("description");
        advertisementDAOList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.advertisementList);
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
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewImageDescriptionActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        allActiveAdsMidListAdpterPage = new AllActiveAdsMidListAdpterPage(ViewImageDescriptionActivity.this, new ArrayList<>());
        recyclerView.setAdapter(allActiveAdsMidListAdpterPage);

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                // doApiCall();
                if (AppStatus.getInstance(ViewImageDescriptionActivity.this).isOnline()) {
                    synchronized (this) {
                        new getAdvertisement().execute();
                    }

                } else {

                    Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
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
        synchronized (this) {
            if (AppStatus.getInstance(ViewImageDescriptionActivity.this).isOnline()) {
                synchronized (this) {
                    insertUserClick();
                    getImagePath();
                    new getAdvertisement().execute();

                }

            } else {

                Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
            }

        }
        if (intent.getStringExtra("likecnt").equals("0")) {
            totalLikes.setVisibility(View.GONE);
        } else if (intent.getStringExtra("likecnt").equals("1")) {
            totalLikes.setVisibility(View.VISIBLE);
            totalLikes.setText(intent.getStringExtra("likecnt") + " Like");
        } else {
            totalLikes.setVisibility(View.VISIBLE);
            totalLikes.setText(intent.getStringExtra("likecnt") + " Likes");
        }
        if (!description.equals("")) {
            deshideshow.setVisibility(View.VISIBLE);
            dis.setText(description);

        }
        if (!describe_limitations.equals("")) {
            limithideshow.setVisibility(View.VISIBLE);
            limitation.setText(describe_limitations);
        }
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
       /* if (intent.getStringExtra("likecnt").equals("0")) {
            liked.setVisibility(View.GONE);
            like.setVisibility(View.VISIBLE);


        } else if (Integer.parseInt(intent.getStringExtra("likecnt")) > 0) {

            like.setVisibility(View.GONE);
            liked.setVisibility(View.VISIBLE);
        }
        if (intent.getStringExtra("dislikecnt").equals("0")) {

            dislike.setVisibility(View.VISIBLE);
            disliked.setVisibility(View.GONE);

        } else if (Integer.parseInt(intent.getStringExtra("dislikecnt")) > 0) {
            dislike.setVisibility(View.GONE);
            disliked.setVisibility(View.VISIBLE);

        }*/

        validity.setText(parseTime(intent.getStringExtra("stime"), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", intent.getStringExtra("sdate")) + " to " + parseTime(intent.getStringExtra("etime"), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", intent.getStringExtra("edate")));
        // ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        //  imageLoader.DisplayImage(imagePath, image);
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
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewImageDescriptionActivity.this, RegisterNGetStartActivity.class);
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
                    if (askForPermission(Manifest.permission.CALL_PHONE, CALL)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mobile_no));
                        if (ActivityCompat.checkSelfPermission(ViewImageDescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

                    Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + add + ")"));
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
                if (askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST)) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        Uri bmpUri = getLocalBitmapUri(image);
                        if (bmpUri != null) {
                            // Construct a ShareIntent with link to image
                            Intent shareIntent = new Intent();
                            shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on AlertMeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/store/apps/details?id=in.alertmeu).\n\n" + imgDes + " \n" + description + " \n" + validity.getText().toString());
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
        like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewImageDescriptionActivity.this, RegisterNGetStartActivity.class);
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
                        like_status = "1";
                        if (like_status.equals("1")) {
                            disliked.setVisibility(View.GONE);
                            like.setVisibility(View.GONE);
                            dislike.setVisibility(View.VISIBLE);
                            liked.setVisibility(View.VISIBLE);
                        }
                        new initStatusUpdate().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewImageDescriptionActivity.this, RegisterNGetStartActivity.class);
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
                        like_status = "0";
                        if (like_status.equals("0")) {
                            liked.setVisibility(View.GONE);
                            dislike.setVisibility(View.GONE);
                            like.setVisibility(View.VISIBLE);
                            disliked.setVisibility(View.VISIBLE);

                        }
                        new initStatusUpdate().execute();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    builder.setMessage(res.getString(R.string.jprqr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(ViewImageDescriptionActivity.this, RegisterNGetStartActivity.class);
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

                    Intent intent1 = new Intent(ViewImageDescriptionActivity.this, FullScreenViewActivity.class);
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
        if (preferences.getString("account_status", "").equals("1")) {
            takebarCode.setVisibility(View.GONE);
            clickTxt.setVisibility(View.GONE);

        } else {
            takebarCode.setVisibility(View.VISIBLE);
            clickTxt.setVisibility(View.VISIBLE);
        }
        takebarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXSTQ)) {
                    Utility.showLoadingDialogQr(ViewImageDescriptionActivity.this);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                try {

                                    bitmap = TextToImageEncode(preferences.getString("user_id", "") + "#" + id + "#" + qrCode);

                                    // imageView.setImageBitmap(bitmap);
                                    String root = Environment.getExternalStorageDirectory().toString();
                                    File myDir = new File(root + "/AlertMeU");
                                    myDir.mkdirs();
                                    // Random generator = new Random();
                                    // int n = 10000;
                                    //  n = generator.nextInt(n);
                                    fname = "Img-" + Integer.parseInt(preferences.getString("user_id", "") + id) + ".jpg";

                                    File file = new File(myDir, fname);
                                    if (file.exists()) file.delete();
                                    try {
                                        FileOutputStream out = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                        out.flush();
                                        out.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + fname;
                                Bitmap bmp = BitmapFactory.decodeFile(filePath);

                                Utility.dismissLoadingDialog();
                                Intent intent1 = new Intent(ViewImageDescriptionActivity.this, FullScreenViewActivity.class);
                                intent1.putExtra("path", fname);
                                intent1.putExtra("flag", "2");
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
                    }, 1);
                }
            }
        });
        ratingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    //Toast.makeText(getApplicationContext(),"rating"+rating,Toast.LENGTH_SHORT).show();
                    starrating = "" + rating;
                    new initRatingUpdate().execute();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }

            }
        });
        readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewImageDescriptionActivity.this, BusinessProfileActivity.class);
                intent.putExtra("id", bid);
                intent.putExtra("referral_code", qrCode);
                intent.putExtra("lat", "" + lat);
                intent.putExtra("long", "" + lng);
                intent.putExtra("imagePath", company_logo);
                intent.putExtra("title", bname);
                intent.putExtra("sponsor_flag", 0);
                intent.putExtra("business", txtaddress);
                intent.putExtra("mobile_no", mobile_no);
                intent.putExtra("email", emailid);
                intent.putExtra("maincat", busmaincat);
                intent.putExtra("subcat", bussubcat);
                intent.putExtra("price_status", price_status);
                startActivity(intent);
                finish();
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
                        put("advertisment_id", id);
                        put("like_dislike_status", like_status);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERLIKES, jsonLeadObj);
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
                            Toast.makeText(ViewImageDescriptionActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewImageDescriptionActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
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

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("advertisment_id", id);
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
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_GETIMAGELOCPATH, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            msg = jsonObject.getString("message");
                            path = jsonObject.getString("path");
                            dis_like_status = jsonObject.getString("likestatus");
                            //userStar = jsonObject.getString("ratingstar");
                           /* if (!userStar.equals("null") && !userStar.equals("")) {
                                ratingStar.setRating(Float.parseFloat(userStar));
                            }*/

                            String a[] = path.split("#");

                            if (a[1].equals("0")) {
                                totalLikes.setVisibility(View.GONE);
                            } else if (a[1].equals("1")) {
                                totalLikes.setVisibility(View.VISIBLE);
                                totalLikes.setText(a[1] + " Like");
                            } else {
                                totalLikes.setVisibility(View.VISIBLE);
                                totalLikes.setText(a[1] + " Likes");
                            }
                            if (dis_like_status.equals("0")) {
                                liked.setVisibility(View.GONE);
                                dislike.setVisibility(View.GONE);
                                like.setVisibility(View.VISIBLE);
                                disliked.setVisibility(View.VISIBLE);

                            } else if (dis_like_status.equals("1")) {

                                disliked.setVisibility(View.GONE);
                                like.setVisibility(View.GONE);
                                liked.setVisibility(View.VISIBLE);
                                dislike.setVisibility(View.VISIBLE);
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

    public void insertUserClick() {

        jsonSchedule1 = new JSONObject() {
            {
                try {
                    put("advertisment_id", id);
                    put("user_id", preferences.getString("user_id", ""));
                    put("t_zone", localTime);
                    put("title", title);
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
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_INSERTUSERVIEWCOUNTADS, jsonSchedule1);
                Log.i("resp", "imagePathResponse" + imagePathResponse);


            }
        });

        objectThread.start();

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
            mProgressDialog = new ProgressDialog(ViewImageDescriptionActivity.this);
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
                        put("advertisment_id", preferences.getString("id", ""));
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
                            Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
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
                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                getImagePath();


            } else {

                // Toast.makeText(ViewImageDescriptionActivity.this, msg, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                ;
            }
            mProgressDialog.dismiss();

        }
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent intent = new Intent(ViewImageDescriptionActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
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
                // Toast.makeText(getApplicationContext(), "inappropriate", Toast.LENGTH_LONG).show();
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(ViewImageDescriptionActivity.this, RegisterNGetStartActivity.class);
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
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewImageDescriptionActivity.this);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.write_inappropriateads_layout, null);
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
                            userreview = descEdtTxt.getText().toString();
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
            // mProgressDialog = new ProgressDialog(BusinessProfileActivity.this);
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
                        put("advertisment_id", id);
                        put("user_inappropriate", userreview);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERADSINAPPROPRIATE, jsonLeadObj);
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
                            Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ViewImageDescriptionActivity.this, res.getString(R.string.jdus), Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(BusinessProfileActivity.this, msg, Toast.LENGTH_LONG).show();

            }


        }
    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //mProgressDialog = new ProgressDialog(ViewImageDescriptionActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
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
                        put("Bid", bid);
                        put("mcatid", mcatid);
                        put("page", currentPage);
                        put("row_per_page", "20");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLCURRENTACTADSOTHCATBUSINESSIDPAGE, jsonLeadObj1);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            try {
                if (isJSONValid(advertisementListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(advertisementListResponse);
                                status = jsonObject.getBoolean("status");
                                totalPage = jsonObject.getInt("last_page");
                                if (status) {
                                    JsonHelper jsonHelper = new JsonHelper();
                                    advertisementDAOList = jsonHelper.parseAllAdvertisementList(advertisementListResponse);
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
            /*if (advertisementDAOList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                advertisementListAdpter = new ActiveAdvertisementListAdpter(ViewImageDescriptionActivity.this, advertisementDAOList);
                recyclerView.setAdapter(advertisementListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewImageDescriptionActivity.this));
            } else {
                recyclerView.setVisibility(View.GONE);
            }*/
            if (advertisementDAOList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                if (currentPage != PAGE_START) allActiveAdsMidListAdpterPage.removeLoading();
                allActiveAdsMidListAdpterPage.addItems(advertisementDAOList);
                // check weather is last page or not
                if (currentPage < totalPage) {
                    allActiveAdsMidListAdpterPage.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    /*//
    private boolean checkAndRequestPermissions() {


        int permissionReadPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permissionReadPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
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


                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mobile_no));
                        if (ActivityCompat.checkSelfPermission(ViewImageDescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
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
    }*/

    private boolean askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ViewImageDescriptionActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewImageDescriptionActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ViewImageDescriptionActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ViewImageDescriptionActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            switch (requestCode) {

                //Call
                case 1:
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mobile_no));
                    if (ActivityCompat.checkSelfPermission(ViewImageDescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return true;
                    }
                    startActivity(callIntent);
                    break;

                //Read External Storage
                case 2:
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        Uri bmpUri = getLocalBitmapUri(image);
                        if (bmpUri != null) {
                            // Construct a ShareIntent with link to image
                            Intent shareIntent = new Intent();
                            shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on AlertMeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/store/apps/details?id=in.alertmeu).\n\n" + imgDes + " \n" + description + " \n" + validity.getText().toString());
                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/jpg");
                            startActivity(Intent.createChooser(shareIntent, "Share with"));

                        } else {
                            // ...sharing failed, handle error
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        //  Utility.showLoadingDialogQr(ViewImageDescriptionActivity.this);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    try {
                                        synchronized (this) {
                                            // imageView.setImageBitmap(bitmap);
                                            String root = Environment.getExternalStorageDirectory().toString();
                                            File myDir = new File(root + "/AlertMeU");
                                            myDir.mkdirs();
                                            // Random generator = new Random();
                                            // int n = 10000;
                                            //  n = generator.nextInt(n);
                                            fname = "Img-" + preferences.getString("user_id", "") + id + ".png";
                                            File file = new File(myDir, fname);
                                            //   if (file.exists()) file.delete();
                                            if (!file.exists()) {
                                                bitmap = TextToImageEncode(preferences.getString("user_id", "") + "#" + id + "#" + qrCode);

                                                try {
                                                    FileOutputStream out = new FileOutputStream(file);
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                                                    out.flush();
                                                    out.close();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                            Intent intent1 = new Intent(ViewImageDescriptionActivity.this, FullScreenViewActivity.class);
                                            intent1.putExtra("path", fname);
                                            intent1.putExtra("flag", "2");
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
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }
                                    //   String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + fname;
                                    //  Bitmap bmp = BitmapFactory.decodeFile(filePath);

                                    // Utility.dismissLoadingDialog();

                                }
                            }
                        }, 1);
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {

                //Call
                case 1:
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mobile_no));
                    if (ActivityCompat.checkSelfPermission(ViewImageDescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                    break;

                //Read External Storage
                case 2:
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        Uri bmpUri = getLocalBitmapUri(image);
                        if (bmpUri != null) {
                            // Construct a ShareIntent with link to image
                            Intent shareIntent = new Intent();
                            shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on AlertMeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/store/apps/details?id=in.alertmeu).\n\n" + imgDes + " \n" + description + " \n" + validity.getText().toString());
                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/jpg");
                            startActivity(Intent.createChooser(shareIntent, "Share with"));

                        } else {
                            // ...sharing failed, handle error
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        // Utility.showLoadingDialogQr(ViewImageDescriptionActivity.this);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    try {
                                        synchronized (this) {
                                            // imageView.setImageBitmap(bitmap);
                                            String root = Environment.getExternalStorageDirectory().toString();
                                            File myDir = new File(root + "/AlertMeU");
                                            myDir.mkdirs();
                                            // Random generator = new Random();
                                            // int n = 10000;
                                            //  n = generator.nextInt(n);
                                            fname = "Img-" + preferences.getString("user_id", "") + id + ".png";
                                            File file = new File(myDir, fname);
                                            //  if (file.exists()) file.delete();
                                            if (!file.exists()) {
                                                bitmap = TextToImageEncode(preferences.getString("user_id", "") + "#" + id + "#" + qrCode);

                                                try {
                                                    FileOutputStream out = new FileOutputStream(file);
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                                                    out.flush();
                                                    out.close();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            Intent intent1 = new Intent(ViewImageDescriptionActivity.this, FullScreenViewActivity.class);
                                            intent1.putExtra("path", fname);
                                            intent1.putExtra("flag", "2");
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
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }
                                    //  Utility.dismissLoadingDialog();

                                }
                            }
                        }, 1);
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

            // Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }
}
