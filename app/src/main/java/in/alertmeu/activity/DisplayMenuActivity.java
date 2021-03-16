package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.alertmeu.R;
import in.alertmeu.adapter.CatMianSubAdapter;
import in.alertmeu.adapter.MenuMianSubAdapter;
import in.alertmeu.jsonparser.JsonHelper;
import in.alertmeu.models.AllMainMenuPriceModeDAO;
import in.alertmeu.models.ExAdvertisementDAO;
import in.alertmeu.models.ExAllMainMenuPriceModeDAO;
import in.alertmeu.models.ExMainSubCatDAO;
import in.alertmeu.models.ExMainSubMenuDAO;
import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.models.SubMenuPriListModeDAO;
import in.alertmeu.utils.AppStatus;
import in.alertmeu.utils.Config;
import in.alertmeu.utils.WebClient;

public class DisplayMenuActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    JSONObject jsonLeadObj, jsonLeadObj1;
    String menuListResponse = "", subMenuListResponse = "";
    boolean status;
    String bid, title;
    TextView titleTxt;
    List<SubMenuPriListModeDAO> Menudata;
    List<AllMainMenuPriceModeDAO> SubMenudata;
    ExMainSubMenuDAO exMainSubMenuDAO;
    List<ExMainSubMenuDAO> exMainSubMenuDAOList;
    MenuMianSubAdapter menuMianSubAdapter;
    ExpandableListView mAdvertisementsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_display_menu);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        Intent intent = getIntent();
        bid = intent.getStringExtra("business_id");
        title = intent.getStringExtra("title");
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        titleTxt.setText(title);
        Menudata = new ArrayList<>();
        SubMenudata = new ArrayList<>();
        exMainSubMenuDAOList = new ArrayList<ExMainSubMenuDAO>();
        mAdvertisementsListView = (ExpandableListView) findViewById(R.id.list_advertisement);
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new getMainCategoryList().execute();
        } else {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }

    /*
     * Preparing the list data
     */


    private class getMainCategoryList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("business_user_id", bid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("business_user_id", bid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            menuListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLPRICELISTMENUBYBID, jsonLeadObj);
            subMenuListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLPRICELISTSUBMENUBYBID, jsonLeadObj1);
            Log.i("resp", "menuListResponse" + menuListResponse);
            Log.i("resp", "subMenuListResponse" + subMenuListResponse);
            Menudata.clear();
            SubMenudata.clear();
            if (isJSONValid(menuListResponse)) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObject = new JSONObject(menuListResponse);
                            status = jsonObject.getBoolean("status");
                            if (status) {
                                JsonHelper jsonHelper = new JsonHelper();
                                Menudata = jsonHelper.parseAllMenuList(menuListResponse);
                                SubMenudata = jsonHelper.parseAllSubMenuPriListList(subMenuListResponse);
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            for (int i = 0; i < Menudata.size(); i++) {
                List<ExAllMainMenuPriceModeDAO> exAdvertisementDAOList = new ArrayList<ExAllMainMenuPriceModeDAO>();
                for (int j = 0; j < SubMenudata.size(); j++) {
                    if (Menudata.get(i).getId().equals(SubMenudata.get(j).getMainid_pricelist())) {
                        exAdvertisementDAOList.add(new ExAllMainMenuPriceModeDAO(SubMenudata.get(j).getId(), SubMenudata.get(j).getTitle(), SubMenudata.get(j).getDescription(), SubMenudata.get(j).getRate(), SubMenudata.get(j).getLink(), SubMenudata.get(j).getImage_path(), SubMenudata.get(j).getSubbc_id(), SubMenudata.get(j).getMaincat_id(), SubMenudata.get(j).getBusiness_user_id(), SubMenudata.get(j).getMainid_pricelist()));
                    }
                }
                exMainSubMenuDAO = new ExMainSubMenuDAO(Menudata.get(i).getCategory_name(), exAdvertisementDAOList);
                exMainSubMenuDAOList.add(exMainSubMenuDAO);

            }
            Log.d("Logs", "" + exMainSubMenuDAOList.size());
            menuMianSubAdapter = new MenuMianSubAdapter(DisplayMenuActivity.this, exMainSubMenuDAOList);
            mAdvertisementsListView.setAdapter(menuMianSubAdapter);
            //expand all the Groups
            expandAll();

        }

    }


    //method to expand all groups
    private void expandAll() {
        int count = menuMianSubAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mAdvertisementsListView.expandGroup(i);
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