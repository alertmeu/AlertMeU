package in.alertmeu.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.R;

import in.alertmeu.adapter.SubCatListAdpter;

import in.alertmeu.database.DatabaseQueryClass;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.Constant;
import in.alertmeu.utils.Listener;
import in.alertmeu.utils.WebClient;


public class SubCatDetailsView extends DialogFragment {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    RecyclerView mainCatList;
    JSONObject jsonLeadObj, jsonObjectSync, syncJsonObject;
    JSONArray jsonArray, jsonArraySync;
    String myPlaceListResponse = "", syncDataesponse = "", maincat_id = "";
    List<SubCatModeDAO> data;
    SubCatListAdpter subCatListAdpter;
    LinearLayout showhide;
    ProgressDialog mProgressDialog;
    LinearLayout btnNext;
    Resources res;
    boolean status;
    ArrayList<String> nameArrayList;
    private static Listener mListener;
    ImageView back_arrow1;
    TextView title;
    CheckBox chkAll;
    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View registerView = inflater.inflate(R.layout.dialog_business_sub_category, null);
        context = getActivity();
        res = getResources();
        Window window = getDialog().getWindow();
        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 50;
        window.setAttributes(params);
        preferences = getActivity().getSharedPreferences("Prefrence", getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        mainCatList = (RecyclerView) registerView.findViewById(R.id.mainCatList);
        btnNext = (LinearLayout) registerView.findViewById(R.id.btnNext);
        back_arrow1 = (ImageView) registerView.findViewById(R.id.back_arrow1);
        title = (TextView) registerView.findViewById(R.id.title);
        title.setText(preferences.getString("m_name_cat", ""));
        showhide = (LinearLayout) registerView.findViewById(R.id.showhide);
        data = new ArrayList<>();
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            new getSubCatList().execute();
        } else {

            Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        back_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SubCatModeDAO> stList = ((SubCatListAdpter) subCatListAdpter).getSservicelist();

                jsonArraySync = new JSONArray();
                nameArrayList = new ArrayList<>();
                for (int i = 0; i < stList.size(); i++) {
                    SubCatModeDAO serviceListDAO = stList.get(i);
                    if (serviceListDAO.isSelected() == true) {
                        long id1 = databaseQueryClass.UpdateUserSubCat(Integer.parseInt(serviceListDAO.getId()), Integer.parseInt(serviceListDAO.getBc_id()), Integer.parseInt(preferences.getString("user_id", "")), 1);

                    } else {
                        long id1 = databaseQueryClass.UpdateUserSubCat(Integer.parseInt(serviceListDAO.getId()), Integer.parseInt(serviceListDAO.getBc_id()), Integer.parseInt(preferences.getString("user_id", "")), 0);

                    }
                    jsonObjectSync = new JSONObject();
                    try {
                        jsonObjectSync.put("subbc_id", serviceListDAO.getId());
                        jsonObjectSync.put("maincat_id", serviceListDAO.getBc_id());
                        if (serviceListDAO.isSelected() == true) {
                            jsonObjectSync.put("status", 1);
                        } else {
                            jsonObjectSync.put("status", 0);
                        }
                        jsonObjectSync.put("user_id", preferences.getString("user_id", ""));
                        jsonArraySync.put(jsonObjectSync);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mListener.messageReceived("message");
                dismiss();
                try {
                    syncJsonObject = new JSONObject();
                    syncJsonObject.put("preData", jsonArraySync);
                    Log.d("preData", "" + syncJsonObject);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Thread objectThread = new Thread(new Runnable() {
                    public void run() {
                        // TODO Auto-generated method stub
                        WebClient serviceAccess = new WebClient();
                        syncDataesponse = serviceAccess.SendHttpPost(Config.URL_SHOPPERPRECATSYNCDATAOFFLINE, syncJsonObject);
                        Log.i("resp", "syncDataesponse" + syncDataesponse);

                    }
                });
                objectThread.start();
            }
        });
        chkAll = (CheckBox) registerView.findViewById(R.id.chkAllSelected);

        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    List<SubCatModeDAO> list = ((SubCatListAdpter) subCatListAdpter).getSservicelist();
                    for (SubCatModeDAO workout : list) {
                        workout.setSelected(true);
                        workout.setChecked_status("1");
                    }
                    prefEditor.putBoolean(preferences.getString("m_id_l", ""), true);
                    prefEditor.commit();
                    ((SubCatListAdpter) mainCatList.getAdapter()).notifyDataSetChanged();
                } else {
                    List<SubCatModeDAO> list = ((SubCatListAdpter) subCatListAdpter).getSservicelist();
                    for (SubCatModeDAO workout : list) {
                        workout.setSelected(false);
                        workout.setChecked_status("0");
                    }
                    prefEditor.remove(preferences.getString("m_id_l", ""));
                    prefEditor.commit();
                    ((SubCatListAdpter) mainCatList.getAdapter()).notifyDataSetChanged();
                }
            }
        });
        return registerView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        update();
                        return true;
                    } else {
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }

    private void update() {
        dismiss();
    }

    private class getSubCatList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //   mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            //    mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //   mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //    mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("user_id", ""));
                        put("bc_id", preferences.getString("m_id_l", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            //  WebClient serviceAccess = new WebClient();

            // Log.i("json", "json" + jsonLeadObj);
            //  myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLUSERSMAINSUBCATEGORY, jsonLeadObj);
            //  Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            data.clear();
            data.addAll(databaseQueryClass.getUserSubCatCat(Integer.parseInt(preferences.getString("user_id", "")), Integer.parseInt(preferences.getString("m_id_l", ""))));

            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {


                                    JsonHelper jsonHelper = new JsonHelper();
                                    data = jsonHelper.parseSubCatList(myPlaceListResponse);
                                    jsonArray = new JSONArray(myPlaceListResponse);

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            int cac = 0;
            //mProgressDialog.dismiss();
            if (data.size() > 0) {
                btnNext.setVisibility(View.VISIBLE);
                subCatListAdpter = new SubCatListAdpter(getActivity(), data);
                mainCatList.setAdapter(subCatListAdpter);
                mainCatList.setLayoutManager(new LinearLayoutManager(getActivity()));
                subCatListAdpter.notifyDataSetChanged();
                showhide.setVisibility(View.VISIBLE);
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getChecked_status().equals("0")) {
                        cac++;
                    }
                }
                if (cac > 0) {
                    chkAll.setChecked(false);

                } else {
                    chkAll.setChecked(true);
                }
            } else {
                btnNext.setVisibility(View.GONE);
                showhide.setVisibility(View.GONE);
            }
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

    public static void bindListener(Listener listener) {
        mListener = listener;
    }

}