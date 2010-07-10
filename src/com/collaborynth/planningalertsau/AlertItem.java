package com.collaborynth.planningalertsau;

import com.google.android.maps.GeoPoint;

public class AlertItem {
	
	private String title;
	private String description;
	private GeoPoint gp;
	public String url;
	

	public void AlertItem(){
		
	}
		
	public String getTitle(){
		return title;
	}
	
	public GeoPoint getGP(){
		return gp;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getURL(){
		return url;
	}
	
	public void setTitle(String tstring){
		title = tstring;
	}
	
	public void setDescription(String dstring){
		description = dstring;
	}
	
	public void setGeoPoint(Double lat, Double lon){
		gp = new GeoPoint((int)(lat * 1e6),(int)(lon * 1e6));
	}
	
	public void setURL(String purl){
		url = purl;
	}
}
