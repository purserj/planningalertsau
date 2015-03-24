package com.collaborynth.planningalertsau;

import com.google.android.maps.GeoPoint;

public class AlertItem {
	
	private String title;
	private String description;
	private GeoPoint gp;
	public String url;
	public String date;
	public double longt;
	public double lat;
	

	public void AlertItem(){
		
	}
		
	public String getTitle(){
		return this.title;
	}
	
	public GeoPoint getGP(){
		return this.gp;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public Double getLong(){
		return this.longt;
	}
	
	public Double getLat(){
		return this.lat;
	}
	
	public String getURL(){
		return this.url;
	}
	
	public void setTitle(String tstring){
		this.title = tstring;
	}
	
	public void setDescription(String dstring){
		this.description = dstring;
	}
	
	public void setGeoPoint(Double lat, Double lon){
		this.longt = lon;
		this.lat = lat;
		this.gp = new GeoPoint((int)(lat * 1e6),(int)(lon * 1e6));
	}
	
	public void setDate(String dt){
		this.date = dt;
	}
	
	public void setURL(String purl){
		this.url = purl;
	}
}
