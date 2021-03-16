package in.alertmeu.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.R;
import in.alertmeu.activity.BusinessProfileActivity;
import in.alertmeu.activity.RegisterNGetStartActivity;
import in.alertmeu.activity.ViewImageDescriptionActivity;
import in.alertmeu.models.ShowAllReviewsDAO;
import in.alertmeu.models.YouTubeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;


public class BusinessReviewListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private Context context;
    private LayoutInflater inflater;
    List<ShowAllReviewsDAO> data;
    ShowAllReviewsDAO current;
    int number = 1, clickflag = 1;
    String like_status, updateStatusResponse = "", id;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    JSONObject jsonLeadObj;
    boolean status;
    String localTime;

    // create constructor to innitilize context and data sent from MainActivity
    public BusinessReviewListAdpter(Context context, List<ShowAllReviewsDAO> data) {
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
        View view = inflater.inflate(R.layout.layout_allreviews_details, parent, false);
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
        myHolder.textViewName.setText(current.getFirst_name());
        myHolder.textViewName.setTag(position);

        myHolder.avgratingStar.setRating(Float.parseFloat(current.getRating_star()));
        myHolder.avgratingStar.setTag(position);

        myHolder.textViewDate.setText(formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm:ss", current.getTime_stamp()));
        myHolder.textViewDate.setTag(position);
        myHolder.like.setTag(position);
        myHolder.dislike.setTag(position);
        myHolder.liked.setTag(position);
        myHolder.disliked.setTag(position);
        if (!current.getReview_status().contains("-1")) {
            if (current.getReview_status().contains("0")) {
                myHolder.liked.setVisibility(View.GONE);
                myHolder.dislike.setVisibility(View.GONE);
                myHolder.like.setVisibility(View.VISIBLE);
                myHolder.disliked.setVisibility(View.VISIBLE);
            } else {
                myHolder.disliked.setVisibility(View.GONE);
                myHolder.like.setVisibility(View.GONE);
                myHolder.dislike.setVisibility(View.VISIBLE);
                myHolder.liked.setVisibility(View.VISIBLE);
            }
        }
        myHolder.likecnt.setTag(position);
        myHolder.dislikecnt.setTag(position);
        if (Integer.parseInt(current.getRlcnt()) > 0) {
            myHolder.likecnt.setText(current.getRlcnt());
        }
        if (Integer.parseInt(current.getRdcnt()) > 0) {
            myHolder.dislikecnt.setText(current.getRdcnt());
        }
        if (!current.getUser_review().equals("")) {
            myHolder.textComment.setVisibility(View.VISIBLE);
            myHolder.textComment.setText(current.getUser_review());
            myHolder.textComment.setTag(position);
        } else {
            myHolder.textComment.setVisibility(View.GONE);
            myHolder.textComment.setTag(position);
        }
        /*if (!current.getProfilePath().equals("")) {
            try {
                Picasso.get().load(current.getProfilePath()).into(myHolder.imageViewIcon);
                myHolder.imageViewIcon.setTag(position);
            } catch (Exception e) {

            }
        }*/

        if (current.getProfilePath().contains("http")) {
            try {
                Picasso.get().load(current.getProfilePath()).into(myHolder.imageViewIcon);
                myHolder.imageViewIcon.setTag(position);
            } catch (Exception e) {

            }
        } else {
            myHolder.imageViewIcon.setImageResource(R.drawable.defuserpro);

        }
        myHolder.like.setOnClickListener(new View.OnClickListener() {
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
                    int ID = (Integer) v.getTag();
                    Log.e("", "list Id" + ID);
                    current = data.get(ID);
                    if (AppStatus.getInstance(context).isOnline()) {
                        like_status = "1";
                        if (like_status.equals("1")) {
                            myHolder.disliked.setVisibility(View.GONE);
                            myHolder.like.setVisibility(View.GONE);
                            myHolder.dislike.setVisibility(View.VISIBLE);
                            myHolder.liked.setVisibility(View.VISIBLE);
                        }

                        if (current.getReview_status().contains("-1")) {
                            if (Integer.parseInt(current.getRlcnt()) > 0) {
                                myHolder.dislikecnt.setText("");
                                myHolder.likecnt.setText("" + (Integer.parseInt(current.getRlcnt()) + 1));
                                current.setRlcnt("" + (Integer.parseInt(current.getRlcnt()) + 1));
                            } else {
                                current.setRlcnt("" + 1);
                                myHolder.dislikecnt.setText("");
                                myHolder.likecnt.setText("1");

                            }

                        } else {
                            if (Integer.parseInt(current.getRlcnt()) > 0) {
                                myHolder.likecnt.setText("" + (Integer.parseInt(current.getRlcnt()) + 1));
                                current.setRlcnt("" + (Integer.parseInt(current.getRlcnt()) + 1));
                            } else {
                                current.setRlcnt("1");
                                myHolder.likecnt.setText("" + 1);
                            }
                            if (Integer.parseInt(current.getRdcnt()) > 0) {
                                myHolder.dislikecnt.setText("" + (Integer.parseInt(current.getRdcnt()) - 1));
                                current.setRdcnt("" + (Integer.parseInt(current.getRdcnt()) - 1));
                            } else {
                                myHolder.dislikecnt.setText("0");
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

        myHolder.dislike.setOnClickListener(new View.OnClickListener() {
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
                    int ID = (Integer) v.getTag();
                    Log.e("", "list Id" + ID);
                    current = data.get(ID);
                    if (AppStatus.getInstance(context).isOnline()) {
                        like_status = "0";
                        if (like_status.equals("0")) {
                            myHolder.liked.setVisibility(View.GONE);
                            myHolder.dislike.setVisibility(View.GONE);
                            myHolder.like.setVisibility(View.VISIBLE);
                            myHolder.disliked.setVisibility(View.VISIBLE);

                        }

                        if (current.getReview_status().contains("-1")) {
                            if (Integer.parseInt(current.getRdcnt()) > 0) {
                                myHolder.likecnt.setText("");
                                myHolder.dislikecnt.setText("" + (Integer.parseInt(current.getRdcnt()) + 1));
                                current.setRdcnt("" + (Integer.parseInt(current.getRdcnt()) + 1));
                            } else {
                                myHolder.likecnt.setText("");
                                myHolder.dislikecnt.setText("1");
                                current.setRdcnt("" + 1);
                            }
                        } else {
                            if (Integer.parseInt(current.getRlcnt()) > 0) {
                                myHolder.likecnt.setText("" + (Integer.parseInt(current.getRlcnt()) - 1));
                                current.setRlcnt("" + (Integer.parseInt(current.getRlcnt()) - 1));
                            } else {
                                current.setRlcnt("0");
                                myHolder.likecnt.setText("");
                            }
                            if (Integer.parseInt(current.getRdcnt()) > 0) {
                                myHolder.dislikecnt.setText("" + (Integer.parseInt(current.getRdcnt()) + 1));
                                current.setRdcnt("" + (Integer.parseInt(current.getRdcnt()) + 1));
                            } else {
                                myHolder.dislikecnt.setText("1");
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


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewDate, textComment, likecnt, dislikecnt;
        RatingBar avgratingStar;
        ImageView imageViewIcon, like, dislike, liked, disliked;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textComment = (TextView) itemView.findViewById(R.id.textComment);
            avgratingStar = (RatingBar) itemView.findViewById(R.id.avgratingStar);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            likecnt = (TextView) itemView.findViewById(R.id.likecnt);
            dislikecnt = (TextView) itemView.findViewById(R.id.dislikecnt);
            like = (ImageView) itemView.findViewById(R.id.like);
            dislike = (ImageView) itemView.findViewById(R.id.dislike);
            liked = (ImageView) itemView.findViewById(R.id.liked);
            disliked = (ImageView) itemView.findViewById(R.id.disliked);
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
