package in.alertmeu.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import org.json.JSONObject;

import java.util.List;

import in.alertmeu.R;
import in.alertmeu.models.IncidentTypeModeDAO;
import in.alertmeu.view.IncidentSubTypeView;


public class IncidentTypeListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<IncidentTypeModeDAO> data;
    IncidentTypeModeDAO current;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    // create constructor to innitilize context and data sent from MainActivity
    public IncidentTypeListAdpter(Context context, List<IncidentTypeModeDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_maincat_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);

        myHolder.notes.setTag(position);
        if (preferences.getString("ulang", "").equals("en")) {
            if (current.getChecked_status().equals("0")) {
                myHolder.notes.setText(current.getIncident_name().trim());
                myHolder.notes.setTextColor(Color.parseColor("#000000"));

            } else {
                myHolder.notes.setText(current.getIncident_name().trim() + "(" + current.getChecked_status() + ")");
                myHolder.notes.setTextColor(Color.parseColor("#23A566"));
            }
        } else if (preferences.getString("ulang", "").equals("hi")) {
            if (current.getChecked_status().equals("0")) {
                myHolder.notes.setText(current.getIncident_name_hindi().trim());
                myHolder.notes.setTextColor(Color.parseColor("#000000"));

            } else {
                myHolder.notes.setText(current.getIncident_name_hindi().trim() + "(" + current.getChecked_status() + ")");
                myHolder.notes.setTextColor(Color.parseColor("#23A566"));
            }
        }
        myHolder.id.setText(current.getId());
        myHolder.id.setTag(position);
        myHolder.layout_quotation.setTag(position);
        if (!current.getImage_path().equals("")) {
            // ImageLoader imageLoader = new ImageLoader(context);
            //  imageLoader.DisplayImage(current.getImage_path(), myHolder.mimage);
            //  myHolder.mimage.setTag(position);
            //  Picasso.with(context).load(current.getImage_path()).noPlaceholder().into((ImageView) myHolder.mimage);
            try {
                Picasso.get().load(current.getImage_path()).into(myHolder.mimage);
            } catch (Exception e) {

            }
          /*  ImageloaderNew imageLoader = new ImageloaderNew(context);
            myHolder.mimage.setTag(current.getImage_path());
            imageLoader.DisplayImage(current.getImage_path(), context,myHolder.mimage);*/
        } else {
            myHolder.mimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_category));
            myHolder.mimage.setTag(position);


        }

        myHolder.layout_quotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                if (preferences.getString("ulang", "").equals("en")) {
                    prefEditor.putString("inc_name", current.getIncident_name());
                } else if (preferences.getString("ulang", "").equals("hi")) {
                    prefEditor.putString("inc_name", current.getIncident_name_hindi());
                }
                prefEditor.putString("inc_id", current.getId());
                prefEditor.commit();
                // Toast.makeText(context, "Clicked on Checkbox: " + current.getId(), Toast.LENGTH_LONG).show();
                IncidentSubTypeView incidentSubTypeView = new IncidentSubTypeView();
                incidentSubTypeView.show(((AppCompatActivity) context).getSupportFragmentManager(), "incidentSubTypeView");

            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView txt_date, notes, id;
        CheckBox chkBox;
        ImageView mimage;
        LinearLayout layout_quotation;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            notes = (TextView) itemView.findViewById(R.id.comments);
            notes = (TextView) itemView.findViewById(R.id.comments);
            id = (TextView) itemView.findViewById(R.id.id);
            chkBox = (CheckBox) itemView.findViewById(R.id.chkBox);
            mimage = (ImageView) itemView.findViewById(R.id.mimage);
            layout_quotation = (LinearLayout) itemView.findViewById(R.id.layout_quotation);

        }

    }

}
