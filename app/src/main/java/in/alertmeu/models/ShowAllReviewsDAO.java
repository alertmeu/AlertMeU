package in.alertmeu.models;

public class ShowAllReviewsDAO {
    String id = "";
    String user_id = "";
    String business_id = "";
    String rating_star = "";
    String user_review = "";
    String time_stamp = "";
    String first_name = "";
    String last_name = "";
    String mobile_no = "";
    String email_id = "";
    String user_email = "";
    String user_mobile = "";
    String gender = "";
    String fcm_id = "";
    String profilePath = "";
    String rlcnt = "";
    String rdcnt = "";
    String review_status = "";

    public ShowAllReviewsDAO() {
    }

    public ShowAllReviewsDAO(String id, String user_id, String business_id, String rating_star, String user_review, String time_stamp, String first_name, String last_name, String mobile_no, String email_id, String user_email, String user_mobile, String gender, String fcm_id, String profilePath) {
        this.id = id;
        this.user_id = user_id;
        this.business_id = business_id;
        this.rating_star = rating_star;
        this.user_review = user_review;
        this.time_stamp = time_stamp;
        this.first_name = first_name;
        this.last_name = last_name;
        this.mobile_no = mobile_no;
        this.email_id = email_id;
        this.user_email = user_email;
        this.user_mobile = user_mobile;
        this.gender = gender;
        this.fcm_id = fcm_id;
        this.profilePath = profilePath;
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

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getRating_star() {
        return rating_star;
    }

    public void setRating_star(String rating_star) {
        this.rating_star = rating_star;
    }

    public String getUser_review() {
        return user_review;
    }

    public void setUser_review(String user_review) {
        this.user_review = user_review;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getRlcnt() {
        return rlcnt;
    }

    public void setRlcnt(String rlcnt) {
        this.rlcnt = rlcnt;
    }

    public String getRdcnt() {
        return rdcnt;
    }

    public void setRdcnt(String rdcnt) {
        this.rdcnt = rdcnt;
    }

    public String getReview_status() {
        return review_status;
    }

    public void setReview_status(String review_status) {
        this.review_status = review_status;
    }
}
