package in.alertmeu.FirebaseNotification;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.R;
import in.alertmeu.activity.AdvertisementExpandableListActivity;
import in.alertmeu.activity.DispalyAdvertisementActivity;
import in.alertmeu.activity.GetNotifyDataActivity;
import in.alertmeu.activity.HomePageActivity;
import in.alertmeu.activity.LMActivity;
import in.alertmeu.activity.MapsActivity;
import in.alertmeu.activity.ShowNotificationActivity;
import in.alertmeu.activity.UserProfileSettingActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.adapter.ActiveAdvertisementListAdpter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.MyApplicatio;
import in.alertmeu.utils.WebClient;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    Intent intent = null;
    String id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {

                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");
            if (title.contains("New Advertisement#")) {
                String a[] = title.split("#");
                title = a[0];
                id = a[1];
            }
            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            if (title.equals("New Alert")) {
                intent = new Intent(getApplicationContext(), MapsActivity.class);
            } else if (title.equals("New Advertisement")) {
                // intent = new Intent(getApplicationContext(), AdvertisementExpandableListActivity.class);
                intent = new Intent(getApplicationContext(), GetNotifyDataActivity.class);
                intent.putExtra("id", id);

            } else if (title.equals("Inactivity Notice")) {
                intent = new Intent(getApplicationContext(), HomePageActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), HomePageActivity.class);
            }
            //if there is no image
            if (imageUrl.equals("null")) {
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                //if there is an image
                //displaying a big notification
                //intent = new Intent(getApplicationContext(), ShowNotificationActivity.class);
                // intent.putExtra("url",imageUrl);
                Uri uri = Uri.parse(imageUrl); // missing 'http://' will cause crashed
                intent = new Intent(Intent.ACTION_VIEW, uri);
                mNotificationManager.showSmallNotification(title, message, intent);
                //mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


}