package in.alertmeu.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;


import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.concurrent.ExecutionException;

import in.alertmeu.R;

import in.alertmeu.activity.BusinessMainCategoryActivity;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.activity.CheckIngLoadActivity;
import in.alertmeu.activity.DisplayAllBusinessActivity;
import in.alertmeu.activity.HomePageActivity;

import in.alertmeu.activity.IncidentReportActivity;
import in.alertmeu.activity.IncidentReportListActivity;
import in.alertmeu.activity.IncidentTypeActivity;
import in.alertmeu.activity.NetPromotorScoreActivity;
import in.alertmeu.activity.RegisterNGetStartActivity;
import in.alertmeu.activity.UserProfileSettingActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;

import in.alertmeu.activity.ViewIncDescriptionActivity;
import in.alertmeu.adapter.MarkerInfoWindowAdapter;
import in.alertmeu.database.DatabaseQueryClass;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.AppUtils;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.FetchAddressIntentService;
import in.alertmeu.utils.GooglePlacesReadTask;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.PlacesDisplayTask;
import in.alertmeu.utils.Utility;
import in.alertmeu.utils.VersionChecker;
import in.alertmeu.utils.WebClient;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private static String TAG = "MAP LOCATION";
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    GoogleMap googleMap;
    double latitude = 0.0;
    double longitude = 0.0;
    double save_latitude = 0.0;
    double save_longitude = 0.0;
    private LatLng mCenterLatLong;
    TextView mLocationMarkerText;
    EditText nameOfThePlace;
    String placeName = "";
    ImageView sendData, showhide, areaUnit, refreshMap, incidentReport, incidentReportList, allOthBusinessList;
    LinearLayout head2, btnNext, locationMarker, ths, thsna;
    private JSONObject jsonLeadObj, jsonSchedule, jsonMainCat, jsonSubCat, jsonUserSubCat;
    ProgressDialog mProgressDialog;
    String placeAddResponse = "";
    boolean status;
    String msg = "", imagePathResponse = "", latestVersion = "", confRefuteResponse1 = "", subcatResponse = "", usersubcatResponse = "";
    String title = "";
    int flag = 0;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    int days = 0;
    String provider;
    private LocationManager locationManager;
    String localTime;
    Resources res;
    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass();
    Context mContext;
    Button redo;
    int redof = 0, redo_s = 0, inc = 0;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    boolean not_first_time_showing_info_window;
    GooglePlacesReadTask googlePlacesReadTask;

    // GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        res = getResources();
        forceUpdate();
        preferences = getActivity().getSharedPreferences("Prefrence", getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putString("t_markers", "" + 0);
        prefEditor.putString("stop", "0");
        prefEditor.commit();
        mContext = getContext();
        mLocationMarkerText = (TextView) v.findViewById(R.id.locationMarkertext);
        nameOfThePlace = (EditText) v.findViewById(R.id.nameOfThePlace);
        sendData = (ImageView) v.findViewById(R.id.sendData);
        showhide = (ImageView) v.findViewById(R.id.showhide);
        areaUnit = (ImageView) v.findViewById(R.id.areaUnit);
        refreshMap = (ImageView) v.findViewById(R.id.refreshMap);
        incidentReportList = (ImageView) v.findViewById(R.id.incidentReportList);
        incidentReport = (ImageView) v.findViewById(R.id.incidentReport);
        allOthBusinessList = (ImageView) v.findViewById(R.id.allOthBusinessList);
        btnNext = (LinearLayout) v.findViewById(R.id.btnNext);
        head2 = (LinearLayout) v.findViewById(R.id.head2);
        ths = (LinearLayout) v.findViewById(R.id.ths);
        thsna = (LinearLayout) v.findViewById(R.id.thsna);
        redo = (Button) v.findViewById(R.id.redo);
        locationMarker = (LinearLayout) v.findViewById(R.id.locationMarker);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        fragment.getMapAsync(this);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            synchronized (this) {
                getImagePath();

            }

        } else {

            Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        try {
            PlacesDisplayTask.bindListener(new Listener() {
                @Override
                public void messageReceived(String messageText) {
                    flag = 0;
                    if (status == true)
                        if (!messageText.equals("0")) {
                            thsna.setVisibility(View.GONE);
                            ths.setVisibility(View.VISIBLE);
                        } else {
                            ths.setVisibility(View.GONE);
                            thsna.setVisibility(View.VISIBLE);
                        }
                    /*if (AppStatus.getInstance(getActivity()).isOnline()) {
                        // Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();



                    } else {

                        Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }*/
                }
            });

        } catch (Exception e) {
        }
        mResultReceiver = new AddressResultReceiver(new Handler());
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(getActivity())) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
            Toast.makeText(getActivity(), "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    placeName = nameOfThePlace.getText().toString().trim();
                    if (!placeName.equals("")) {
                        // Toast.makeText(getActivity(), "lat n long" + save_latitude + "," + save_longitude, Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(res.getString(R.string.jdwant) + placeName + res.getString(R.string.jflwant))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new submitData().execute();
                                        dialog.cancel();


                                    }
                                })
                                .setNegativeButton(res.getString(R.string.jno), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        //  alert.setTitle("Adding Favorite place");
                        alert.show();


                    } else {
                        Toast.makeText(getActivity(), res.getString(R.string.jfpe)
                                , Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                            , Toast.LENGTH_SHORT).show();
                }
            }

        });

        HomePageActivity.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                // Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();
                title = messageText;
                // updateMapData();

            }
        });
        refreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                redof = 1;
                redo.setVisibility(View.GONE);
                latitude = save_latitude;
                longitude = save_longitude;
                // Toast.makeText(getActivity(), res.getString(R.string.jrepre), Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(getActivity(), res.getString(R.string.jrepre), Toast.LENGTH_LONG);
