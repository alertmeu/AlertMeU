package in.alertmeu.models;

public class AllMainCatDAO {
    private String id;
    private String country_code;
    private String currency_sign;
    private String ads_pricing;
    private String discount;
    private String notification_charge;
    private String price_formula;
    private String rounding_scale;
    private String special_message;
    private String special_message_hindi;
    private String category_name;
    private String category_name_hindi;
    private String date_time;
    private String image_path;
    private String status;

    public AllMainCatDAO() {

    }

    public AllMainCatDAO(String id, String country_code, String currency_sign, String ads_pricing, String discount, String notification_charge, String price_formula, String rounding_scale, String special_message, String special_message_hindi, String category_name, String category_name_hindi, String date_time, String image_path, String status) {
        this.id = id;
        this.country_code = country_code;
        this.currency_sign = currency_sign;
        this.ads_pricing = ads_pricing;
        this.discount = discount;
        this.notification_charge = notification_charge;
        this.price_formula = price_formula;
        this.rounding_scale = rounding_scale;
        this.special_message = special_message;
        this.special_message_hindi = special_message_hindi;
        this.category_name = category_name;
        this.category_name_hindi = category_name_hindi;
        this.date_time = date_time;
        this.image_path = image_path;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCurrency_sign() {
        return currency_sign;
    }

    public void setCurrency_sign(String currency_sign) {
        this.currency_sign = currency_sign;
    }

    public String getAds_pricing() {
        return ads_pricing;
    }

    public void setAds_pricing(String ads_pricing) {
        this.ads_pricing = ads_pricing;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getNotification_charge() {
        return notification_charge;
    }

    public void setNotification_charge(String notification_charge) {
        this.notification_charge = notification_charge;
    }

    public String getPrice_formula() {
        return price_formula;
    }

    public void setPrice_formula(String price_formula) {
        this.price_formula = price_formula;
    }

    public String getRounding_scale() {
        return rounding_scale;
    }

    public void setRounding_scale(String rounding_scale) {
        this.rounding_scale = rounding_scale;
    }

    public String getSpecial_message() {
        return special_message;
    }

    public void setSpecial_message(String special_message) {
        this.special_message = special_message;
    }

    public String getSpecial_message_hindi() {
        return special_message_hindi;
    }

    public void setSpecial_message_hindi(String special_message_hindi) {
        this.special_message_hindi = special_message_hindi;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_name_hindi() {
        return category_name_hindi;
    }

    public void setCategory_name_hindi(String category_name_hindi) {
        this.category_name_hindi = category_name_hindi;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
