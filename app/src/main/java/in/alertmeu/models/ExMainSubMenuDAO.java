package in.alertmeu.models;

import java.util.ArrayList;
import java.util.List;


public class ExMainSubMenuDAO {

    public String catMainSubName;
    public List<ExAllMainMenuPriceModeDAO> advertisements = new ArrayList<ExAllMainMenuPriceModeDAO>();

    public ExMainSubMenuDAO(String catMainSubName, List<ExAllMainMenuPriceModeDAO> advertisements) {
        this.catMainSubName = catMainSubName;
        this.advertisements = advertisements;
    }

}
