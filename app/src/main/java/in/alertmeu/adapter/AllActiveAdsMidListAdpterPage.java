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
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.models.LocationDAO;

public class AllActiveAdsMidListAdpterPage extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private Context context;
    private List<AdvertisementAllBusDAO> mPostItems;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    public AllActiveAdsMidListAdpterPage(Context context, List<AdvertisementAllBusDAO> postItems) {
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
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_active_advertisement_details, parent, false));
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

    public void addItems(List<AdvertisementAllBusDAO> postItems) {
        mPostItems.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new AdvertisementAllBusDAO());
        notifyItemInserted(mPostItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        AdvertisementAllBusDAO item = getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }

    AdvertisementAllBusDAO getItem(int position) {
        return mPostItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.mainCat)
        TextView mainCat;
        @BindView(R.id.subCat)
        TextView subCat;
        @BindView(R.id.dis)
        TextView dis;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.deshideshow)
        LinearLayout deshideshow;

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
            AdvertisementAllBusDAO current = mPostItems.get(position);
            if (preferences.getString("ulang", "").equals("en")) {
                mainCat.setText(res.getString(R.string.jadcat) + current.getMain_cat_name());

                subCat.setText(res.getString(R.string.jadscat) + current.getSubcategory_name());

            } else if (preferences.getString("ulang", "").equals("hi")) {
                mainCat.setText(res.getString(R.string.jadcat) + current.getMain_cat_name_hindi());

                subCat.setText(res.getString(R.string.jadscat) + current.getSubcategory_name_hindi());

            }
            try {

                imageView.setVisibility(View.VISIBLE);

                Picasso.get().load(current.getOriginal_image_path()).into(imageView);
            } catch (Exception e) {

            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

            deshideshow.setTag(position);
            if (!current.getDescription().equals("")) {
                deshideshow.setVisibility(View.VISIBLE);
                dis.setText(current.getDescription());

            } else {
                deshideshow.setVisibility(View.GONE);
            }

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
}
