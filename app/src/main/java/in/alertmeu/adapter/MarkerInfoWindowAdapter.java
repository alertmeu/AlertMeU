package in.alertmeu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public MarkerInfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.map_marker_info_window, null);

        LatLng latLng = arg0.getPosition();
        String a[] = arg0.getSnippet().split("#");
      /*  TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
        tvLat.setText("business:" + a[2]);
        tvLng.setText("Address:" + a[3]);
        LinearLayout clickLy = (LinearLayout) v.findViewById(R.id.clickLy);*/

        return v;
    }

}
