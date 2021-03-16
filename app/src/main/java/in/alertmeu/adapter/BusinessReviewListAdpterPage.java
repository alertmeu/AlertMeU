package in.alertmeu.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.activity.RegisterNGetStartActivity;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.models.ShowAllReviewsDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class BusinessReviewListAdpterPage extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private Context context;
    private List<ShowAllReviewsDAO> mPostItems;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    JSONObject jsonLeadObj;
    boolean status;
    String localTime;
    String like_status, updateStatusResponse = "", id;

    public BusinessReviewListAdpterPage(Context context, List<ShowAllReviewsDAO> postItems) {
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
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_allreviews_details, parent, false));
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

    public void addItems(List<ShowAllReviewsDAO> postItems) {
        mPostItems.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new ShowAllReviewsDAO());
        notifyItemInserted(mPostItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        ShowAllReviewsDAO item = getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }

    ShowAllReviewsDAO getItem(int position) {
        return mPostItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.textViewDate)
        TextView textViewDate;
        @BindView(R.id.textComment)
        TextView textComment;
        @BindView(R.id.likecnt)
        TextView likecnt;
        @BindView(R.id.dislikecnt)
        TextView dislikecnt;
        @BindView(R.id.avgratingStar)
        RatingBar avgratingStar;
        @BindView(R.id.imageViewIcon)
        ImageView imageViewIcon;
        @BindView(R.id.like)
        ImageView like;
        @BindView(R.id.dislike)
        ImageView dislike;
        @BindView(R.id.liked)
        ImageView liked;
        @BindView(R.id.disliked)
        ImageView disliked;

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
            ShowAllReviewsDAO current = mPostItems.get(position);
            textViewName.setText(current.getFirst_name());
            try {
                avgratingStar.setRating(Float.parseFloat(current.getRating_star()));
            } catch (Exception e) {
            }
            textViewDate.setText(formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm:ss", current.getTime_stamp()));

            if (!current.getReview_status().contains("-1")) {
                if (current.getReview_status().contains("0")) {
                    liked.setVisibility(View.GONE);
                    dislike.setVisibility(View.GONE);
                    like.setVisibility(View.VISIBLE);
                    disliked.setVisibility(View.VISIBLE);
                } else {
                    disliked.setVisibility(View.GONE);
                    like.setVisibility(View.GONE);
                    dislike.setVisibility(View.VISIBLE);
                    liked.setVisibility(View.VISIBLE);
                }
            } else {
                liked.setVisibility(View.GONE);
                disliked.setVisibility(View.GONE);
                dislike.setVisibility(View.VISIBLE);
                like.setVisibility(View.VISIBLE);

            }

            if (Integer.parseInt(current.getRlcnt()) > 0) {
                likecnt.setVisibility(View.VISIBLE);
                likecnt.setText(current.getRlcnt());
            } else {
                likecnt.setVisibility(View.GONE);
            }
            if (Integer.parseInt(current.getRdcnt()) > 0) {
                dislikecnt.setVisibility(View.VISIBLE);
                dislikecnt.setText(current.getRdcnt());
            } else {
                dislikecnt.setVisibility(View.GONE);
            }
            if (!current.getUser_review().equals("")) {
                textComment.setVisibility(View.VISIBLE);
                textComment.setText(current.getUser_review());

            } else {
                textComment.setVisibility(View.GONE);

            }

            if (current.getProfilePath().contains("http")) {
                try {
                    Picasso.get().load(current.getProfilePath()).into(imageViewIcon);
                } catch (Exception e) {

                }
            } else {
                imageViewIcon.setImageResource(R.drawable.defuserpro);
            }
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (preferences.getString("account_status", "").equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(res.getString(R.string.jpr))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.logreg)
                                        , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Intent intent = new Intent(context, RegisterNGetStartActivity.class);
                                                context.startActivity(intent);
                                            }
                                        })
                                .setNegativeButton(res.getString(R.string.helpCan)
                                        , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                            }
                                        });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle(res.getString(R.string.jur));
                        alert.show();
                    } else {

                        if (AppStatus.getInstance(context).isOnline()) {
                            like_status = "1";
                            if (like_status.equals("1")) {
                                disliked.setVisibility(View.GONE);
                                like.setVisibility(View.GONE);
                                dislike.setVisibility(View.VISIBLE);
                                liked.setVisibility(View.VISIBLE);
                            }

                            if (current.getReview_status().contains("-1")) {
                                if (Integer.parseInt(current.getRlcnt()) > 0) {
                                    dislikecnt.setText("");
                                    likecnt.setText("" + (Integer.parseInt(current.getRlcnt()) + 1));
                                    current.setRlcnt("" + (Integer.parseInt(current.getRlcnt()) + 1));
                                } else {
                                    current.setRlcnt("" + 1);
                                    dislikecnt.setText("");
                                    likecnt.setText("1");

                                }

                            } else {
                                if (Integer.parseInt(current.getRlcnt()) > 0) {
                                    likecnt.setText("" + (Integer.parseInt(current.getRlcnt()) + 1));
                                    current.setRlcnt("" + (Integer.parseInt(current.getRlcnt()) + 1));
                                } else {
                                    current.setRlcnt("1");
                                    likecnt.setText("" + 1);
                                }
                                if (Integer.parseInt(current.getRdcnt()) > 0) {
                                    dislikecnt.setText("" + (Integer.parseInt(current.getRdcnt()) - 1));
                                    current.setRdcnt("" + (Integer.parseInt(current.getRdcnt()) - 1));
                                } else {
                                    dislikecnt.setText("0");
                                    current.setRdcnt("");
                                }

                            }
                            current.setReview_status("1");
                            id = current.getId();
                            new initStatusUpdate().execute();
                        } else {
                            Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (preferences.getString("account_status", "").equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(res.getString(R.string.jpr))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.logreg)
                                        , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Intent intent = new Intent(context, RegisterNGetStartActivity.class);
                                                context.startActivity(intent);
                                            }
                                        })
                                .setNegativeButton(res.getString(R.string.helpCan)
                                        , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                            }
                                        });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle(res.getString(R.string.jur));
                        alert.show();
                    } else {

                        if (AppStatus.getInstance(context).isOnline()) {
                            like_status = "0";
                            if (like_status.equals("0")) {
                                liked.setVisibility(View.GONE);
                                dislike.setVisibility(View.GONE);
                                like.setVisibility(View.VISIBLE);
                                disliked.setVisibility(View.VISIBLE);

                            }

                            if (current.getReview_status().contains("-1")) {
                                if (Integer.parseInt(current.getRdcnt()) > 0) {
                                    likecnt.setText("");
                                    dislikecnt.setText("" + (Integer.parseInt(current.getRdcnt()) + 1));
                                    current.setRdcnt("" + (Integer.parseInt(current.getRdcnt()) + 1));
                                } else {
                                    likecnt.setText("");
                                    dislikecnt.setText("1");
                                    current.setRdcnt("" + 1);
                                }
                            } else {
                                if (Integer.parseInt(current.getRlcnt()) > 0) {
                                    likecnt.setText("" + (Integer.parseInt(current.getRlcnt()) - 1));
                                    current.setRlcnt("" + (Integer.parseInt(current.getRlcnt()) - 1));
                                } else {
                                    current.setRlcnt("0");
                                    likecnt.setText("");
                                }
                                if (Integer.parseInt(current.getRdcnt()) > 0) {
                                    dislikecnt.setText("" + (Integer.parseInt(current.getRdcnt()) + 1));
                                    current.setRdcnt("" + (Integer.parseInt(current.getRdcnt()) + 1));
                                } else {
                                    dislikecnt.setText("1");
                                    current.setRdcnt("" + 1);
                                }

                            }
                            current.setReview_status("0");
                            id = current.getId();
                            new initStatusUpdate().execute();
                        } else {
                            Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                        }
                    }
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

    private class initStatusUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                    Locale.getDefault());
            Date currentLocalTime = calendar.getTime();

            DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
            localTime = date.format(currentLocalTime);
            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("review_id", id);
                        put("like_dislike_status", like_status);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERREVIEWLIKES, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    try {
                        JSONObject jsonObject = new JSONObject(updateStatusResponse);
                        status = jsonObject.getBoolean("status");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {

                    return null;
                }
            } else {


                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {


        }
    }

    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
