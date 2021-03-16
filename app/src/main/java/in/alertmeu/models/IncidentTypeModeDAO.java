package in.alertmeu.models;

public class IncidentTypeModeDAO {

    private String id;
    private String incident_name;
    private String incident_name_hindi;
    private String image_path;
    private String checked_status;
    private boolean isSelected = false;


    public IncidentTypeModeDAO() {
    }

    public IncidentTypeModeDAO(String id, String incident_name, String incident_name_hindi, String image_path, String checked_status, boolean isSelected) {
        this.id = id;
        this.incident_name = incident_name;
        this.incident_name_hindi = incident_name_hindi;
        this.image_path = image_path;
        this.checked_status = checked_status;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getChecked_status() {
        return checked_status;
    }

    public void setChecked_status(String checked_status) {
        this.checked_status = checked_status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return incident_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MainCatModeDAO) {
            IncidentTypeModeDAO c = (IncidentTypeModeDAO) obj;
            if (c.getIncident_name().equals(incident_name) && c.getId() == id) return true;
        }

        return false;
    }

}