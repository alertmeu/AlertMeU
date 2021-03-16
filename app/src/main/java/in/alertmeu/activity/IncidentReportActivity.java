package in.alertmeu.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;

import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.text.DateFormat;
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
import in.alertmeu.adapter.GridViewAdapter;
import in.alertmeu.adapter.SubIncidentTypeListAdpter;
import in.alertmeu.adapter.UserSubIncidentTypeListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AlertTypeDAO;
import in.alertmeu.models.AletSubscriptionsDAO;
import in.alertmeu.models.IncidentSubTypeModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.CompressFile;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.MySSLSocketFactory;
import in.alertmeu.utils.Utility;
import in.alertmeu.utils.WebClient;

import static android.graphics.BitmapFactory.decodeFile;

public class IncidentReportActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 60 * 2000; /* 2 sec */

    private LocationManager locationManager;


    double latitude = 0.0;
    double longitude = 0.0;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    Button sendData;

    String userChoosenTask = "";
    private Uri fileUri; // file url to store image
    private int REQUEST_CAMERA = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    int PICK_IMAGE_MULTIPLE = 1;
    static String fileName = "";
    static File destination;
    // LogCat tag
    private static final String TAG = IncidentReportActivity.class.getSimpleName();
    static final Integer CAMERA = 0x1;

    static List<String> imagesEncodedList;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    String imageEncoded;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    GridView grid;
    GridViewAdapter adapter;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj, jsonLeadObjReq, jsonLeadObj1;
    JSONArray jsonArray;
    String addImageLocationResponse = "", message = "", id = "", addRequestAttachResponse = "", incSubTypeResponse = "";
    boolean status;
    int count = 0;
    public ArrayList<String> map = new ArrayList<String>();
    private ProgressDialog dialog;
    MultipartEntity entity;

    String alertResponse = "", incident_type_id = "";
    ArrayList<AlertTypeDAO> alertTypeArrayList;
    ArrayList<AletSubscriptionsDAO> aletSubscriptionsDAOArrayList;
    EditText descriptionEdtTxt, areaEdtTxt;
    String description = "", area = "", incident_type = "", incsubtypeid = "";

    ImageView image;
    String localTime;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    List<IncidentSubTypeModeDAO> data;
    UserSubIncidentTypeListAdpter userSubIncidentTypeListAdpter;
    RecyclerView IncSubTypeList;
    ArrayList<String> subIncTypeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation(); //check whether location service is enable or not in your  phone
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        sendData = (Button) findViewById(R.id.sendData);
        image = (ImageView) findViewById(R.id.image);
        descriptionEdtTxt = (EditText) findViewById(R.id.descriptionEdtTxt);
        areaEdtTxt = (EditText) findViewById(R.id.areaEdtTxt);
        IncSubTypeList = (RecyclerView) findViewById(R.id.IncSubTypeList);
        data = new ArrayList<>();
        if (checkAndRequestPermissions()) {
            cameraIntent();
        }
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new initAlertTypeSpinner().execute();
        } else {

            Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    String temp_size = "" + mArrayUri.size();
                    description = descriptionEdtTxt.getText().toString().trim();
                    area = areaEdtTxt.getText().toString();
                    List<IncidentSubTypeModeDAO> stList = ((UserSubIncidentTypeListAdpter) userSubIncidentTypeListAdpter).getSservicelist();
                    String data1 = "";
                    subIncTypeArrayList = new ArrayList<>();
                    for (int i = 0; i < stList.size(); i++) {
                        IncidentSubTypeModeDAO serviceListDAO = stList.get(i);
                        if (serviceListDAO.isSelected() == true) {
                            data1 = serviceListDAO.getId().toString();
                            subIncTypeArrayList.add(data1);
                        }
                    }
                    incsubtypeid = "";
                    WebClient webClient = new WebClient();
                    incsubtypeid = webClient.convertArrayListToStringWithComma(subIncTypeArrayList);
                    if (validate(incsubtypeid, description, temp_size)) {
                        if (latitude != 0.0 && longitude != 0.0) {
                            //  new rangeAvailable().execute();
                            new addLocationDataDetails().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });

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

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            // mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        // mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        // mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        // latitude = mLocation.getLatitude();
        // longitude = mLocation.getLongitude();
        //  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private class initAlertTypeSpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(IncidentReportActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
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
                        put("country_code", preferences.getString("country_code", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            alertResponse = serviceAccess.SendHttpPost(Config.URL_GETALLINCIDENTREPORT, jsonLeadObj);
            Log.i("resp", "alertResponse" + alertResponse);

            if (alertResponse.compareTo("") != 0) {
                if (isJSONValid(alertResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                alertTypeArrayList = new ArrayList<>();
                                JSONObject jsonObject = new JSONObject(alertResponse);
                                status = jsonObject.getBoolean("status");
                                if (!jsonObject.isNull("dataList")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject json_data = jsonArray.getJSONObject(i);
                                        if (preferences.getString("ulang", "").equals("en")) {
                                            alertTypeArrayList.add(new AlertTypeDAO(json_data.getString("id"), json_data.getString("incident_name")));
                                        } else if (preferences.getString("ulang", "").equals("hi")) {
                                            alertTypeArrayList.add(new AlertTypeDAO(json_data.getString("id"), json_data.getString("incident_name_hindi")));
                                        }

                                    }
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
                            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (alertResponse.compareTo("") != 0) {

                Spinner spinnerCustom = (Spinner) findViewById(R.id.spinnerAlertType);
                ArrayAdapter<AlertTypeDAO> adapter = new ArrayAdapter<AlertTypeDAO>(IncidentReportActivity.this, android.R.layout.simple_spinner_dropdown_item, alertTypeArrayList);
                spinnerCustom.setAdapter(adapter);
                spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                        AlertTypeDAO alertTypeDAO = (AlertTypeDAO) parent.getSelectedItem();
                        //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                        incident_type_id = alertTypeDAO.getId();
                        incident_type = alertTypeDAO.getAlert_name();
                        new InitIncSubTypeSpinner().execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }


                });
                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                mProgressDialog.dismiss();
            }
        }
    }

    //


    private void selectImage() {
        final CharSequence[] items = {"Take Photo"};
        // final CharSequence[] items = {"Take Photo", "Choose from Library"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IncidentReportActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(IncidentReportActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result) {
                        //cameraIntent();
                    }

                } /*else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                }*/
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image
     */
    private static File getOutputMediaFile(int type) {
        // Internal sdcard location
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "CREO");
        // Create the storage directory if it does not exist
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.d(TAG, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;


        if (type == MEDIA_TYPE_IMAGE) {
            fileName = System.currentTimeMillis() + ".jpg";
            mediaFile = new File(folder.getPath() + File.separator + fileName);
            destination = new File(folder.getPath(), fileName);

        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    mArrayUri.add(mImageUri);

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);

                    cursor.close();
                    Log.v("LOG_TAG", "imageEncoded" + imageEncoded);
                    Log.v("LOG_TAG", "Selected Images" + mImageUri);
                    // Create a String array for FilePathStrings
                    FilePathStrings = new String[1];
                    // Create a String array for FileNameStrings
                    FileNameStrings = new String[1];

                    File myFile = new File(mImageUri.getPath());

                    Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(IncidentReportActivity.this, mImageUri));

                    imagesEncodedList.add(getPathFromUri(IncidentReportActivity.this, mImageUri));

                    // Get the path of the image file

                    FilePathStrings[0] = getPathFromUri(IncidentReportActivity.this, mImageUri);
                    // Get the name image file
                    FileNameStrings[0] = getFileName(mImageUri);

                    // Locate the GridView in gridview_main.xml
                    // grid = (GridView) findViewById(R.id.gridview);
                    // Pass String arrays to LazyAdapter Class
                    //  adapter = new GridViewAdapter(this, FilePathStrings, FileNameStrings);
                    // Set the LazyAdapter to the GridView
                    //   grid.setAdapter(adapter);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 1;
                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    image.setImageBitmap(bitmap);


                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(getPathFromUri(IncidentReportActivity.this, uri));
                            Log.v("LOG_TAG", "imageEncoded" + imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        // Create a String array for FilePathStrings
                        FilePathStrings = new String[mArrayUri.size()];
                        // Create a String array for FileNameStrings
                        FileNameStrings = new String[mArrayUri.size()];
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // down sizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                        image.setImageBitmap(bitmap);


                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {
                //  onCaptureImageResult(data);

                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);


                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                    finish();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e);
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void launchUploadActivity(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            mArrayUri.add(Uri.fromFile(destination));
            imagesEncodedList = new ArrayList<String>();
            FilePathStrings = new String[1];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[1];

            imagesEncodedList.add("" + destination);// imagesEncodedList.add(destination);
            // Get the path of the image file
            FilePathStrings[0] = "" + destination;
            // Get the name image file
            FileNameStrings[0] = fileName;
            image.setImageBitmap(bitmap);

        } else {

        }
    }

    private class addLocationDataDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(IncidentReportActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Uploading...");
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
                        put("description", description);
                        put("area", area);
                        put("latitude", latitude);
                        put("longitude", longitude);
                        put("incident_type_id", incident_type_id);
                        put("incsubtypeid", incsubtypeid);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            addImageLocationResponse = serviceAccess.SendHttpPost(Config.URL_ADDINCIDENTREPORTBYUID, jsonLeadObj);
            Log.i("resp", "addRequestChangeResponse" + addImageLocationResponse);


            if (addImageLocationResponse.compareTo("") != 0) {
                if (isJSONValid(addImageLocationResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addImageLocationResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        id = jObject.getString("id");
                        jsonArray = new JSONArray(addImageLocationResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(IncidentReportActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(IncidentReportActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(IncidentReportActivity.this, message, Toast.LENGTH_LONG).show();
                // Close the progressdialog
                mProgressDialog.dismiss();
                if (mArrayUri.size() > 0) {
                    for (int i = 0; i < imagesEncodedList.size(); i++) {
                        map.add(imagesEncodedList.get(i).toString());
                    }
                    new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                } else {
                    finish();
                    Intent intent = new Intent(IncidentReportActivity.this, MapsActivity.class);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(IncidentReportActivity.this, message, Toast.LENGTH_LONG).show();
                // Close the progressdialog
                mProgressDialog.dismiss();

            }
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }

    class ImageUploadTask extends AsyncTask<String, Void, String> {

        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(IncidentReportActivity.this, "Uploading",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                File file = new File(map.get(i));
                File f = CompressFile.getCompressedImageFile(file, IncidentReportActivity.this);
                Bitmap bitmap = decodeFile(f.getAbsolutePath());

                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                fileName = "Inc_" + preferences.getString("user_id", "") + "_cam_" + System.currentTimeMillis() + ".png";
                entity.addPart("user_id", new StringBody("199"));
                entity.addPart("fileName", new StringBody(fileName));
                entity.addPart("club_image", new ByteArrayBody(data, "image/jpeg", params[1]));
                // entity.addPart("club_image", new FileBody(file, "image/jpeg", params[1]));
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                sResponse = EntityUtils.getContentCharSet(response.getEntity());

                System.out.println("sResponse : " + sResponse);
            } catch (Exception e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Log.e(e.getClass().getName(), e.getMessage(), e);

            }

            jsonLeadObjReq = new JSONObject() {
                {
                    try {

                        put("image_path", Config.URL_AlertMeUImage + "uploads/" + fileName);
                        put("business_id", id);
                        put("image_status", "1");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObjReq);
            addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_ADDREQUESTATTACHMENT, jsonLeadObjReq);
            Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
            return sResponse;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    //  Toast.makeText(getApplicationContext(), sResponse + " Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                    count++;
                    if (count < map.size()) {
                        // new ImageUploadTask().execute(count + "", "hm" + count + ".jpg");

                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));

                    } else {
                        finish();
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    public boolean validate(String alert_type, String description, String temp_size) {
        boolean isValidate = false;
        if (alert_type.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please Select Alert type.", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (description.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter description.", Toast.LENGTH_LONG).show();
            isValidate = false;
        } else if (temp_size.equals("0")) {
            Toast.makeText(getApplicationContext(), "Please take image.", Toast.LENGTH_LONG).show();
            isValidate = false;
        } else {
            isValidate = true;
        }
        return isValidate;
    }


    //
    private class InitIncSubTypeSpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("inc_id", incident_type_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            incSubTypeResponse = serviceAccess.SendHttpPost(Config.URL_GETALLINCSUBUSERBYID, jsonLeadObj1);
            Log.i("resp", "alertResponse" + alertResponse);
            try {
                if (incSubTypeResponse.compareTo("") != 0) {
                    if (isJSONValid(incSubTypeResponse)) {

                        try {

                            data.clear();
                            JsonHelper jsonHelper = new JsonHelper();
                            data = jsonHelper.parseUserSubIncTypeList(incSubTypeResponse);
                            jsonArray = new JSONArray(incSubTypeResponse);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {


                        return null;
                    }
                } else {


                    return null;
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //   mProgressDialog.dismiss();

            if (data.size() > 0) {
                IncSubTypeList.setVisibility(View.VISIBLE);
                userSubIncidentTypeListAdpter = new UserSubIncidentTypeListAdpter(IncidentReportActivity.this, data);
                IncSubTypeList.setAdapter(userSubIncidentTypeListAdpter);
                IncSubTypeList.setLayoutManager(new LinearLayoutManager(IncidentReportActivity.this));
                userSubIncidentTypeListAdpter.notifyDataSetChanged();

            } else {
                IncSubTypeList.setVisibility(View.GONE);
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

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    //
    private boolean checkAndRequestPermissions() {

        int writepermission = ContextCompat.checkSelfPermission(IncidentReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(IncidentReportActivity.this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(IncidentReportActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            //requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                        cameraIntent();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(IncidentReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(IncidentReportActivity.this, Manifest.permission.CAMERA)) {
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
        new android.app.AlertDialog.Builder(IncidentReportActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(IncidentReportActivity.this);
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

}

