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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.models.LocationDAO;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherBusinessListAdpterPage extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private Context context;
    private List<LocationDAO> mPostItems;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    public OtherBusinessListAdpterPage(Context context, List<LocationDAO> postItems) {
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
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.map_marker_info_window, parent, false));
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

    public void addItems(List<LocationDAO> postItems) {
        mPostItems.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new LocationDAO());
        notifyItemInserted(mPostItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        LocationDAO item = getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }

    LocationDAO getItem(int position) {
        return mPostItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.textViewTotal)
        TextView textViewTotal;
        @BindView(R.id.avgratingStar)
        RatingBar avgratingStar;
        @BindView(R.id.imageViewIcon)
        ImageView imageViewIcon;
        @BindView(R.id.cardView)
        CardView cardView;

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
            LocationDAO current = mPostItems.get(position);
            textViewName.setText(current.getBusiness_name());
            avgratingStar.setTag(position);
            try {
                if (!current.getpBidavgrating().equals("null")) {
                    avgratingStar.setRating(Float.parseFloat(current.getpBidavgrating()));

                } else {
                    avgratingStar.setRating(0);
                }
            } catch (Exception e) {

            }
            if (current.getTorcount().equals("1")) {
                textViewTotal.setVisibility(View.VISIBLE);
                textViewTotal.setText(current.getTorcount() + " " + res.getString(R.string.xwars));
            } else if (current.getTorcount().equals("0")) {
                textViewTotal.setVisibility(View.GONE);
            } else {
                textViewTotal.setVisibility(View.VISIBLE);
                textViewTotal.setText(current.getTorcount() + " " + res.getString(R.string.xers));
            }

            if (current.getPath().contains("http")) {
                try {
                    Picasso.get().load(current.getPath()).into(imageViewIcon);
                } catch (Exception e) {

                }
            } else {
                imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultbusimg));
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   ((Activity) context).finish();
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
                    } else if (preferences.getString("ulang", "").equals("hi")) {
                        intent.putExtra("maincat", current.getMain_cat_name_hindi());
                        intent.putExtra("subcat", current.getSubcategory_name_hindi());
                    }
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
}
