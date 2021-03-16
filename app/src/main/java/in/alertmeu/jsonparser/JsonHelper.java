package in.alertmeu.jsonparser;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.alertmeu.database.DatabaseHelper;
import in.alertmeu.models.AdvertisementAllBusDAO;
import in.alertmeu.models.AdvertisementDAO;
import in.alertmeu.models.AllMainMenuPriceModeDAO;
import in.alertmeu.models.FAQDAO;
import in.alertmeu.models.ImageModel;
import in.alertmeu.models.IncidentDAO;
import in.alertmeu.models.IncidentTypeModeDAO;
import in.alertmeu.models.LocationDAO;
import in.alertmeu.models.MainCatModeDAO;
import in.alertmeu.models.MoreIncInfoModel;
import in.alertmeu.models.MyPlaceModeDAO;
import in.alertmeu.models.ShowAllReviewsDAO;
import in.alertmeu.models.SubCatModeDAO;
import in.alertmeu.models.IncidentSubTypeModeDAO;
import in.alertmeu.models.SubMenuPriListModeDAO;
import in.alertmeu.models.YouTubeDAO;
import in.alertmeu.utils.Constant;


public class JsonHelper {

    private ArrayList<LocationDAO> locationDAOArrayList = new ArrayList<LocationDAO>();
    private LocationDAO locationDAO;

    private ArrayList<AdvertisementDAO> advertisementDAOArrayList = new ArrayList<AdvertisementDAO>();
    private AdvertisementDAO advertisementDAO;

    private ArrayList<ImageModel> imageModelArrayList = new ArrayList<ImageModel>();
    private ImageModel imageModel;

    private ArrayList<MyPlaceModeDAO> myPlaceModeDAOArrayList = new ArrayList<MyPlaceModeDAO>();
    private MyPlaceModeDAO myPlaceModeDAO;

    private ArrayList<MainCatModeDAO> mainCatModeDAOArrayList = new ArrayList<MainCatModeDAO>();
    private MainCatModeDAO mainCatModeDAO;

    private ArrayList<SubCatModeDAO> subCatModeDAOArrayList = new ArrayList<SubCatModeDAO>();
    private SubCatModeDAO subCatModeDAO;

    private ArrayList<FAQDAO> faqdaoArrayList = new ArrayList<FAQDAO>();
    private FAQDAO faqdao;

    private ArrayList<ShowAllReviewsDAO> showAllReviewsDAOArrayList = new ArrayList<ShowAllReviewsDAO>();
    private ShowAllReviewsDAO showAllReviewsDAO;

    private ArrayList<YouTubeDAO> youTubeDAOArrayList = new ArrayList<YouTubeDAO>();
    private YouTubeDAO youTubeDAO;

    private ArrayList<AdvertisementAllBusDAO> advertisementAllBusDAOArrayList = new ArrayList<AdvertisementAllBusDAO>();
    private AdvertisementAllBusDAO advertisementAllBusDAO;

    private ArrayList<SubMenuPriListModeDAO> subMenuPriListModeDAOArrayList = new ArrayList<SubMenuPriListModeDAO>();
    private SubMenuPriListModeDAO subMenuPriListModeDAO;

    private ArrayList<AllMainMenuPriceModeDAO> allMainMenuPriceModeDAOArrayList = new ArrayList<AllMainMenuPriceModeDAO>();
    private AllMainMenuPriceModeDAO allMainMenuPriceModeDAO;

    private ArrayList<IncidentDAO> incidentDAOArrayList = new ArrayList<IncidentDAO>();
    private IncidentDAO incidentDAO;

    private ArrayList<MoreIncInfoModel> moreIncInfoModelArrayList = new ArrayList<MoreIncInfoModel>();
    private MoreIncInfoModel moreIncInfoModel;

    private ArrayList<IncidentTypeModeDAO> incidentTypeModeDAOArrayList = new ArrayList<IncidentTypeModeDAO>();
    private IncidentTypeModeDAO incidentTypeModeDAO;

    private ArrayList<IncidentSubTypeModeDAO> incidentSubTypeModeDAOArrayList = new ArrayList<IncidentSubTypeModeDAO>();
    private IncidentSubTypeModeDAO incidentSubTypeModeDAO;

    JSONArray leadJsonObj;


