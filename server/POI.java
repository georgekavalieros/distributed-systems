import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class POI implements Serializable{
	private String poiName, photo, poi_name;
	private int photosNumber;
	private double lng, lat;
	private ArrayList<String> photos = new ArrayList<>();
	
	
	POI(String poiName, String photo, double lng, double lat, String poi_name){
		this.poi_name = poi_name;
		this.poiName = poiName;
		this.photo = photo;
		this.lat=lat;
		this.lng=lng;
		if(!photo.equalsIgnoreCase("not exists"))
			photos.add(photo);
	}
	
	public String getPhoto() {
		return photo;
	}

	public void setPhotosNumber() {
		photosNumber = 1;
	}
	
	public String getPoiName() {
		return poiName;
	}
	
	public int getPhotosNumber() {
		return photosNumber;
	}

	public void updatePhotosNumber(String photo) {
		if(!photos.contains(photo))
			photosNumber++;
	}
	
	public double getLongitude(){
		return lng;
	}
	
	public double getLatitude(){
		return lat;
	}
	
	public String getPoi(){
		return poi_name;
	}

}
