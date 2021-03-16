package in.alertmeu.models;


public class IncidentSubTypeModeDAO {
    private String id;
    private String incident_type_id;
    String incident_name;
    String incident_name_hindi;
    String checked_status;
    String isselected = "";
    String image_path = "";
    private String sequence = "";
    private boolean isSelected = false;

    public IncidentSubTypeModeDAO() {

    }

    public IncidentSubTypeModeDAO(String id, String incident_type_id, String incident_name, String incident_name_hindi, String checked_status, String isselected, String image_path, String sequence, boolean isSelected) {
        this.id = id;
        this.incident_type_id = incident_type_id;
        this.incident_name = incident_name;
        this.incident_name_hindi = incident_name_hindi;
        this.checked_status = checked_status;
        this.isselected = isselected;
        this.image_path = image_path;
        this.sequence = sequence;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncident_type_id() {
        return incident_type_id;
    }

    public void setIncident_type_id(String incident_type_id) {
        this.incident_type_id = incident_type_id;
    }

    public String getIncident_name() {
        return incident_name;
    }

    public void setIncident_name(String incident_name) {
        this.incident_name = incident_name;
    }

    public String getIncident_name_hindi() {
        return incident_name_hindi;
    }

    public void setIncident_name_hindi(String incident_name_hindi) {
        this.incident_name_hindi = incident_name_hindi;
    }

    public String getChecked_status() {
        return checked_status;
    }

    public void setChecked_status(String checked_status) {
        this.checked_status = checked_status;
    }

    public String getIsselected() {
        return isselected;
    }

    public void setIsselected(String isselected) {
        this.isselected = isselected;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}