package com.collaborynth.planningalertsau;

import java.util.ArrayList;
import java.util.List;

public class SearchObject {
	
	private String address;
	private String suburb;
	private String postcode;
	private String council;
	private String radius;
	
	public SearchObject(){
		
	}
	
	public void setAddress(String addr){
		address = addr;
	}
	
	public void setSuburb(String sub){
		suburb = sub;
	}
	
	public void setPostCode(String pcode){
		postcode = pcode;
	}
	
	public void setCouncil(String cle){
		council = cle;
	}
	
	public void setRadius(String rad){
		radius = rad;
	}
	
	public List<AlertItem> getResults(){
		List<AlertItem> alerts = new ArrayList<AlertItem>();
		return alerts;
	}
}
