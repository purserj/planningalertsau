package com.openaustralia;

public class AlertItem {
	
	public String title;
	public String description;
	public Double latitude;
	public Double longitude;
	

	public void AlertItem(){
		
	}
		
	public String getTitle(){
		return title;
	}
	
	public Double getLatitude(){
		return latitude;
	}
	
	public Double getLongitude(){
		return longitude;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setTitle(String tstring){
		title = tstring;
	}
	
	public void setDescription(String dstring){
		description = dstring;
	}
	
	public void setLatitude(Double lat){
		latitude = lat;
	}
	
	public void setLongitude(Double lon){
		longitude = lon;
	}
}
