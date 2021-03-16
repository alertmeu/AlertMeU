package in.alertmeu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.models.ShowAllReviewsDAO;


public class OtherBusinessListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private Context context;
    private LayoutInflater inflater;
    List<LocationDAO> data;
    LocationDAO current;
    int number = 1, clickflag = 1;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    // create constructor to innitilize context and data sent from MainActivity
    public OtherBusinessListAdpter(Context context, List<LocationDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        res = context.getResources();
        loadLanguage(context);
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();


    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.map_marker_info_window, parent, false);
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
        myHolder.textViewName.setText(current.getBusiness_name());
        myHolder.textViewName.setTag(position);
        myHolder.cardView.setTag(position);

        if(!current.getpBidavgrating().equals("null")) {
            myHolder.avgratingStar.setRating(Float.parseFloat(current.getpBidavgrating()));
            myHolder.avgratingStar.setTag(position);
        }

        myHolder.textViewTotal.setTag(position);
        if (current.getTorcount().equals("1")) {
            myHolder.textViewTotal.setVisibility(View.VISIBLE);
            myHolder.textViewTotal.setText(current.getTorcount() + " " + res.getString(R.string.xwars));
        } else if (current.getTorcount().equals("0")) {
            myHolder.textViewTotal.setVisibility(View.GONE);
        } else {
            myHolder.textViewTotal.setVisibility(View.VISIBLE);
            myHolder.textViewTotal.setText(current.getTorcount() + " " + res.getString(R.string.xers));
        }
       /* if (!current.getPath().equals("")) {
            try {
                Picasso.get().load(current.getPath()).into(myHolder.imageViewIcon);
                myHolder.imageViewIcon.setTag(position);
            } catch (Exception e) {

            }
        }*/
        if (current.getPath().contains("http")) {
            try {
                Picasso.get().load(current.getPath()).into(myHolder.imageViewIcon);
            } catch (Exception e) {

            }
        } else {
            myHolder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultbusimg));
        }
        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                ((Activity)context).finish();
                Intent intent = new Intent(context, BusinessProfileActivity.class);
                intent.putExtra("id", current.getId());
                intent.putExtra("referral_code", current.getReferral_code());
                intent.putExtra("lat", "" + "" + current.getLatitude());
                intent.putExtra("long", "" + current.getLongitude());
                intent.putExtra("imagePath", current.getPath());
                intent.putExtra("title", current.getBusiness_name());
                intent.putExtra("sponsor_flag", current.getSponsor_flag());
                intent.putExtra("business", current.getBusiness_name() + "\n" + current.getAddress());
                intent.putExtra("mobile_no", current.getMobile_no());
                intent.putExtra("email", current.getBusiness_email());
                intent.putExtra("price_status", current.getPrice_status());
                if (preferences.getString("ulang", "").equals("en")) {
                    intent.putExtra("maincat", current.getBusiness_main_category());
                    intent.putExtra("subcat", current.getBusiness_subcategory());
                }
                else if (preferences.getString("ulang", "").equals("hi")) {
                    intent.putExtra("maincat", current.getMain_cat_name_hindi());
                    intent.putExtra("subcat", current.getSubcategory_name_hindi());
                }
                context.startActivity(intent);
            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewTotal;
        RatingBar avgratingStar;
        ImageView imageViewIcon;
        CardView cardView;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewTotal = (TextView) itemView.findViewById(R.id.textViewTotal);
            avgratingStar = (RatingBar) itemView.findViewById(R.id.avgratingStar);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            cardView= (CardView) itemView.findViewById(R.id.cardView);
        }

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

}
