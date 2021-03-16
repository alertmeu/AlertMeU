package in.alertmeu.utils;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.media.MediaPlayer;


import android.util.Log;
import android.widget.Toast;


import androidx.legacy.content.WakefulBroadcastReceiver;

import com.google.android.gms.common.api.GoogleApiClient;


import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.database.DatabaseQueryClass;
import in.alertmeu.jsonparser.JsonHelper;


public class MyBroadcastReceiver extends WakefulBroadcastReceiver {
    MediaPlayer mp;
    // GPSTracker class
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    SharedPreferences preference;
    SharedPreferences.Editor prefEditor;

    JSONObject jsonReqObj, jsonMainCat, jsonSubCat, jsonObjectSync, syncJsonObject;
    JSONArray jsonArraySync;

    String status = "", maincatResponse = "", subcatResponse = "", syncDataesponse = "";
    boolean mstatus, substatus;
    String localTime;
    String time24;
    int totalsize;
    int countmu = 0;
    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass();

    @Override
    public void onReceive(Context context, Intent intent) {
        preference = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preference.edit();

        gps = new GPSTracker(context);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        Calendar now = Calendar.getInstance();
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        if (!preference.getString("user_id", "").equals("")) {
            getLocation(context, intent);
        }

        int c = preference.getInt("syncc", 0);
        if (c <= 15) {
            c = c + 1;
            prefEditor.putInt("syncc", c);
            prefEditor.commit();
            if (c == 15) {
                synchronized (context) {
                    insertmaincat();
                    insertsubcat();
                }
            }
            if (c == 1) {
                synchronized (context) {
                    insertmaincat();
                    insertsubcat();
                }
            }
        } else {
            prefEditor.putInt("syncc", 0);
            prefEditor.commit();
        }
        int a = now.get(Calendar.AM_PM);
        /*if (a == Calendar.AM) {
            System.out.println("AM" + now.get(Calendar.HOUR));
            String time = "AM" + now.get(Calendar.HOUR);
            if (time.endsWith("AM9") || time.endsWith("AM9") || time.endsWith("AM10") || time.endsWith("AM11")) {
                System.out.println("AM............" + now.get(Calendar.HOUR));
              //  Toast.makeText(context, "helo"+longitude, Toast.LENGTH_SHORT).show();
                getLocation(context, intent);

            } else {
                System.out.println("AM............");
            }

        } else if (a == Calendar.PM) {

            System.out.println("PM" + now.get(Calendar.HOUR));
            String time = "PM" + now.get(Calendar.HOUR);
            if (time.endsWith("PM0") || time.endsWith("PM1") || time.endsWith("PM2") || time.endsWith("PM3") || time.endsWith("PM4") || time.endsWith("PM5") || time.endsWith("PM6") || time.endsWith("PM7") || time.endsWith("PM7") || time.endsWith("PM8")) {
                System.out.println("PM........" + now.get(Calendar.HOUR));
               // Toast.makeText(context, "helo"+longitude, Toast.LENGTH_SHORT).show();
                 getLocation(context, intent);
                // getNewLeadraw(context, intent);
            } else {
                System.out.println("PM............");
            }
        }*/

        //getNewLeadraw(context,intent);
        synchronized (context) {
            Cursor cursor = databaseQueryClass.getUnsyncedCatUpdate();
            totalsize = cursor.getCount();
            jsonArraySync = new JSONArray();
            if (cursor.moveToFirst()) {
                do {
                    //calling the method to save the unsynced name to MySQL
                    //  saveUser(cursor.getInt(cursor.getColumnIndex(Constant.M_ID)), cursor.getInt(cursor.getColumnIndex(Constant.INPUTQTY)), cursor.getString(cursor.getColumnIndex(Constant.REMARK)), cursor.getString(cursor.getColumnIndex(Constant.ISFLOORTOSHEET)));
                    countmu++;
                    jsonObjectSync = new JSONObject();
                    try {
                        jsonObjectSync.put("subbc_id", cursor.getInt(cursor.getColumnIndex(Constant.SUBBC_ID)));
                        jsonObjectSync.put("maincat_id", cursor.getInt(cursor.getColumnIndex(Constant.MAINCAT_ID)));
                        jsonObjectSync.put("status", cursor.getInt(cursor.getColumnIndex(Constant.STATUS)));
                        jsonObjectSync.put("user_id", preference.getString("user_id", ""));
                        jsonArraySync.put(jsonObjectSync);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }

            if (totalsize == 0) {
                //userdetailsPre();
            } else {
                try {
                    syncJsonObject = new JSONObject();
                    syncJsonObject.put("preData", jsonArraySync);
                    Log.d("preData", "" + syncJsonObject);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Thread objectThread = new Thread(new Runnable() {
                    public void run() {
                        // TODO Auto-generated method stub
                        WebClient serviceAccess = new WebClient();
                        syncDataesponse = serviceAccess.SendHttpPost(Config.URL_SHOPPERPRECATSYNCDATAOFFLINE, syncJsonObject);
                        Log.i("resp", "syncDataesponse" + syncDataesponse);

                    }
                });
                objectThread.start();

            }
        }
    }


    private void getLocation(Context context, Intent intent) {

        // create class object
        gps = new GPSTracker(context);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            final String dateToStr = format.format(today);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            final String dateToStr1 = format1.format(today);
            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");
            final String dateToStr2 = format2.format(today);
            String now = new SimpleDateFormat("hh:mm aa").format(new java.util.Date().getTime());
            System.out.println("time in 12 hour format : " + now);
            SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
            SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");


            try {
                time24 = outFormat.format(inFormat.parse(now));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            jsonReqObj = new JSONObject() {
                {
                    try {

                        put("user_id", preference.getString("user_id", ""));
                        put("latitude", latitude);
                        put("longitude", longitude);
                        put("create_at", dateToStr);
                        put("entry_date", dateToStr1);
                        put("time", time24);
                        put("t_zone", localTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            Thread objectThread1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    WebClient serviceAccess = new WebClient();
                    Log.i("json", "json" + jsonReqObj);
                    String TrackResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERLOCATION, jsonReqObj);
                    Log.i("TrackResponse", TrackResponse);

                }
            });

            objectThread1.start();

        }
    }

    public void insertmaincat() {

        jsonMainCat = new JSONObject() {
            {
                try {
                    put("country_code", preference.getString("country_code", ""));
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
                    mstatus = jsonObject.getBoolean("status");

                    if (mstatus) {
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
                    put("country_code", preference.getString("country_code", ""));
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
                    substatus = jsonObject.getBoolean("status");
                    if (substatus) {
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


}

   /* private void update() {
        LocationDAO.deleteAll(LocationDAO.class);
        System.out.println("deleted------>");
    }

    private void getNewLeadraw(final Context context, final Intent intent) {
        final JSONObject jsonReqObj = new JSONObject() {
            {
                try {

                    put(Constant.LEAD_USER_ID, preference.getString("u_id", ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("json exception", "json exception" + e);
                }
            }
        };

        Thread objectThread1 = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonReqObj);
                String newLeadrawResponse = serviceAccess.SendHttpPost(Config.URL_GET_NEWLEAD_RAW, jsonReqObj);
                Log.i("newLeadrawResponse---->", newLeadrawResponse);
                if (true) {
                   *//* ComponentName comp = new ComponentName(context.getPackageName(), NotificationService.class.getName());
                    startWakefulService(context, (intent.setComponent(comp)));
                    setResultCode(Activity.RESULT_OK);*//*
                } else {


                }

            }
        });

        objectThread1.start();

    }*/


