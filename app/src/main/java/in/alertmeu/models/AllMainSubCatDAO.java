package in.alertmeu.models;

public class AllMainSubCatDAO {
    private String id;
    private String user_id;
    private String subbc_id;
    private String maincat_id;
    private String status;

    public AllMainSubCatDAO() {

    }

    public AllMainSubCatDAO(String id, String user_id, String subbc_id, String maincat_id, String status) {
        this.id = id;
        this.user_id = user_id;
        this.subbc_id = subbc_id;
        this.maincat_id = maincat_id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
