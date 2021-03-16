package in.alertmeu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import in.alertmeu.R;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.LocationDAO;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<LocationDAO>> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String googlePlacesJson;
    GoogleMap googleMap;
    private static Listener mListener;
    private static Listener mListener1;
    Context context;
   int count=0;
    @Override
    protected List<LocationDAO> doInBackground(Object... inputObj) {
        context = (Context) inputObj[2];
        List<LocationDAO> data = null;
        preferences = MyApplicatio.getInstance().getSharedPreferences("Prefrence", MyApplicatio.getInstance().MODE_PRIVATE);
        prefEditor = preferences.edit();
        JsonHelper jsonHelper = new JsonHelper();


        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = (String) inputObj[1];
            if (isJSONValid(googlePlacesJson)) {
               // data = jsonHelper.parseLocationList(googlePlacesJson);

                data = jsonHelper.parseLocationListPage(googlePlacesJson);
            }

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(List<LocationDAO> list) {
        // googleMap.clear();
        count = preferences.getInt("cntloop", 0);
        try {
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                List<LocationDAO> listData = list;
                double lat = Double.parseDouble(listData.get(i).getLatitude());
                double lng = Double.parseDouble(listData.get(i).getLongitude());
                // String placeName = googlePlace.get("place_name");
                // String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(listData.get(i).getId());

                if (listData.get(i).getStatus().equals("2")) {
                    View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
                    TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
                    numTxt.setText("" + (count + 1));
                    count++;
                    if (preferences.getString("ulang", "").equals("en")) {
                        // markerOptions.snippet(listData.get(i).getPath() + "#" + listData.get(i).getRq_code() + "#" + listData.get(i).getTitle() + "#" + listData.get(i).getDescription() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getS_time() + "#" + listData.get(i).getE_time() + "#" + listData.get(i).getS_date() + "#" + listData.get(i).getE_date() + "#" + listData.get(i).getLikecnt() + "#" + listData.get(i).getDislikecnt() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getDescribe_limitations() + "#" + listData.get(i).getBusiness_main_category() + "#" + listData.get(i).getBusiness_subcategory() + "#" + listData.get(i).getBusiness_email());
                        Marker myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(listData.get(i).getId()).snippet(listData.get(i).getPath() + "#" + listData.get(i).getReferral_code() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getSponsor_flag() + "#" + listData.get(i).getBusiness_email() + "#" + listData.get(i).getpBidavgrating() + "#" + listData.get(i).getTorcount() + "#" + listData.get(i).getBusiness_main_category() + "#" + listData.get(i).getBusiness_subcategory() + "#" + listData.get(i).getStatus() + "#" + listData.get(i).getPrice_status()).icon(BitmapDescriptorFactory.fromBitmap(Utility.createDrawableFromView(context, marker))));

                    } else if (preferences.getString("ulang", "").equals("hi")) {
                        //markerOptions.snippet(listData.get(i).getPath() + "#" + listData.get(i).getRq_code() + "#" + listData.get(i).getTitle() + "#" + listData.get(i).getDescription() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getS_time() + "#" + listData.get(i).getE_time() + "#" + listData.get(i).getS_date() + "#" + listData.get(i).getE_date() + "#" + listData.get(i).getLikecnt() + "#" + listData.get(i).getDislikecnt() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getDescribe_limitations() + "#" + listData.get(i).getMain_cat_name_hindi() + "#" + listData.get(i).getSubcategory_name_hindi() + "#" + listData.get(i).getBusiness_email());
                        Marker myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(listData.get(i).getId()).snippet(listData.get(i).getPath() + "#" + listData.get(i).getReferral_code() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getSponsor_flag() + "#" + listData.get(i).getBusiness_email() + "#" + listData.get(i).getpBidavgrating() + "#" + listData.get(i).getTorcount() + "#" + listData.get(i).getMain_cat_name_hindi() + "#" + listData.get(i).getSubcategory_name_hindi() + "#" + listData.get(i).getStatus() + "#" + listData.get(i).getPrice_status()).icon(BitmapDescriptorFactory.fromBitmap(Utility.createDrawableFromView(context, marker))));

                    }
                } else {
                    View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_markerinc_layout, null);
                    TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
                    numTxt.setText("" + (count + 1));
                    count++;
                    if (preferences.getString("ulang", "").equals("en")) {
                        // markerOptions.snippet(listData.get(i).getPath() + "#" + listData.get(i).getRq_code() + "#" + listData.get(i).getTitle() + "#" + listData.get(i).getDescription() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getS_time() + "#" + listData.get(i).getE_time() + "#" + listData.get(i).getS_date() + "#" + listData.get(i).getE_date() + "#" + listData.get(i).getLikecnt() + "#" + listData.get(i).getDislikecnt() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getDescribe_limitations() + "#" + listData.get(i).getBusiness_main_category() + "#" + listData.get(i).getBusiness_subcategory() + "#" + listData.get(i).getBusiness_email());
                        Marker myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(listData.get(i).getId()).snippet(listData.get(i).getPath() + "#" + listData.get(i).getReferral_code() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getSponsor_flag() + "#" + listData.get(i).getBusiness_email() + "#" + listData.get(i).getpBidavgrating() + "#" + listData.get(i).getTorcount() + "#" + listData.get(i).getBusiness_main_category() + "#" + listData.get(i).getBusiness_subcategory() + "#" + listData.get(i).getStatus() + "#" + listData.get(i).getPrice_status()).icon(BitmapDescriptorFactory.fromBitmap(Utility.createDrawableFromView(context, marker))));

                    } else if (preferences.getString("ulang", "").equals("hi")) {
                        //markerOptions.snippet(listData.get(i).getPath() + "#" + listData.get(i).getRq_code() + "#" + listData.get(i).getTitle() + "#" + listData.get(i).getDescription() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getS_time() + "#" + listData.get(i).getE_time() + "#" + listData.get(i).getS_date() + "#" + listData.get(i).getE_date() + "#" + listData.get(i).getLikecnt() + "#" + listData.get(i).getDislikecnt() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getDescribe_limitations() + "#" + listData.get(i).getMain_cat_name_hindi() + "#" + listData.get(i).getSubcategory_name_hindi() + "#" + listData.get(i).getBusiness_email());
                        Marker myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(listData.get(i).getId()).snippet(listData.get(i).getPath() + "#" + listData.get(i).getReferral_code() + "#" + listData.get(i).getBusiness_name() + "#" + listData.get(i).getAddress() + "#" + listData.get(i).getBusiness_number() + "#" + listData.get(i).getSponsor_flag() + "#" + listData.get(i).getBusiness_email() + "#" + listData.get(i).getpBidavgrating() + "#" + listData.get(i).getTorcount() + "#" + listData.get(i).getMain_cat_name_hindi() + "#" + listData.get(i).getSubcategory_name_hindi() + "#" + listData.get(i).getStatus() + "#" + listData.get(i).getPrice_status()).icon(BitmapDescriptorFactory.fromBitmap(Utility.createDrawableFromView(context, marker))));

                    }
                }
                // Marker myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title(listData.get(i).getId()).icon(BitmapDescriptorFactory.fromBitmap(Utility.createDrawableFromView(context, marker))));

                //googleMap.addMarker(markerOptions);

            }
            prefEditor.putInt("cntloop",list.size()+preferences.getInt("cntloop",0));
            prefEditor.commit();
            if (list.size() > 0) {
                mListener.messageReceived("" + 1);
            } else {
                mListener.messageReceived("" + 0);
            }
          /*  prefEditor.putString("t_markers", "" + list.size());
            prefEditor.commit();
            mListener1.messageReceived("" + list.size());*/
            prefEditor.putString("t_markers", "" + preferences.getInt("cntloop",0));
            prefEditor.commit();
            mListener1.messageReceived("" + preferences.getInt("cntloop",0));
        } catch (Exception e) {
            mListener.messageReceived("" + 0);
        }
    }

    public static void bindListener(Listener listener) {
        mListener = listener;
    }

    public static void bindListener1(Listener listener) {
        mListener1 = listener;
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
}

