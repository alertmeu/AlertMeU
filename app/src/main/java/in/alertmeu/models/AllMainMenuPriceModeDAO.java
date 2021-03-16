package in.alertmeu.models;


public class AllMainMenuPriceModeDAO {
    private String id;
    private String title;
    private String description;
    private String rate;
    private String link;
    private String image_path;
    private String subbc_id;
    private String maincat_id;
    private String business_user_id;
    private String mainid_pricelist;

    public AllMainMenuPriceModeDAO() {
    }

    public AllMainMenuPriceModeDAO(String id, String title, String description, String rate, String link, String image_path, String subbc_id, String maincat_id, String business_user_id, String mainid_pricelist) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rate = rate;
        this.link = link;
        this.image_path = image_path;
        this.subbc_id = subbc_id;
        this.maincat_id = maincat_id;
        this.business_user_id = business_user_id;
        this.mainid_pricelist = mainid_pricelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSubbc_id() {
        return subbc_id;
    }

    public void setSubbc_id(String subbc_id) {
        this.subbc_id = subbc_id;
    }

    public String getMaincat_id() {
        return maincat_id;
    }

    public void setMaincat_id(String maincat_id) {
        this.maincat_id = maincat_id;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getMainid_pricelist() {
        return mainid_pricelist;
    }
    public void setMainid_pricelist(String mainid_pricelist) {
        this.mainid_pricelist = mainid_pricelist;
    }
}