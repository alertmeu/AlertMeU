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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.activity.ViewIncDescriptionActivity;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.models.LocationDAO;

public class IncidentListAdpterPage extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private Context context;
    private List<IncidentDAO> mPostItems;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    public IncidentListAdpterPage(Context context, List<IncidentDAO> postItems) {
        this.context = context;
        this.mPostItems = postItems;
        res = context.getResources();
        loadLanguage(context);
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_incident_details, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mPostItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mPostItems == null ? 0 : mPostItems.size();
    }

    public void addItems(List<IncidentDAO> postItems) {
        mPostItems.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new IncidentDAO());
        notifyItemInserted(mPostItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        IncidentDAO item = getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }

    IncidentDAO getItem(int position) {
        return mPostItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.datetime)
        TextView datetime;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.layout_quotation)
        LinearLayout layout_quotation;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true); // this will also round numbers, 3
            IncidentDAO current = mPostItems.get(position);
            if (preferences.getString("ulang", "").equals("en")) {
                title.setText(current.getBusiness_main_category());
            } else if (preferences.getString("ulang", "").equals("hi")) {
                title.setText(current.getMain_cat_name_hindi());
            }
            description.setText(current.getBusiness_name());
            datetime.setText(formateDateFromstring("yyyy-MM-dd HH:mm", "dd-MMM-yyyy hh:mm aa", current.getBusiness_email()));
            datetime.setTag(position);


            // ImageLoader imageLoader = new ImageLoader(context);
            //  imageLoader.DisplayImage(current.getOriginal_image_path(), myHolder.imageView);

            if (current.getPath().contains("http")) {
                try {
                    Picasso.get().load(current.getPath()).into(imageView);
                } catch (Exception e) {

                }
            } else {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultbusimg));
            }
            layout_quotation.setTag(position);

            layout_quotation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefEditor.putString("incback", "0");
                    prefEditor.commit();
                    ((Activity) context).finish();
                    Intent intent = new Intent(context, ViewIncDescriptionActivity.class);
                    intent.putExtra("id", current.getId());
                    intent.putExtra("referral_code", current.getReferral_code());
                    intent.putExtra("lat", "" + current.getLatitude());
                    intent.putExtra("long", "" + current.getLongitude());
                    intent.putExtra("imagePath", current.getPath());
                    if (preferences.getString("ulang", "").equals("en")) {
                        intent.putExtra("title", current.getBusiness_main_category());
                    } else if (preferences.getString("ulang", "").equals("hi")) {
                        intent.putExtra("title", current.getMain_cat_name_hindi());
                    }
                    intent.putExtra("sponsor_flag", current.getSponsor_flag());
                    intent.putExtra("business", current.getBusiness_name() + "\n" + current.getAddress());
                    intent.putExtra("mobile_no", current.getBusiness_name());
                    intent.putExtra("email", current.getBusiness_email());
                    intent.putExtra("maincat", current.getBusiness_email());
                    intent.putExtra("subcat", current.getBusiness_subcategory());
                    context.startActivity(intent);
                }
            });

        }
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
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
