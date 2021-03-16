package in.alertmeu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.R;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.models.ExAllMainMenuPriceModeDAO;
import in.alertmeu.models.ExMainSubMenuDAO;


public class SubmenuListAdapter extends RecyclerView.Adapter<SubmenuListAdapter.ViewHolder> {

    private Context context;
    private List<ExAllMainMenuPriceModeDAO> advertisements = new ArrayList<ExAllMainMenuPriceModeDAO>();
    ExAllMainMenuPriceModeDAO current;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    public SubmenuListAdapter(Context context, List<ExAllMainMenuPriceModeDAO> advertisements) {
        this.context = context;
        this.advertisements = advertisements;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(R.layout.item_menuchild, null, false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        viewHolder.mobileImage = (ImageView) cardView.findViewById(R.id.subimage);
        viewHolder.title = (TextView) cardView.findViewById(R.id.title);
        viewHolder.desc = (TextView) cardView.findViewById(R.id.desc);
        viewHolder.price = (TextView) cardView.findViewById(R.id.rate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ViewHolder myHolder = (ViewHolder) holder;
        current = advertisements.get(position);
        ImageView mobileImageView = (ImageView) holder.mobileImage;
        try {
            Picasso.get().load(advertisements.get(position).getImage_path()).into(mobileImageView);
            mobileImageView.setTag(position);
        } catch (Exception e) {

        }
        TextView modelTextView = (TextView) holder.title;
        modelTextView.setText(advertisements.get(position).getTitle());

        TextView desc = (TextView) holder.desc;
        desc.setText(advertisements.get(position).getDescription());
        if (preferences.getString("country_code", "").equals("+91")) {
            TextView price = (TextView) holder.price;
            price.setText("Rs " +advertisements.get(position).getRate());
        }
        else
        {
            TextView price = (TextView) holder.price;
            price.setText("$" +advertisements.get(position).getRate());
        }
       /* LinearLayout clicki = (LinearLayout) holder.clickitem;
        clicki.setTag(position);
        clicki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = advertisements.get(ID);
                Intent intent = new Intent(context, ViewImageDescriptionActivity.class);
               *//* intent.putExtra("id", current.getId());
                intent.putExtra("bid", current.getBusiness_user_id());
                intent.putExtra("mcatid", current.getBusiness_main_category_id());*//*
                context.startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mobileImage;
        TextView title, desc;
        TextView price;


        public ViewHolder(View itemView) {
            super(itemView);
            mobileImage = (ImageView) itemView.findViewById(R.id.subimage);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            price = (TextView) itemView.findViewById(R.id.rate);
        }
    }

}