    //locationPaser
    public ArrayList<LocationDAO> parseLocationList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("locationPaser", rawLeadListResponse);
        try {
            Object json = new JSONTokener(rawLeadListResponse).nextValue();

            if (json instanceof JSONObject) {
                //you have an object
            } else if (json instanceof JSONArray) {
                //you have an array
                leadJsonObj = new JSONArray(rawLeadListResponse);
            }
            for (int i = 0; i < leadJsonObj.length(); i++) {
                String sequence = String.format("%03d", i + 1);
                locationDAO = new LocationDAO();
                JSONObject json_data = leadJsonObj.getJSONObject(i);
                //locationDAO.setUser_id(json_data.getString("business_user_id"));
                // locationDAO.setDescribe_limitations(json_data.getString("describe_limitations"));
                // locationDAO.setFlag_map(json_data.getString("title"));
                //  locationDAO.setPath(json_data.getString("original_image_path"));
                // locationDAO.setDescription(json_data.getString("description"));
                //  locationDAO.setRq_code(json_data.getString("rq_code"));
                // locationDAO.setTitle(json_data.getString("title"));
                // locationDAO.setLikecnt(json_data.getString("likecnt"));
                // locationDAO.setDislikecnt(json_data.getString("dislikecnt"));
                // locationDAO.setS_time(json_data.getString("s_time"));
                //  locationDAO.setE_date(json_data.getString("e_date"));
                // locationDAO.setE_time(json_data.getString("e_time"));
                //  locationDAO.setS_date(json_data.getString("s_date"));
                locationDAO.setBusiness_main_category(json_data.getString("main_cat_name"));
                locationDAO.setBusiness_subcategory(json_data.getString("subcategory_name"));
                locationDAO.setMain_cat_name_hindi(json_data.getString("main_cat_name_hindi"));
                locationDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                locationDAO.setId(json_data.getString("id"));
                locationDAO.setSponsor_flag(json_data.getString("sponsor_flag"));
                locationDAO.setReferral_code(json_data.getString("referral_code"));
                locationDAO.setpBidavgrating(json_data.getString("pBidavgrating"));
                locationDAO.setTorcount(json_data.getString("torcount"));
                locationDAO.setLatitude(json_data.getString("latitude"));
                locationDAO.setLongitude(json_data.getString("longitude"));
                locationDAO.setPath(json_data.getString("company_logo"));
                locationDAO.setNumbers("" + sequence);
                locationDAO.setBusiness_name(json_data.getString("business_name"));
                locationDAO.setAddress(json_data.getString("address"));
                locationDAO.setMobile_no(json_data.getString("mobile_no"));
                locationDAO.setStatus(json_data.getString("status"));
                locationDAO.setPrice_status(json_data.getString("price_status"));
                if (json_data.getString("business_email").equals("")) {
                    locationDAO.setBusiness_email(" ");
                } else {
                    locationDAO.setBusiness_email(json_data.getString("business_email"));
                }
                if (json_data.getString("business_number").equals("")) {
                    locationDAO.setBusiness_number(" ");
                } else {
                    locationDAO.setBusiness_number(json_data.getString("business_number"));
                }
                locationDAOArrayList.add(locationDAO);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return locationDAOArrayList;
    }

    public ArrayList<LocationDAO> parseLocationListPage(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("locationPaser", rawLeadListResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawLeadListResponse);
            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                /*for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    imageModel = new ImageModel();
                    imageModel.setImage_id(object.getString("id"));
                    imageModel.setImage_path(object.getString("image_path"));
                    imageModel.setImage_description(object.getString("image_description"));
                    imageModel.setImage_description_hindi(object.getString("image_description_hindi"));
                    imageModelArrayList.add(imageModel);
                }*/

                for (int i = 0; i < jsonArray.length(); i++) {
                    String sequence = String.format("%03d", i + 1);
                    locationDAO = new LocationDAO();
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    locationDAO.setBusiness_main_category(json_data.getString("main_cat_name"));
                    locationDAO.setBusiness_subcategory(json_data.getString("subcategory_name"));
                    locationDAO.setMain_cat_name_hindi(json_data.getString("main_cat_name_hindi"));
                    locationDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                    locationDAO.setId(json_data.getString("id"));
                    locationDAO.setSponsor_flag(json_data.getString("sponsor_flag"));
                    locationDAO.setReferral_code(json_data.getString("referral_code"));
                    locationDAO.setpBidavgrating(json_data.getString("pBidavgrating"));
                    locationDAO.setTorcount(json_data.getString("torcount"));
                    locationDAO.setLatitude(json_data.getString("latitude"));
                    locationDAO.setLongitude(json_data.getString("longitude"));
                    locationDAO.setPath(json_data.getString("company_logo"));
                    locationDAO.setNumbers("" + sequence);
                    locationDAO.setBusiness_name(json_data.getString("business_name"));
                    locationDAO.setAddress(json_data.getString("address"));
                    locationDAO.setMobile_no(json_data.getString("mobile_no"));
                    locationDAO.setStatus(json_data.getString("status"));
                    locationDAO.setPrice_status(json_data.getString("price_status"));
                    if (json_data.getString("business_email").equals("")) {
                        locationDAO.setBusiness_email(" ");
                    } else {
                        locationDAO.setBusiness_email(json_data.getString("business_email"));
                    }
                    if (json_data.getString("business_number").equals("")) {
                        locationDAO.setBusiness_number(" ");
                    } else {
                        locationDAO.setBusiness_number(json_data.getString("business_number"));
                    }
                    locationDAOArrayList.add(locationDAO);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return locationDAOArrayList;
    }

    //advertisementPaser
    public ArrayList<AdvertisementDAO> parseAdvertisementList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", rawLeadListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(rawLeadListResponse);

            for (int i = 0; i < leadJsonObj.length(); i++) {
                advertisementDAO = new AdvertisementDAO();
                String sequence = String.format("%03d", i + 1);
                JSONObject json_data = leadJsonObj.getJSONObject(i);
                advertisementDAO.setId(json_data.getString("id"));
                advertisementDAO.setBusiness_user_id(json_data.getString("business_user_id"));
                advertisementDAO.setMobile_no(json_data.getString("mobile_no"));
                advertisementDAO.setBusiness_email(json_data.getString("business_email"));
                advertisementDAO.setLatitude(json_data.getString("latitude"));
                advertisementDAO.setLongitude(json_data.getString("longitude"));
                advertisementDAO.setAddress(json_data.getString("address"));
                advertisementDAO.setCompany_logo(json_data.getString("company_logo"));
                advertisementDAO.setTitle(json_data.getString("title"));
                advertisementDAO.setDescription(json_data.getString("description"));
                advertisementDAO.setBusiness_name(json_data.getString("business_name"));
                advertisementDAO.setBusiness_number(json_data.getString("business_number"));
                advertisementDAO.setBusiness_main_category_id(json_data.getString("business_main_category_id"));
                // advertisementDAO.setBusiness_subcategory_id(json_data.getString("business_subcategory_id"));
                advertisementDAO.setDescribe_limitations(json_data.getString("describe_limitations"));
                advertisementDAO.setRq_code(json_data.getString("rq_code"));
                advertisementDAO.setOriginal_image_path(json_data.getString("original_image_path"));
                advertisementDAO.setModify_image_path(json_data.getString("modify_image_path"));
                advertisementDAO.setLocation_name(json_data.getString("location_name"));
             //   advertisementDAO.setLikecnt(json_data.getString("likecnt"));
             //   advertisementDAO.setDislikecnt(json_data.getString("dislikecnt"));
                advertisementDAO.setS_time(json_data.getString("s_time"));
                advertisementDAO.setE_time(json_data.getString("e_time"));
                advertisementDAO.setS_date(json_data.getString("s_date"));
                advertisementDAO.setE_date(json_data.getString("e_date"));
                advertisementDAO.setMain_cat_name(json_data.getString("main_cat_name"));
                advertisementDAO.setSubcategory_name(json_data.getString("subcategory_name"));
                advertisementDAO.setMain_cat_name_hindi(json_data.getString("main_cat_name_hindi"));
                advertisementDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                advertisementDAO.setSponsor_flag(json_data.getString("sponsor_flag"));
                advertisementDAO.setReferral_code(json_data.getString("referral_code"));
                advertisementDAO.setBus_main_cat_name(json_data.getString("bus_main_cat_name"));
                advertisementDAO.setBus_subcategory_name(json_data.getString("bus_subcategory_name"));
                advertisementDAO.setBus_main_cat_name_hindi(json_data.getString("bus_main_cat_name_hindi"));
                advertisementDAO.setBus_subcategory_name_hindi(json_data.getString("bus_subcategory_name_hindi"));
                advertisementDAO.setPrice_status(json_data.getString("price_status"));
                advertisementDAO.setNumbers("" + sequence);
                advertisementDAOArrayList.add(advertisementDAO);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return advertisementDAOArrayList;
    }

    //advertisementPaser
    public ArrayList<ImageModel> parseImagePathList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", ListResponse);
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);
            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    imageModel = new ImageModel();
                    imageModel.setImage_id(object.getString("id"));
                    imageModel.setImage_path(object.getString("image_path"));
                    imageModel.setImage_description(object.getString("image_description"));
                    imageModel.setImage_description_hindi(object.getString("image_description_hindi"));
                    imageModelArrayList.add(imageModel);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageModelArrayList;
    }

