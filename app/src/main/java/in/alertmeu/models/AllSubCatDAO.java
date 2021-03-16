package in.alertmeu.models;

public class AllSubCatDAO {
    private String id;
    private String bc_id;
    private String subcategory_name;
    private String subcategory_name_hindi;
    private String image_path;
    private String status;

    public AllSubCatDAO() {

    }

    public AllSubCatDAO(String id, String bc_id, String subcategory_name, String subcategory_name_hindi, String image_path, String status) {
        this.id = id;
        this.bc_id = bc_id;
        this.subcategory_name = subcategory_name;
        this.subcategory_name_hindi = subcategory_name_hindi;
        this.image_path = image_path;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBc_id() {
        return bc_id;
    }

    public void setBc_id(String bc_id) {
        this.bc_id = bc_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getSubcategory_name_hindi() {
        return subcategory_name_hindi;
    }

    public void setSubcategory_name_hindi(String subcategory_name_hindi) {
        this.subcategory_name_hindi = subcategory_name_hindi;
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
