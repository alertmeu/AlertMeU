package in.alertmeu.models;


public class MoreIncInfoModel {

    private String id;
    private String incident_id;
    private String user_id;
    private String comments;
    private String image_path;
    private String datetime;
    public MoreIncInfoModel() {

    }

    public MoreIncInfoModel(String id, String incident_id, String user_id, String comments, String image_path, String datetime) {
        this.id = id;
        this.incident_id = incident_id;
        this.user_id = user_id;
        this.comments = comments;
        this.image_path = image_path;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncident_id() {
        return incident_id;
    }

    public void setIncident_id(String incident_id) {
        this.incident_id = incident_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