//the default toast view group is a relativelayout
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(30);
                toast.show();

                googlePlacesReadTask.cancel(true);
                //  prefEditor.putString("stop", "1");
                //    prefEditor.commit();
                getImagePath();
                updateMapData();
                if (!preferences.getString("account_status", "").equals("1")) {
                    incidentReport.setVisibility(View.VISIBLE);
                    incidentReportList.setVisibility(View.VISIBLE);
                }

            }
        });
        showhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage(res.getString(R.string.jfpe));
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                placeName = nameOfThePlace.getText().toString().trim();
                input.setText(placeName);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                // alertDialog.setIcon(R.drawable.msg_img);

                alertDialog.setPositiveButton(res.getString(R.string.xsub)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                placeName = input.getText().toString();
                                if (AppStatus.getInstance(getActivity()).isOnline()) {
                                    if (!placeName.equals("")) {
                                        new submitData().execute();
                                    } else {
                                        Toast.makeText(getActivity(), res.getString(R.string.jfpe), Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                                            , Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                alertDialog.setNegativeButton(res.getString(R.string.helpCan)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });

        areaUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage(res.getString(R.string.jfd));
                final EditText input = new EditText(getActivity());
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                input.setText("" + preferences.getInt("units_for_area", 0));
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                // alertDialog.setIcon(R.drawable.msg_img);

                alertDialog.setPositiveButton(res.getString(R.string.jconfirm)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String entity = input.getText().toString();
                                prefEditor.putInt("units_for_area", Integer.parseInt(entity));
                                int km = Integer.parseInt(entity);
                                if (km < 1) {
                                    km = 1;
                                } else if (km > 999) {
                                    km = 999;
                                }
                                //int zoom = km;
                                prefEditor.commit();
                                Toast.makeText(getActivity(), res.getString(R.string.jsad) + entity + res.getString(R.string.jkm), Toast.LENGTH_LONG).show();
                                if (AppStatus.getInstance(getActivity()).isOnline()) {
                                    updateMapData();
                                    int zoom = (int) (Math.log(25000 / km) / Math.log(2));
                                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
                                } else {

                                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                alertDialog.setNegativeButton(res.getString(R.string.helpCan)
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
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
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    Intent intent = new Intent(getActivity(), BusinessMainCategoryActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                redof = 1;
                redo.setVisibility(View.GONE);
                latitude = save_latitude;
                longitude = save_longitude;
                googlePlacesReadTask.cancel(true);
                synchronized (this) {
                    getImagePath();
                    updateMapData();
                }
                //   Toast.makeText(getActivity(), "Lat Long" + save_latitude + "/" + save_longitude + " " + redof, Toast.LENGTH_SHORT).show();

            }
        });
        incidentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(res.getString(R.string.jpr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg)
                                    , new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(getActivity(), RegisterNGetStartActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
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
                    if (AppStatus.getInstance(getActivity()).isOnline()) {
                        getIncPrePath();
                       /* if (inc == 1) {
                            if (checkAndRequestPermissions()) {
                                prefEditor.putString("stop", "1");
                                prefEditor.commit();
                                Intent intent = new Intent(getActivity(), IncidentReportActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(getActivity(), IncidentTypeActivity.class);
                            startActivity(intent);
                        }*/

                    } else {

                        Toast.makeText(getActivity(), res.getString(R.string.jpcnc)
                                , Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        incidentReportList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    prefEditor.putString("stop", "1");
                    prefEditor.commit();
                    latitude = save_latitude;
                    longitude = save_longitude;
                    googlePlacesReadTask.cancel(true);
                    Intent intent = new Intent(getActivity(), IncidentReportListActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        allOthBusinessList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    // Intent intent = new Intent(getActivity(), CheckIngLoadActivity.class);
                    prefEditor.putString("incback", "0");
                    prefEditor.putString("stop", "1");
                    prefEditor.commit();
                    googlePlacesReadTask.cancel(true);
                    latitude = save_latitude;
                    longitude = save_longitude;
                    Intent intent = new Intent(getActivity(), DisplayAllBusinessActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);

                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        prefEditor.putString("stop", "0");
        prefEditor.commit();
       /* //Toast.makeText(getActivity(),"total days"+days,Toast.LENGTH_SHORT).show();
        try {
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateToStr = format.format(today);
            if (!preferences.getString("npsdate", "").equals("null")) {
                if (dateToStr.contains(preferences.getString("npsdate", ""))) {
                    Intent intent = new Intent(getActivity(), NetPromotorScoreActivity.class);
                    intent.putExtra("uflagscreen", "1");
                    startActivity(intent);
                } else {
                    //Toast.makeText(getApplicationContext(), preferences.getString("npsdate", "") + "/" + dateToStr, Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(getActivity(), NetPromotorScoreActivity.class);
                intent.putExtra("uflagscreen", "1");
                startActivity(intent);
            }

        } catch (Exception e) {
            // Toast.makeText(getActivity(), preferences.getString("npsdate", "") + "/" + e, Toast.LENGTH_SHORT).show();

        }*/
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        Log.d(TAG, "OnMapReady");
        googleMap = Map;
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                // googleMap.clear();

                try {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setCostAllowed(true);
                    criteria.setPowerRequirement(Criteria.POWER_LOW);
                    criteria.setAltitudeRequired(false);
                    criteria.setBearingRequired(false);
                    //API level 9 and up
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    provider = locationManager.getBestProvider(criteria, true);


                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                    save_latitude = mCenterLatLong.latitude;
                    save_longitude = mCenterLatLong.longitude;
                    // mLocationMarkerText.setText("Lat : " + String.format("%.06f", mCenterLatLong.latitude) + "," + "Long : " + String.format("%.06f", mCenterLatLong.longitude));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        LatLng latLng;
        if (preferences.getString("favloc", "").equals("0")) {
            latLng = new LatLng(latitude, longitude);
            prefEditor.putString("favlat", "" + latitude);
            prefEditor.putString("favlong", "" + longitude);
            prefEditor.commit();
        } else {
            latitude = Double.parseDouble(preferences.getString("favlat", ""));
            longitude = Double.parseDouble(preferences.getString("favlong", ""));
            latLng = new LatLng(latitude, longitude);
            if (AppStatus.getInstance(getActivity()).isOnline()) {
                //  updateMapData();
            } else {

                Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
            }

        }
        try {
            if (ActivityCompat.checkSelfPermission((Activity) mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 10);
            }
            int km = preferences.getInt("units_for_area", 0);
            if (km < 1) {
                km = 1;
            } else if (km > 999) {
                km = 999;
            }
            int zoom = (int) (Math.log(25000 / km) / Math.log(2));
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        } catch (Exception e) {

        }

    /*    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng position = marker.getPosition(); //
                String a[] = marker.getSnippet().split("#");
               *//* prefEditor.putString("id", marker.getTitle());
                prefEditor.putString("imagePath",a[0]);
                prefEditor.putString("qrCode", a[1]);
                prefEditor.putString("lat", "" + position.latitude);
                prefEditor.putString("long", "" + position.longitude);
                prefEditor.commit();*//*
                //Toast.makeText(getActivity(), "Lat " + position.latitude + " " + "Long " + position.longitude,Toast.LENGTH_LONG).show();
                // LocationDetailsView locationDetailsView = new LocationDetailsView();
                //  locationDetailsView.show(getActivity().getSupportFragmentManager(), "locationDetailsView");
                Intent intent = new Intent(getActivity(), BusinessProfileActivity.class);
                intent.putExtra("id", marker.getTitle());
                intent.putExtra("referral_code", a[1]);
                intent.putExtra("lat", "" + "" + position.latitude);
                intent.putExtra("long", "" + position.longitude);
                intent.putExtra("imagePath", a[0]);
                intent.putExtra("title", a[2]);
                intent.putExtra("sponsor_flag", a[5]);
                intent.putExtra("business", a[2] + "\n" + a[3]);
                intent.putExtra("mobile_no", a[4]);
                intent.putExtra("email", a[6]);
                startActivity(intent);





                return true;
            }
        });*/

        // Setting a custom info window adapter for the google map
        MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getActivity());
        googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng position = marker.getPosition(); //
                String a[] = marker.getSnippet().split("#");
              /*
                Intent intent = new Intent(getActivity(), BusinessProfileActivity.class);
                intent.putExtra("id", marker.getTitle());
                intent.putExtra("referral_code", a[1]);
                intent.putExtra("lat", "" + "" + position.latitude);
                intent.putExtra("long", "" + position.longitude);
                intent.putExtra("imagePath", a[0]);
                intent.putExtra("title", a[2]);
                intent.putExtra("sponsor_flag", a[5]);
                intent.putExtra("business", a[2] + "\n" + a[3]);
                intent.putExtra("mobile_no", a[4]);
                intent.putExtra("email", a[6]);
                startActivity(intent);*/

                marker.showInfoWindow();
                    /*AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    final View view = factory.inflate(R.layout.edit_reviewcustom_layout, null);
                    alertadd.setView(view);
                    alertadd.show();*/


                return true;
            }
        });

        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker args) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker args) {

                LatLng latLng = args.getPosition();
                String a[] = args.getSnippet().split("#");
                // Getting view from the layout file info_window_layout
                View v = null;
                if (a[11].equals("2")) {
                    v = getLayoutInflater().inflate(R.layout.map_marker_info_window, null);

                    // Getting the position from the marker
                    // clickMarkerLatLng = args.getPosition();


                    TextView textViewName = (TextView) v.findViewById(R.id.textViewName);
                    TextView textViewTotal = (TextView) v.findViewById(R.id.textViewTotal);
                    RatingBar avgratingStar = (RatingBar) v.findViewById(R.id.avgratingStar);
                    ImageView imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
                    textViewName.setText(a[2]);
                    if (a[8].equals("1")) {
                        textViewTotal.setVisibility(View.VISIBLE);
                        textViewTotal.setText(a[8] + " " + res.getString(R.string.xwars));
                    } else if (a[8].equals("0")) {
                        textViewTotal.setVisibility(View.GONE);
                    } else {
                        textViewTotal.setVisibility(View.VISIBLE);
                        textViewTotal.setText(a[8] + " " + res.getString(R.string.xers));
                    }
                    try {
                        if (a[0].contains("http")) {
                            if (not_first_time_showing_info_window) {

                                imageViewIcon.setVisibility(View.VISIBLE);
                                Picasso.get().load(a[0]).into(imageViewIcon);

                            } else { // if it's the first time, load the image with the callback set
                                not_first_time_showing_info_window = true;
                                imageViewIcon.setVisibility(View.VISIBLE);
                                Picasso.get().load(a[0]).into(imageViewIcon, new InfoWindowRefresher(args));
                            }
                        } else {
                            imageViewIcon.setVisibility(View.VISIBLE);
                            imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.defaultbusimg));
                        }
                    } catch (Exception e) {

                    }
                    if (!a[7].equals("null")) {
                        avgratingStar.setRating(Float.parseFloat(a[7]));
                    }
                } else {
                    v = getLayoutInflater().inflate(R.layout.map_marker_info_windowinc, null);

                    // Getting the position from the marker
                    // clickMarkerLatLng = args.getPosition();


                    TextView textViewName = (TextView) v.findViewById(R.id.textViewName);
                    TextView desc = (TextView) v.findViewById(R.id.desc);
                    TextView textViewTotal = (TextView) v.findViewById(R.id.textViewTotal);
                    TextView textrefuteTotal = (TextView) v.findViewById(R.id.textrefuteTotal);
                    TextView txtdate = (TextView) v.findViewById(R.id.txtdate);
                    ImageView imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
                    textViewName.setText(a[9]);
                    txtdate.setText(formateDateFromstring("yyyy-MM-dd HH:mm", "dd-MMM-yyyy hh:mm aa", a[6]));

                    if (!a[2].equals("")) {
                        desc.setVisibility(View.VISIBLE);
                        desc.setText(a[2]);
                    } else {
                        desc.setVisibility(View.GONE);
                    }

                    if (a[8].equals("1")) {
                        textViewTotal.setVisibility(View.VISIBLE);
                        textViewTotal.setText(res.getString(R.string.xocnby) + " " + a[8]);
                    } else if (a[8].equals("0")) {
                        textViewTotal.setVisibility(View.GONE);
                    } else {
                        textViewTotal.setVisibility(View.VISIBLE);
                        textViewTotal.setText(res.getString(R.string.xocnby) + " " + a[8]);
                    }

                    if (a[12].equals("1")) {
                        textrefuteTotal.setVisibility(View.VISIBLE);
                        textrefuteTotal.setText(res.getString(R.string.xorefby) + " " + a[12]);
                    } else if (a[12].equals("0")) {
                        textrefuteTotal.setVisibility(View.GONE);
                    } else {
                        textrefuteTotal.setVisibility(View.VISIBLE);
                        textrefuteTotal.setText(res.getString(R.string.xorefby) + " " + a[12]);
                    }


                    try {
                        if (not_first_time_showing_info_window) {

                            if (a[0].contains("http")) {
                                imageViewIcon.setVisibility(View.VISIBLE);
                                Picasso.get().load(a[0]).into(imageViewIcon);

                            } else {
                                imageViewIcon.setVisibility(View.GONE);
                            }
                        } else { // if it's the first time, load the image with the callback set

                            not_first_time_showing_info_window = true;
                            imageViewIcon.setVisibility(View.VISIBLE);
                            Picasso.get().load(a[0]).into(imageViewIcon, new InfoWindowRefresher(args));
                        }
                    } catch (Exception e) {

                    }


                }
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {

                        LatLng position = marker.getPosition(); //
                        String a[] = marker.getSnippet().split("#");
                        if (a[11].equals("2")) {
                            Intent intent = new Intent(getActivity(), BusinessProfileActivity.class);
                            intent.putExtra("id", marker.getTitle());
                            intent.putExtra("referral_code", a[1]);
                            intent.putExtra("lat", "" + "" + position.latitude);
                            intent.putExtra("long", "" + position.longitude);
                            intent.putExtra("imagePath", a[0]);
                            intent.putExtra("title", a[2]);
                            intent.putExtra("sponsor_flag", a[5]);
                            intent.putExtra("business", a[2] + "\n" + a[3]);
                            intent.putExtra("mobile_no", a[4]);
                            intent.putExtra("email", a[6]);
                            intent.putExtra("maincat", a[9]);
                            intent.putExtra("subcat", a[10]);
                            intent.putExtra("price_status", a[12]);
                            startActivity(intent);
                        } else {
                            prefEditor.putString("incback", "1");
                            prefEditor.commit();
                            Intent intent = new Intent(getActivity(), ViewIncDescriptionActivity.class);
                            intent.putExtra("id", marker.getTitle());
                            intent.putExtra("referral_code", a[1]);
                            intent.putExtra("lat", "" + "" + position.latitude);
                            intent.putExtra("long", "" + position.longitude);
                            intent.putExtra("imagePath", a[0]);
                            intent.putExtra("title", a[9]);
                            intent.putExtra("sponsor_flag", a[5]);
                            intent.putExtra("business", a[2] + "\n" + a[3]);
                            intent.putExtra("mobile_no", a[2]);
                            intent.putExtra("email", a[6]);
                            intent.putExtra("maincat", a[6]);
                            intent.putExtra("subcat", a[10]);
                            startActivity(intent);
                        }

                    }
                });

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                //Toast.makeText(getActivity(), "zoom out", Toast.LENGTH_SHORT).show();

            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Toast.makeText(getApplicationContext(), "zoom in", Toast.LENGTH_SHORT).show();
            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                //  googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                save_latitude = latLng.latitude;
                save_longitude = latLng.longitude;
                // Placing a marker on the touched position
                // googleMap.addMarker(markerOptions);
            }
        });

        if (AppStatus.getInstance(getActivity()).isOnline()) {
            // updateMapData();
        } else {

            Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

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

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (googleMap != null) {

            googleMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            if (preferences.getString("favloc", "").equals("0")) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                latLong = new LatLng(location.getLatitude(), location.getLongitude());
                prefEditor.putString("favlat", "" + latitude);
                prefEditor.putString("favlong", "" + longitude);
                prefEditor.commit();
            } else {
                latitude = Double.parseDouble(preferences.getString("favlat", ""));
                longitude = Double.parseDouble(preferences.getString("favlong", ""));
                latLong = new LatLng(latitude, longitude);
                // updateMapData();
            }
            save_latitude = location.getLatitude();
            save_longitude = location.getLongitude();
            /*  CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(10f).tilt(70).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            int km = preferences.getInt("units_for_area", 0);
            if (km < 1) {
                km = 1;
            } else if (km > 999) {
                km = 999;
            }
            int zoom = (int) (Math.log(25000 / km) / Math.log(2));
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            // mLocationMarkerText.setText("Lat : " + String.format("%.06f", latitude) + "," + "Long : " + String.format("%.06f", latitude));
            // startIntentService(location);
            if (AppStatus.getInstance(getActivity()).isOnline()) {
                updateMapData();
            } else {

                Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateMapData() {
        googleMap.clear();
        if (flag == 0) {
            if (latitude != 0.0) {
                // Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
                prefEditor.putInt("page_no", 1);
                prefEditor.putInt("cntloop", 0);
                prefEditor.putString("clat", "" + latitude);
                prefEditor.putString("clong", "" + longitude);
                prefEditor.commit();
                googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[6];
                toPass[0] = googleMap;
                toPass[1] = title;
                toPass[2] = "" + preferences.getInt("units_for_area", 0);
                toPass[3] = "" + days;
                toPass[4] = getActivity();
                toPass[5] = preferences.getString("user_id", "");

                //latitude = location.getLatitude();
                // longitude = location.getLongitude();

                googlePlacesReadTask.execute(toPass);

            }
            // prefEditor.putString("stop", "0");
            // prefEditor.commit();
            flag = 1;
        }
    }

    private class submitData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsd));
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
                        put("full_address", placeName);
                        put("latitude", save_latitude);
                        put("longitude", save_longitude);
                        put("t_zone", localTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            placeAddResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERPLACES, jsonLeadObj);
            Log.i("resp", "placeAddResponse" + placeAddResponse);


            if (placeAddResponse.compareTo("") != 0) {
                if (isJSONValid(placeAddResponse)) {


                    try {

                        JSONObject jsonObject = new JSONObject(placeAddResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {


                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();


                }
            } else {

                Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(getActivity(), res.getString(R.string.jpadds), Toast.LENGTH_SHORT).show();

                nameOfThePlace.setText("");
                // Close the progressdialog
                mProgressDialog.dismiss();

            } else {
                // Close the progressdialog
                mProgressDialog.dismiss();

            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        Context ctx = (Context) HomeFragment.this.getActivity();
        ctx.startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    private void displayAddressOutput() {

        if (mAreaOutput != null) {
            // mLocationMarkerText.setVisibility(View.VISIBLE);
            // mLocationMarkerText.setText(mAreaOutput);
            //  nameOfThePlace.setText(mAreaOutput);

        } else {
            // nameOfThePlace.setText("");
            // mLocationMarkerText.setVisibility(View.GONE);
            // redo.setVisibility(View.GONE);
        }
        if (redof > 1) {
            redo.setVisibility(View.VISIBLE);
        }
        redof++;
        // Toast.makeText(getActivity(), "Lat Long" + save_latitude + "/" + save_longitude + " " + redof, Toast.LENGTH_SHORT).show();

    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
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
                // WebClient serviceAccess = new WebClient();
                //Log.i("json", "json" + jsonSchedule);
                //  imagePathResponse = serviceAccess.SendHttpPost(Config.URL_CUCBID, jsonSchedule);
                // Log.i("resp", "imagePathResponse" + imagePathResponse);


                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int uid = databaseQueryClass.getUserCheck(Integer.parseInt(preferences.getString("user_id", "")));
                                if (uid == 0) {
                                    status = false;
                                    head2.setVisibility(View.GONE);
                                  //  locationMarker.setVisibility(View.GONE);
                                    ths.setVisibility(View.GONE);
                                    thsna.setVisibility(View.GONE);
                                    btnNext.setVisibility(View.VISIBLE);
                                    prefEditor.remove("m_id_l");
                                    prefEditor.commit();
                                } else {
                                    status = true;
                                    btnNext.setVisibility(View.GONE);
                                    head2.setVisibility(View.VISIBLE);
                                  //  locationMarker.setVisibility(View.VISIBLE);
                                }

                            } catch (NumberFormatException e) {

                            }
                     /*       try {
                                JSONObject jsonObject = new JSONObject(imagePathResponse);
                                status = jsonObject.getBoolean("status");
                                msg = jsonObject.getString("message");
                                if (status) {
                                    btnNext.setVisibility(View.GONE);
                                    head2.setVisibility(View.VISIBLE);
                                    locationMarker.setVisibility(View.VISIBLE);
                                    //  ths.setVisibility(View.VISIBLE);
                                } else {
                                    head2.setVisibility(View.GONE);
                                    locationMarker.setVisibility(View.GONE);
                                    ths.setVisibility(View.GONE);
                                    thsna.setVisibility(View.GONE);
                                    btnNext.setVisibility(View.VISIBLE);
                                    prefEditor.remove("m_id_l");
                                    prefEditor.commit();
                                }


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }*/

                        }
                    });
                } catch (Exception e) {
                }

            }
        });

        objectThread.start();

    }

    public void getIncPrePath() {

        jsonSchedule = new JSONObject() {
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
                //Log.i("json", "json" + jsonSchedule);
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_CUIPID, jsonSchedule);
                // Log.i("resp", "imagePathResponse" + imagePathResponse);
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(imagePathResponse);
                                status = jsonObject.getBoolean("status");
                                msg = jsonObject.getString("message");
                                if (status) {
                                    //  inc = 1;
                                    if (checkAndRequestPermissions()) {
                                        prefEditor.putString("stop", "1");
                                        prefEditor.commit();
                                        Intent intent = new Intent(getActivity(), IncidentReportActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    // inc = 0;
                                    Intent intent = new Intent(getActivity(), IncidentTypeActivity.class);
                                    startActivity(intent);
                                }


                            } catch (
                                    JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (
                        Exception e) {
                }

            }
        });

        objectThread.start();

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

    public void forceUpdate() {
        //  int playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString("android_latest_version_code");
        VersionChecker versionChecker = new VersionChecker();
        try {
            latestVersion = versionChecker.execute().get();
            /*if (latestVersion.length() > 0) {
                latestVersion = latestVersion.substring(50, 58);
                latestVersion = latestVersion.trim();
            }*/


            Log.d("versoncode", "" + latestVersion);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //  String currentVersion = packageInfo.versionName;
        String currentVersion = packageInfo.versionName;

        new ForceUpdateAsync(currentVersion, getActivity()).execute();

    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {


        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context) {
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {


            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!latestVersion.equals("")) {
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();

                        if (!((Activity) context).isFinishing()) {
                            showForceUpdateDialog();
                        }


                    }
                } else {
                    if (AppStatus.getInstance(getActivity()).isOnline()) {

                        // AppUpdater appUpdater = new AppUpdater((Activity) context);
                        //  appUpdater.start();
                    } else {

                        Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }

                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));

            alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
            alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.helpCan, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }

   /* //
    public void insertmaincat() {

        jsonMainCat = new JSONObject() {
            {
                try {
                    put("country_code", preferences.getString("country_code", ""));
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
                Log.i("json", "json" + jsonMainCat);
                maincatResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMAINCATFOFFLINE, jsonMainCat);
                Log.i("resp", "maincatResponse" + maincatResponse);
                try {
                    JSONObject jsonObject = new JSONObject(maincatResponse);
                    status = jsonObject.getBoolean("status");

                    if (status) {
                        JsonHelper jsonHelper = new JsonHelper();
                        long i = jsonHelper.parsemainCatOfflineList(maincatResponse);

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

    public void insertsubcat() {

        jsonSubCat = new JSONObject() {
            {
                try {
                    put("country_code", preferences.getString("country_code", ""));
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
                Log.i("json", "json" + jsonSubCat);
                subcatResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSUBCATFOFFLINE, jsonSubCat);
                Log.i("resp", "subcatResponse" + subcatResponse);
                try {
                    JSONObject jsonObject = new JSONObject(subcatResponse);
                    status = jsonObject.getBoolean("status");

                    if (status) {
                        JsonHelper jsonHelper = new JsonHelper();
                        long i = jsonHelper.parseSubCatOfflineList(subcatResponse);

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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


            }
        });

        objectThread.start();

    }*/

    //
    private boolean checkAndRequestPermissions() {

        int writepermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            // ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //finish();
                        Intent intent = new Intent(getActivity(), IncidentReportActivity.class);
                        startActivity(intent);
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
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
                                                    dialog.dismiss();
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
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                        getActivity().finish();
                    }
                });
        dialog.show();
    }

    //
    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
            not_first_time_showing_info_window = false;
        }

        @Override
        public void onError(Exception e) {
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


}
