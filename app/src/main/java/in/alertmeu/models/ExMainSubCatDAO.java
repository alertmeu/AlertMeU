package in.alertmeu.models;

import java.util.ArrayList;
import java.util.List;


public class ExMainSubCatDAO {

    public String catMainSubName;
    public String mainCatId;
    public List<ExAdvertisementDAO> advertisements = new ArrayList<ExAdvertisementDAO>();

    public ExMainSubCatDAO(String catMainSubName,String mainCatId, List<ExAdvertisementDAO> advertisements) {
        this.catMainSubName = catMainSubName;
        this.advertisements = advertisements;
        this.mainCatId = mainCatId;
    }

}