    //advertisementPaser
    public ArrayList<MyPlaceModeDAO> parseMyPlaceList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                myPlaceModeDAO = new MyPlaceModeDAO();
                myPlaceModeDAO.setId(object.getString("id"));
                myPlaceModeDAO.setUser_id(object.getString("user_id"));
                myPlaceModeDAO.setFull_address(object.getString("full_address"));
                myPlaceModeDAO.setLatitude(object.getString("latitude"));
                myPlaceModeDAO.setLongitude(object.getString("longitude"));
                myPlaceModeDAO.setTime_stamp(object.getString("time_stamp"));
                myPlaceModeDAOArrayList.add(myPlaceModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myPlaceModeDAOArrayList;
    }

    //Main cat
    public ArrayList<MainCatModeDAO> parseMainCatList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("maincat", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);

            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                mainCatModeDAO = new MainCatModeDAO();
                mainCatModeDAO.setId(object.getString("id"));
                mainCatModeDAO.setCategory_name(object.getString("category_name"));
                mainCatModeDAO.setCategory_name_hindi(object.getString("category_name_hindi"));
                mainCatModeDAO.setChecked_status(object.getString("checked_status"));
                mainCatModeDAO.setImage_path(object.getString("image_path"));
                mainCatModeDAOArrayList.add(mainCatModeDAO);

            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mainCatModeDAOArrayList;
    }

    //Sub cat
    public ArrayList<SubCatModeDAO> parseSubCatList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("subcat", ListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                subCatModeDAO = new SubCatModeDAO();
                subCatModeDAO.setId(object.getString("id"));
                subCatModeDAO.setBc_id(object.getString("bc_id"));
                subCatModeDAO.setSubcategory_name(object.getString("subcategory_name"));
                subCatModeDAO.setSubcategory_name_hindi(object.getString("subcategory_name_hindi"));
                subCatModeDAO.setChecked_status(object.getString("checked_status"));
                subCatModeDAO.setImage_path(object.getString("image_path"));
                subCatModeDAOArrayList.add(subCatModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subCatModeDAOArrayList;
    }

    public ArrayList<FAQDAO> parseFAQList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("faq", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                faqdao = new FAQDAO();
                faqdao.setId(object.getString("id"));
                faqdao.setTitle(object.getString("title"));
                faqdao.setDescription(object.getString("description"));
                faqdao.setTitle_hindi(object.getString("title_hindi"));
                faqdao.setDescription_hindi(object.getString("description_hindi"));
                faqdaoArrayList.add(faqdao);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return faqdaoArrayList;
    }

    //Main cat
    public ArrayList<MainCatModeDAO> parseNewMainCatList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("maincat", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                mainCatModeDAO = new MainCatModeDAO();
                mainCatModeDAO.setId(object.getString("id"));
                mainCatModeDAO.setCategory_name(object.getString("category_name"));
                mainCatModeDAO.setCategory_name_hindi(object.getString("category_name_hindi"));
                mainCatModeDAOArrayList.add(mainCatModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mainCatModeDAOArrayList;
    }

    public ArrayList<YouTubeDAO> parseYouTubeList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("youtube", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                youTubeDAO = new YouTubeDAO();
                youTubeDAO.setId(object.getString("id"));
                youTubeDAO.setVideo_description(object.getString("video_description"));
                youTubeDAO.setVideo_description_hindi(object.getString("video_description_hindi"));
                youTubeDAO.setVideo_link(object.getString("video_link"));
                youTubeDAO.setHindi_video_link(object.getString("hindi_video_link"));
                youTubeDAOArrayList.add(youTubeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return youTubeDAOArrayList;
    }

    //main cat offline
    public long parsemainCatOfflineList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("maincatoffline", ListResponse);
        long id = -1;
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);
            DatabaseHelper databaseHelper1 = DatabaseHelper.getInstance();
            SQLiteDatabase sqLiteDatabase1 = databaseHelper1.getWritableDatabase();
            try {
                id = sqLiteDatabase1.delete(Constant.TABLE_TBL_BUSINESS_CATEGORY, null, null);
            } catch (SQLiteException e) {

            } finally {
                //sqLiteDatabase1.close();
            }
            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                    SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                    String dateToStr = format.format(today);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Constant.ID, object.getInt("id"));
                    contentValues.put(Constant.COUNTRY_CODE, object.getString("country_code"));
                    contentValues.put(Constant.ADS_PRICING, object.getDouble("ads_pricing"));
                    contentValues.put(Constant.DISCOUNT, object.getDouble("discount"));
                    contentValues.put(Constant.NOTIFICATION_CHARGE, object.getDouble("notification_charge"));
                    contentValues.put(Constant.ROUNDING_SCALE, object.getInt("rounding_scale"));
                    contentValues.put(Constant.CURRENCY_SIGN, object.getString("currency_sign"));
                    contentValues.put(Constant.CATEGORY_NAME, object.getString("category_name"));
                    contentValues.put(Constant.CATEGORY_NAME_HINDI, object.getString("category_name_hindi"));
                    contentValues.put(Constant.SPECIAL_MESSAGE, object.getString("special_message"));
                    contentValues.put(Constant.SPECIAL_MESSAGE_HINDI, object.getString("special_message_hindi"));
                    contentValues.put(Constant.PRICE_FORMULA, object.getString("price_formula"));
                    contentValues.put(Constant.STATUS, object.getInt("status"));
                    contentValues.put(Constant.IMAGE_PATH, object.getString("image_path"));
                    contentValues.put(Constant.SYNC_STATUS, 1);
                    contentValues.put(Constant.CREATE_AT, dateToStr);
                    try {
                        id = sqLiteDatabase.insertOrThrow(Constant.TABLE_TBL_BUSINESS_CATEGORY, null, contentValues);
                    } catch (SQLiteException e) {
                        //Logger.d("Exception: " + e.getMessage());
                        //Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        // sqLiteDatabase.close();
                    }
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;
    }

    //sub cat offline
    public long parseSubCatOfflineList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("subcatoffline", ListResponse);
        long id = -1;
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);
            DatabaseHelper databaseHelper1 = DatabaseHelper.getInstance();
            SQLiteDatabase sqLiteDatabase1 = databaseHelper1.getWritableDatabase();
            try {
                id = sqLiteDatabase1.delete(Constant.TABLE_TBL_BUSINESS_SUBCATEGORY, null, null);
            } catch (SQLiteException e) {

            } finally {
                //sqLiteDatabase1.close();
            }
            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                    SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                    String dateToStr = format.format(today);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Constant.ID, object.getInt("id"));
                    contentValues.put(Constant.BC_ID, object.getInt("bc_id"));
                    contentValues.put(Constant.SUBCATEGORY_NAME, object.getString("subcategory_name"));
                    contentValues.put(Constant.SUBCATEGORY_NAME_HINDI, object.getString("subcategory_name_hindi"));
                    contentValues.put(Constant.STATUS, object.getInt("status"));
                    contentValues.put(Constant.IMAGE_PATH, object.getString("image_path"));
                    contentValues.put(Constant.SYNC_STATUS, 1);
                    contentValues.put(Constant.CREATE_AT, dateToStr);
                    try {
                        id = sqLiteDatabase.insertOrThrow(Constant.TABLE_TBL_BUSINESS_SUBCATEGORY, null, contentValues);
                    } catch (SQLiteException e) {
                        //Logger.d("Exception: " + e.getMessage());
                        //Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        //  sqLiteDatabase.close();
                    }
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;
    }

    //sub cat offline
    public long parseMainSubCatOfflineList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("subcatoffline", ListResponse);
        long id = -1;
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);
            DatabaseHelper databaseHelper1 = DatabaseHelper.getInstance();
            SQLiteDatabase sqLiteDatabase1 = databaseHelper1.getWritableDatabase();
            try {
                id = sqLiteDatabase1.delete(Constant.TABLE_TBL_USER_SUBCATGEORY, null, null);
            } catch (SQLiteException e) {

            } finally {
                //sqLiteDatabase1.close();
            }
            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                    SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                    String dateToStr = format.format(today);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Constant.ID, object.getInt("id"));
                    contentValues.put(Constant.USER_ID, object.getInt("user_id"));
                    contentValues.put(Constant.SUBBC_ID, object.getInt("subbc_id"));
                    contentValues.put(Constant.MAINCAT_ID, object.getInt("maincat_id"));
                    contentValues.put(Constant.STATUS, object.getInt("status"));
                    contentValues.put(Constant.SYNC_STATUS, 1);
                    contentValues.put(Constant.CREATE_AT, dateToStr);
                    try {
                        id = sqLiteDatabase.insertOrThrow(Constant.TABLE_TBL_USER_SUBCATGEORY, null, contentValues);
                    } catch (SQLiteException e) {
                        //Logger.d("Exception: " + e.getMessage());
                        //Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        // sqLiteDatabase.close();
                    }
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;
    }

    //advertisementPaser
    public ArrayList<ShowAllReviewsDAO> parseReviewList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", ListResponse);
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    showAllReviewsDAO = new ShowAllReviewsDAO();
                    showAllReviewsDAO.setId(object.getString("id"));
                    showAllReviewsDAO.setFirst_name(object.getString("first_name"));
                    showAllReviewsDAO.setLast_name(object.getString("last_name"));
                    showAllReviewsDAO.setGender(object.getString("gender"));
                    showAllReviewsDAO.setBusiness_id(object.getString("business_id"));
                    showAllReviewsDAO.setEmail_id(object.getString("email_id"));
                    showAllReviewsDAO.setUser_email(object.getString("user_email"));
                    showAllReviewsDAO.setMobile_no(object.getString("mobile_no"));
                    showAllReviewsDAO.setProfilePath(object.getString("profilePath"));
                    showAllReviewsDAO.setRating_star(object.getString("rating_star"));
                    showAllReviewsDAO.setFcm_id(object.getString("fcm_id"));
                    showAllReviewsDAO.setTime_stamp(object.getString("time_stamp"));
                    showAllReviewsDAO.setUser_id(object.getString("user_id"));
                    showAllReviewsDAO.setUser_mobile(object.getString("user_mobile"));
                    showAllReviewsDAO.setUser_review(object.getString("user_review"));
                    showAllReviewsDAO.setRdcnt(object.getString("rdcnt"));
                    showAllReviewsDAO.setRlcnt(object.getString("rlcnt"));
                    showAllReviewsDAO.setReview_status(object.getString("review_status"));
                    showAllReviewsDAOArrayList.add(showAllReviewsDAO);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return showAllReviewsDAOArrayList;
    }

    //advertisementPaser
    public ArrayList<AdvertisementAllBusDAO> parseAllAdvertisementList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", rawLeadListResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawLeadListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    advertisementAllBusDAO = new AdvertisementAllBusDAO();
                    String sequence = String.format("%03d", i + 1);
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    advertisementAllBusDAO.setId(json_data.getString("id"));
                    advertisementAllBusDAO.setBusiness_user_id(json_data.getString("business_user_id"));
                    advertisementAllBusDAO.setMobile_no(json_data.getString("mobile_no"));
                    advertisementAllBusDAO.setBusiness_email(json_data.getString("business_email"));
                    advertisementAllBusDAO.setLatitude(json_data.getString("latitude"));
                    advertisementAllBusDAO.setLongitude(json_data.getString("longitude"));
                    advertisementAllBusDAO.setAddress(json_data.getString("address"));
                    advertisementAllBusDAO.setCompany_logo(json_data.getString("company_logo"));
                    advertisementAllBusDAO.setTitle(json_data.getString("title"));
                    advertisementAllBusDAO.setDescription(json_data.getString("description"));
                    advertisementAllBusDAO.setBusiness_name(json_data.getString("business_name"));
                    advertisementAllBusDAO.setBusiness_number(json_data.getString("business_number"));
                    advertisementAllBusDAO.setBusiness_main_category_id(json_data.getString("business_main_category_id"));
                    // advertisementAllBusDAO.setBusiness_subcategory_id(json_data.getString("business_subcategory_id"));
                    advertisementAllBusDAO.setDescribe_limitations(json_data.getString("describe_limitations"));
                    advertisementAllBusDAO.setRq_code(json_data.getString("rq_code"));
                    advertisementAllBusDAO.setOriginal_image_path(json_data.getString("original_image_path"));
                    advertisementAllBusDAO.setModify_image_path(json_data.getString("modify_image_path"));
                    advertisementAllBusDAO.setLocation_name(json_data.getString("location_name"));
                    advertisementAllBusDAO.setLikecnt(json_data.getString("likecnt"));
                    advertisementAllBusDAO.setDislikecnt(json_data.getString("dislikecnt"));
                    advertisementAllBusDAO.setS_time(json_data.getString("s_time"));
                    advertisementAllBusDAO.setE_time(json_data.getString("e_time"));
                    advertisementAllBusDAO.setS_date(json_data.getString("s_date"));
                    advertisementAllBusDAO.setE_date(json_data.getString("e_date"));
                    advertisementAllBusDAO.setMain_cat_name(json_data.getString("main_cat_name"));
                    advertisementAllBusDAO.setSubcategory_name(json_data.getString("subcategory_name"));
                    advertisementAllBusDAO.setMain_cat_name_hindi(json_data.getString("main_cat_name_hindi"));
                    advertisementAllBusDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                    advertisementAllBusDAO.setBus_main_cat_name(json_data.getString("bus_main_cat_name"));
                    advertisementAllBusDAO.setBus_subcategory_name(json_data.getString("bus_subcategory_name"));
                    advertisementAllBusDAO.setBus_main_cat_name_hindi(json_data.getString("bus_main_cat_name_hindi"));
                    advertisementAllBusDAO.setBus_subcategory_name_hindi(json_data.getString("bus_subcategory_name_hindi"));
                    advertisementAllBusDAO.setPrice_status(json_data.getString("price_status"));
                    advertisementAllBusDAO.setNumbers("" + sequence);
                    advertisementAllBusDAOArrayList.add(advertisementAllBusDAO);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return advertisementAllBusDAOArrayList;
    }

    //advertisementPaser
    public ArrayList<LocationDAO> parseAllOtherBusineesList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", rawLeadListResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawLeadListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    locationDAO = new LocationDAO();
                    String sequence = String.format("%03d", i + 1);
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    locationDAO.setBusiness_main_category(json_data.getString("main_cat_name"));
                    locationDAO.setBusiness_subcategory(json_data.getString("subcategory_name"));
                    locationDAO.setMain_cat_name_hindi(json_data.getString("main_cat_name_hindi"));
                    locationDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                    locationDAO.setId(json_data.getString("id"));
                    locationDAO.setSponsor_flag(json_data.getString("sponsor_flag"));
                    locationDAO.setReferral_code(json_data.getString("referral_code"));
                    locationDAO.setpBidavgrating(json_data.getString("pBidavgrating"));
                    locationDAO.setTorcount(json_data.getString("torcount"));
                    locationDAO.setLatitude(json_data.getString("latitude"));
                    locationDAO.setLongitude(json_data.getString("longitude"));
                    locationDAO.setPath(json_data.getString("company_logo"));
                    locationDAO.setNumbers("" + sequence);
                    locationDAO.setBusiness_name(json_data.getString("business_name"));
                    locationDAO.setAddress(json_data.getString("address"));
                    locationDAO.setMobile_no(json_data.getString("mobile_no"));
                    locationDAO.setPrice_status(json_data.getString("price_status"));
                    if (json_data.getString("business_email").equals("")) {
                        locationDAO.setBusiness_email(" ");
                    } else {
                        locationDAO.setBusiness_email(json_data.getString("business_email"));
                    }
                    if (json_data.getString("business_number").equals("")) {
                        locationDAO.setBusiness_number(" ");
                    } else {
                        locationDAO.setBusiness_number(json_data.getString("business_number"));
                    }
                    locationDAOArrayList.add(locationDAO);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return locationDAOArrayList;
    }

    public ArrayList<SubMenuPriListModeDAO> parseAllMenuList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", ListResponse);
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    subMenuPriListModeDAO = new SubMenuPriListModeDAO();
                    subMenuPriListModeDAO.setId(object.getString("id"));
                    subMenuPriListModeDAO.setCategory_name(object.getString("category_name"));
                    subMenuPriListModeDAO.setBusiness_user_id(object.getString("business_user_id"));
                    subMenuPriListModeDAO.setSubbc_id(object.getString("subbc_id"));
                    subMenuPriListModeDAOArrayList.add(subMenuPriListModeDAO);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subMenuPriListModeDAOArrayList;
    }

    public ArrayList<AllMainMenuPriceModeDAO> parseAllSubMenuPriListList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", ListResponse);
        try {
            JSONObject jsonObject = new JSONObject(ListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    allMainMenuPriceModeDAO = new AllMainMenuPriceModeDAO();
                    allMainMenuPriceModeDAO.setId(object.getString("id"));
                    allMainMenuPriceModeDAO.setTitle(object.getString("title"));
                    allMainMenuPriceModeDAO.setDescription(object.getString("description"));
                    allMainMenuPriceModeDAO.setRate(object.getString("rate"));
                    allMainMenuPriceModeDAO.setLink(object.getString("link"));
                    allMainMenuPriceModeDAO.setSubbc_id(object.getString("subbc_id"));
                    allMainMenuPriceModeDAO.setMaincat_id(object.getString("maincat_id"));
                    allMainMenuPriceModeDAO.setMainid_pricelist(object.getString("mainid_pricelist"));
                    allMainMenuPriceModeDAO.setImage_path(object.getString("image_path"));
                    allMainMenuPriceModeDAOArrayList.add(allMainMenuPriceModeDAO);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return allMainMenuPriceModeDAOArrayList;
    }

    public ArrayList<IncidentDAO> parseAllIncList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", rawLeadListResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawLeadListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String sequence = String.format("%03d", i + 1);
                    incidentDAO = new IncidentDAO();
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    incidentDAO.setBusiness_main_category(json_data.getString("main_cat_name"));
                    incidentDAO.setBusiness_subcategory(json_data.getString("subcategory_name"));
                    incidentDAO.setMain_cat_name_hindi(json_data.getString("main_cat_name_hindi"));
                    incidentDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                    incidentDAO.setId(json_data.getString("id"));
                    incidentDAO.setSponsor_flag(json_data.getString("sponsor_flag"));
                    incidentDAO.setReferral_code(json_data.getString("referral_code"));
                    incidentDAO.setpBidavgrating(json_data.getString("pBidavgrating"));
                    incidentDAO.setTorcount(json_data.getString("torcount"));
                    incidentDAO.setLatitude(json_data.getString("latitude"));
                    incidentDAO.setLongitude(json_data.getString("longitude"));
                    incidentDAO.setPath(json_data.getString("company_logo"));
                    incidentDAO.setNumbers("" + sequence);
                    incidentDAO.setBusiness_name(json_data.getString("business_name"));
                    incidentDAO.setAddress(json_data.getString("address"));
                    incidentDAO.setMobile_no(json_data.getString("mobile_no"));
                    incidentDAO.setStatus(json_data.getString("status"));
                    if (json_data.getString("business_email").equals("")) {
                        incidentDAO.setBusiness_email(" ");
                    } else {
                        incidentDAO.setBusiness_email(json_data.getString("business_email"));
                    }
                    if (json_data.getString("business_number").equals("")) {
                        incidentDAO.setBusiness_number(" ");
                    } else {
                        incidentDAO.setBusiness_number(json_data.getString("business_number"));
                    }
                    incidentDAOArrayList.add(incidentDAO);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return incidentDAOArrayList;
    }

    public ArrayList<MoreIncInfoModel> parseAllOtherMoreIncList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", rawLeadListResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawLeadListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    moreIncInfoModel = new MoreIncInfoModel();
                    String sequence = String.format("%03d", i + 1);
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    moreIncInfoModel.setId(json_data.getString("id"));
                    moreIncInfoModel.setComments(json_data.getString("comments"));
                    moreIncInfoModel.setIncident_id(json_data.getString("incident_id"));
                    moreIncInfoModel.setImage_path(json_data.getString("image_path"));
                    moreIncInfoModel.setDatetime(json_data.getString("datetime"));
                    moreIncInfoModel.setUser_id(json_data.getString("user_id"));
                    moreIncInfoModelArrayList.add(moreIncInfoModel);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return moreIncInfoModelArrayList;
    }

    public ArrayList<SubCatModeDAO> parsePageNationList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", rawLeadListResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawLeadListResponse);

            if (!jsonObject.isNull("dataList")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    subCatModeDAO = new SubCatModeDAO();
                    String sequence = String.format("%03d", i + 1);
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    subCatModeDAO.setId(json_data.getString("id"));
                    subCatModeDAO.setBc_id(json_data.getString("bc_id"));
                    subCatModeDAO.setSubcategory_name(json_data.getString("subcategory_name"));
                    subCatModeDAO.setSubcategory_name_hindi(json_data.getString("subcategory_name_hindi"));
                    subCatModeDAO.setChecked_status(json_data.getString("status"));
                    subCatModeDAO.setImage_path(json_data.getString("image_path"));
                    subCatModeDAOArrayList.add(subCatModeDAO);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subCatModeDAOArrayList;
    }

    public ArrayList<IncidentTypeModeDAO> parseMyIncTypeList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("ads", ListResponse);
        try {


            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                incidentTypeModeDAO = new IncidentTypeModeDAO();
                incidentTypeModeDAO.setId(object.getString("id"));
                incidentTypeModeDAO.setIncident_name(object.getString("incident_name"));
                incidentTypeModeDAO.setIncident_name_hindi(object.getString("incident_name_hindi"));
                incidentTypeModeDAO.setImage_path(object.getString("image_path"));
                incidentTypeModeDAO.setChecked_status(object.getString("checked_status"));
                incidentTypeModeDAOArrayList.add(incidentTypeModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return incidentTypeModeDAOArrayList;
    }

    public ArrayList<IncidentSubTypeModeDAO> parseSubIncTypeList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("subcat", ListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                incidentSubTypeModeDAO = new IncidentSubTypeModeDAO();
                incidentSubTypeModeDAO.setId(object.getString("id"));
                incidentSubTypeModeDAO.setIncident_type_id(object.getString("incident_type_id"));
                incidentSubTypeModeDAO.setIncident_name(object.getString("incident_name"));
                incidentSubTypeModeDAO.setIncident_name_hindi(object.getString("incident_name_hindi"));
                incidentSubTypeModeDAO.setChecked_status(object.getString("checked_status"));
                incidentSubTypeModeDAO.setImage_path(object.getString("image_path"));
                incidentSubTypeModeDAOArrayList.add(incidentSubTypeModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return incidentSubTypeModeDAOArrayList;
    }

    public ArrayList<IncidentSubTypeModeDAO> parseUserSubIncTypeList(String ListResponse) {
        // TODO Auto-generated method stub
        Log.d("subcat", ListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(ListResponse);
            for (int i = 0; i < leadJsonObj.length(); i++) {
                JSONObject object = leadJsonObj.getJSONObject(i);
                incidentSubTypeModeDAO = new IncidentSubTypeModeDAO();
                incidentSubTypeModeDAO.setId(object.getString("id"));
                incidentSubTypeModeDAO.setIncident_type_id(object.getString("incident_type_id"));
                incidentSubTypeModeDAO.setIncident_name(object.getString("incident_name"));
                incidentSubTypeModeDAO.setIncident_name_hindi(object.getString("incident_name_hindi"));
                incidentSubTypeModeDAO.setImage_path(object.getString("image_path"));
                incidentSubTypeModeDAOArrayList.add(incidentSubTypeModeDAO);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return incidentSubTypeModeDAOArrayList;
    }
}
