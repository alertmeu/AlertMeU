package in.alertmeu.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.models.IncidentSubTypeModeDAO;


public class SubIncidentTypeListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<IncidentSubTypeModeDAO> data;
    IncidentSubTypeModeDAO current;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    // create constructor to innitilize context and data sent from MainActivity
    public SubIncidentTypeListAdpter(Context context, List<IncidentSubTypeModeDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        res = context.getResources();
        loadLanguage(context);

    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_subinctype_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);

        if (preferences.getString("ulang", "").equals("en")) {
            myHolder.notes.setText(current.getIncident_name());
            myHolder.notes.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.notes.setText(current.getIncident_name_hindi());
            myHolder.notes.setTag(position);
        }

        if (!current.getImage_path().equals("")) {
            // ImageLoader imageLoader = new ImageLoader(context);
            //  imageLoader.DisplayImage(current.getImage_path(), myHolder.subimage);
            myHolder.subimage.setTag(position);
            //   Picasso.with(context).load(current.getImage_path()).noPlaceholder().into((ImageView) myHolder.subimage);
            try {
                Picasso.get().load(current.getImage_path()).into(myHolder.subimage);
            } catch (Exception e) {

            }
        } else {

            myHolder.subimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_sub_category));
            myHolder.subimage.setTag(position);

        }
        myHolder.id.setText(current.getId());
        myHolder.id.setTag(position);
        myHolder.chkBox.setTag(position);
        myHolder.chkBox.setChecked(data.get(position).isSelected());
        myHolder.chkBox.setTag(data.get(position));
        if (current.getChecked_status().equals("1")) {
            myHolder.chkBox.setChecked(true);
            current.setSelected(true);
        } else {
            myHolder.chkBox.setChecked(false);
            current.setSelected(false);
        }
        myHolder.chkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                IncidentSubTypeModeDAO contact = (IncidentSubTypeModeDAO) cb.getTag();
                contact.setSelected(cb.isChecked());
                data.get(pos).setSelected(cb.isChecked());


            }
        });
    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItems(List<IncidentSubTypeModeDAO> postItems) {
        data.addAll(postItems);
        notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView txt_date, notes, id;
        CheckBox chkBox;
        ImageView subimage;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            notes = (TextView) itemView.findViewById(R.id.comments);
            notes = (TextView) itemView.findViewById(R.id.comments);
            id = (TextView) itemView.findViewById(R.id.id);
            chkBox = (CheckBox) itemView.findViewById(R.id.chkBox);
            subimage = (ImageView) itemView.findViewById(R.id.subimage);

        }

    }

    // method to access in activity after updating selection
    public List<IncidentSubTypeModeDAO> getSservicelist() {
        return data;
    }

    private String getLangCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "");
        return langCode;
    }

    private void loadLanguage(Context context) {
        Locale locale = new Locale(getLangCode(context));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
