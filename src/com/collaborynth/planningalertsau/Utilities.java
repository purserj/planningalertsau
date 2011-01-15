package com.collaborynth.planningalertsau;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class Utilities {
	
	public Utilities(){
		
	}
	
	private class MyLocationListener implements LocationListener 
    {
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, 
            Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
	
	public String buildUrl(int type, String value, String radius, String state, Context ct){
		String url = null;
		String search = null;
		switch(type){
        case 1:
        	search = "applications.rss?address="+value;
        	search += "&radius="+radius;
        	search = search.replaceAll("\\s", "%20");
        	break;
        case 2:
        	search = "applications.rss?suburb="+value+"&state="+state;
        	search = search.replaceAll("\\s", "%20");
        	break;
        case 3:
          	search = "applications.rss?postcode="+value;
        	break;
        case 4:
        	search = "authorities/"+value+"/applications.rss";
        	search = search.replaceAll("\\s", "_");
        	search = search.toLowerCase();
        	break;
        case 5:
        	LocationManager locationManager;
        	LocationListener locationListener;
        	locationListener = new MyLocationListener();
        	String context = Context.LOCATION_SERVICE;
        	locationManager = (LocationManager)ct.getSystemService(context);
        	String provider = null;
        	if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
        		provider = LocationManager.GPS_PROVIDER;
        	} else {
        		provider = LocationManager.NETWORK_PROVIDER;
        	}
        	locationManager.requestLocationUpdates(provider, 1000L, 500.0f, locationListener);
        	Location location = locationManager.getLastKnownLocation(provider);
        	double lat = 0.0;
        	double lng = 0.0;
        	if (location != null)
        	{
        		lat = location.getLatitude();
        		lng = location.getLongitude();
          	}
        	search = "applications.rss?lat="+lat+"&lng="+lng+"&radius="+radius;      	
        }
		url = "http://www.planningalerts.org.au/" + search;
		Log.d("URL", url);
		return url;
	}

}
