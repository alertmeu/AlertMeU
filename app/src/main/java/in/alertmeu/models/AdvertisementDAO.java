package in.alertmeu.models;

public class AdvertisementDAO {
    String id = "";
    String business_user_id = "";
    String title = "";
    String description = "";
    String describe_limitations = "";
    String rq_code = "";
    String business_main_category_id = "";
    String business_subcategory_id = "";
    String s_time = "";
    String e_time = "";
    String mobile_no = "";
    String business_name = "";
    String business_number="";
    String business_email = "";
    String address = "";
    String company_logo = "";
    String location_name = "";
    String latitude = "";
    String longitude="";
    String original_image_path = "";
    String modify_image_path = "";
    String likecnt = "";
    String dislikecnt = "";
    String main_cat_name="";
    String subcategory_name="";
    String main_cat_name_hindi="";
    String subcategory_name_hindi="";
    String s_date = "";
    String e_date = "";
    String numbers="";
    String sponsor_flag = "";
    String referral_code="";
    String bus_main_cat_name="";
    String bus_subcategory_name="";
    String bus_main_cat_name_hindi="";
    String bus_subcategory_name_hindi="";
    String price_status="";
    private boolean isSelected;

    public AdvertisementDAO() {
    }

    public AdvertisementDAO(String id, String business_user_id, String title, String description, String describe_limitations, String rq_code, String business_main_category_id, String business_subcategory_id, String s_time, String e_time, String mobile_no, String business_name, String business_number, String business_email, String address, String company_logo, String location_name, String latitude, String longitude, String original_image_path, String modify_image_path, String likecnt, String dislikecnt, String numbers, String main_cat_name, String subcategory_name, String bus_main_cat_name, String bus_subcategory_name, boolean isSelected) {
        this.id = id;
        this.business_user_id = business_user_id;
        this.title = title;
        this.description = description;
        this.describe_limitations = describe_limitations;
        this.rq_code = rq_code;
        this.business_main_category_id = business_main_category_id;
        this.business_subcategory_id = business_subcategory_id;
        this.s_time = s_time;
        this.e_time = e_time;
        this.mobile_no = mobile_no;
        this.business_name = business_name;
        this.business_number = business_number;
        this.business_email = business_email;
        this.address = address;
        this.company_logo = company_logo;
        this.location_name = location_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.original_image_path = original_image_path;
        this.modify_image_path = modify_image_path;
        this.likecnt = likecnt;
        this.dislikecnt = dislikecnt;
        this.numbers = numbers;
        this.main_cat_name = main_cat_name;
        this.subcategory_name = subcategory_name;
        this.bus_main_cat_name=bus_main_cat_name;
        this.bus_subcategory_name=bus_subcategory_name;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescribe_limitations() {
        return describe_limitations;
    }

    public void setDescribe_limitations(String describe_limitations) {
        this.describe_limitations = describe_limitations;
    }

    public String getRq_code() {
        return rq_code;
    }

    public void setRq_code(String rq_code) {
        this.rq_code = rq_code;
    }

    public String getBusiness_main_category_id() {
        return business_main_category_id;
    }

    public void setBusiness_main_category_id(String business_main_category_id) {
        this.business_main_category_id = business_main_category_id;
    }

    public String getBusiness_subcategory_id() {
        return business_subcategory_id;
    }

    public void setBusiness_subcategory_id(String business_subcategory_id) {
        this.business_subcategory_id = business_subcategory_id;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public String getE_time() {
        return e_time;
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_number() {
        return business_number;
    }

    public void setBusiness_number(String business_number) {
        this.business_number = business_number;
    }

    public String getBusiness_email() {
        return business_email;
    }

    public void setBusiness_email(String business_email) {
        this.business_email = business_email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOriginal_image_path() {
        return original_image_path;
    }

    public void setOriginal_image_path(String original_image_path) {
        this.original_image_path = original_image_path;
    }

    public String getModify_image_path() {
        return modify_image_path;
    }

    public void setModify_image_path(String modify_image_path) {
        this.modify_image_path = modify_image_path;
    }

    public String getLikecnt() {
        return likecnt;
    }

    public void setLikecnt(String likecnt) {
        this.likecnt = likecnt;
    }

    public String getDislikecnt() {
        return dislikecnt;
    }

    public void setDislikecnt(String dislikecnt) {
        this.dislikecnt = dislikecnt;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getMain_cat_name() {
        return main_cat_name;
    }

    public void setMain_cat_name(String main_cat_name) {
        this.main_cat_name = main_cat_name;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getS_date() {
        return s_date;
    }

    public void setS_date(String s_date) {
        this.s_date = s_date;
    }

    public String getE_date() {
        return e_date;
    }

    public void setE_date(String e_date) {
        this.e_date = e_date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getMain_cat_name_hindi() {
        return main_cat_name_hindi;
    }

    public void setMain_cat_name_hindi(String main_cat_name_hindi) {
        this.main_cat_name_hindi = main_cat_name_hindi;
    }

    public String getSubcategory_name_hindi() {
        return subcategory_name_hindi;
    }

    public void setSubcategory_name_hindi(String subcategory_name_hindi) {
        this.subcategory_name_hindi = subcategory_name_hindi;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSponsor_flag() {
        return sponsor_flag;
    }

    public void setSponsor_flag(String sponsor_flag) {
        this.sponsor_flag = sponsor_flag;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

    public String getBus_main_cat_name() {
        return bus_main_cat_name;
    }

    public void setBus_main_cat_name(String bus_main_cat_name) {
        this.bus_main_cat_name = bus_main_cat_name;
    }

    public String getBus_subcategory_name() {
        return bus_subcategory_name;
    }

    public void setBus_subcategory_name(String bus_subcategory_name) {
        this.bus_subcategory_name = bus_subcategory_name;
    }

    public String getBus_main_cat_name_hindi() {
        return bus_main_cat_name_hindi;
    }

    public void setBus_main_cat_name_hindi(String bus_main_cat_name_hindi) {
        this.bus_main_cat_name_hindi = bus_main_cat_name_hindi;
    }

    public String getBus_subcategory_name_hindi() {
        return bus_subcategory_name_hindi;
    }

    public void setBus_subcategory_name_hindi(String bus_subcategory_name_hindi) {
        this.bus_subcategory_name_hindi = bus_subcategory_name_hindi;
    }

    public String getPrice_status() {
        return price_status;
    }

    public void setPrice_status(String price_status) {
        this.price_status = price_status;
    }
}
