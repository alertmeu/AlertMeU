package in.alertmeu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.alertmeu.R;
import in.alertmeu.activity.ViewIncDescriptionActivity;
import in.alertmeu.activity.ViewMoreInfoActivity;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.models.MoreIncInfoModel;
import in.alertmeu.utils.Listener;

public class IncidentMoreInfoListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<MoreIncInfoModel> data;
    MoreIncInfoModel current;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String user_id = "";
    private static Listener mListener;
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    List<MoreIncInfoModel> itemsPendingRemoval = new ArrayList<>();

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<MoreIncInfoModel, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    // create constructor to innitilize context and data sent from MainActivity
    public IncidentMoreInfoListAdpter(Context context, List<MoreIncInfoModel> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_incident_moreinfo_details, parent, false);
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

        myHolder.title.setText(current.getComments());
        myHolder.title.setTag(position);
        myHolder.datetime.setText(formateDateFromstring("yyyy-MM-dd HH:mm", "dd-MMM-yyyy hh:mm aa", current.getDatetime()));
        myHolder.datetime.setTag(position);
        // ImageLoader imageLoader = new ImageLoader(context);
        //  imageLoader.DisplayImage(current.getOriginal_image_path(), myHolder.imageView);
        // Picasso.get().load(current.getImage_path()).into(myHolder.imageView);
        myHolder.imageView.setTag(position);
        if (current.getImage_path().contains("http")) {
            try {
                Picasso.get().load(current.getImage_path()).into(myHolder.imageView);
            } catch (Exception e) {

            }
        } else {
            myHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_warning));
        }
        myHolder.layout_quotation.setTag(position);
        myHolder.layout_quotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                Intent intent = new Intent(context, ViewMoreInfoActivity.class);
                intent.putExtra("desc", current.getComments());
                intent.putExtra("imagePath", current.getImage_path());
                intent.putExtra("reportd", formateDateFromstring("yyyy-MM-dd HH:mm", "dd-MMM-yyyy hh:mm aa", current.getDatetime()));
                context.startActivity(intent);
            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        current = data.get(position);
        if (!itemsPendingRemoval.contains(current)) {
            itemsPendingRemoval.add(current);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {

                    remove(data.indexOf(current));

                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(current, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        current = data.get(position);

        user_id = current.getUser_id();
        ID = position;
        // Toast.makeText(context, "Remove id" + id, Toast.LENGTH_LONG).show();

        if (itemsPendingRemoval.contains(current)) {
            itemsPendingRemoval.remove(current);
        }
        if (data.contains(current)) {
            data.remove(position);
            notifyItemRemoved(position);
        }
        // new deleteSale().execute();
    }

    public boolean isPendingRemoval(int position) {
        current = data.get(position);
        return itemsPendingRemoval.contains(current);
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView title, description, time, datetime;

        ImageView imageView;

        LinearLayout layout_quotation;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            time = (TextView) itemView.findViewById(R.id.time);
            datetime = (TextView) itemView.findViewById(R.id.datetime);
            layout_quotation = (LinearLayout) itemView.findViewById(R.id.layout_quotation);

        }

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
