package in.alertmeu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import java.util.List;
import java.util.Locale;


import in.alertmeu.R;
import in.alertmeu.activity.FullScreenViewActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.models.AdvertisementDAO;


public class ActiveAdvertisementListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<AdvertisementAllBusDAO> data;
    AdvertisementAllBusDAO current;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";


    // create constructor to innitilize context and data sent from MainActivity
    public ActiveAdvertisementListAdpter(Context context, List<AdvertisementAllBusDAO> data) {
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
        View view = inflater.inflate(R.layout.layout_active_advertisement_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);
        if (preferences.getString("ulang", "").equals("en")) {
            myHolder.mainCat.setText(res.getString(R.string.jadcat) + current.getMain_cat_name());
            myHolder.mainCat.setTag(position);
            myHolder.subCat.setText(res.getString(R.string.jadscat) + current.getSubcategory_name());
            myHolder.subCat.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.mainCat.setText(res.getString(R.string.jadcat) + current.getMain_cat_name_hindi());
            myHolder.mainCat.setTag(position);
            myHolder.subCat.setText(res.getString(R.string.jadscat) + current.getSubcategory_name_hindi());
            myHolder.subCat.setTag(position);
        }
        try {

            myHolder.imageView.setVisibility(View.VISIBLE);
            myHolder.imageView.setTag(position);
            Picasso.get().load(current.getOriginal_image_path()).into(myHolder.imageView);
        } catch (Exception e) {

        }

        myHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                ((Activity) context).finish();
                Intent intent = new Intent(context, ViewImageDescriptionActivity.class);
                intent.putExtra("id", current.getId());
                intent.putExtra("bid", current.getBusiness_user_id());
                intent.putExtra("mcatid", current.getBusiness_main_category_id());
                intent.putExtra("qrCode", current.getRq_code());
                intent.putExtra("lat", "" + current.getLatitude());
                intent.putExtra("long", "" + current.getLongitude());
                intent.putExtra("imagePath", current.getOriginal_image_path());
                intent.putExtra("imageCompanyPath", current.getCompany_logo());
                intent.putExtra("bname", current.getBusiness_name());
                intent.putExtra("title", current.getTitle());
                intent.putExtra("description", current.getDescription());
                intent.putExtra("business", current.getBusiness_name() + "\n" + current.getAddress());
                intent.putExtra("stime", "" + current.getS_time());
                intent.putExtra("etime", "" + current.getE_time());
                intent.putExtra("sdate", current.getS_date());
                intent.putExtra("edate", current.getE_date());
                intent.putExtra("likecnt", current.getLikecnt());
                intent.putExtra("dislikecnt", current.getDislikecnt());
                intent.putExtra("mobile_no", current.getBusiness_number());
                intent.putExtra("email", current.getBusiness_email());
                intent.putExtra("price_status", current.getPrice_status());
                intent.putExtra("describe_limitations", current.getDescribe_limitations());
                if (preferences.getString("ulang", "").equals("en")) {
                    intent.putExtra("main_cat_name", current.getMain_cat_name());
                    intent.putExtra("subcategory_name", current.getSubcategory_name());
                    intent.putExtra("maincat", current.getBus_main_cat_name());
                    intent.putExtra("subcat", current.getBus_subcategory_name());
                } else if (preferences.getString("ulang", "").equals("hi")) {
                    intent.putExtra("main_cat_name", current.getMain_cat_name_hindi());
                    intent.putExtra("subcategory_name", current.getSubcategory_name_hindi());
                    intent.putExtra("maincat", current.getBus_main_cat_name_hindi());
                    intent.putExtra("subcat", current.getBus_subcategory_name_hindi());
                }
                context.startActivity(intent);
            }
        });

        myHolder.deshideshow.setTag(position);
        if (!current.getDescription().equals("")) {
            myHolder.deshideshow.setVisibility(View.VISIBLE);
            myHolder.dis.setText(current.getDescription());

        } else {
            myHolder.deshideshow.setVisibility(View.GONE);
        }
    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView mainCat, subCat, dis;
        ImageView imageView;
        CountDownTimer timer;
        LinearLayout deshideshow;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            mainCat = itemView.findViewById(R.id.mainCat);
            subCat = itemView.findViewById(R.id.subCat);
            deshideshow = (LinearLayout) itemView.findViewById(R.id.deshideshow);
            dis = (TextView) itemView.findViewById(R.id.dis);

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
}
