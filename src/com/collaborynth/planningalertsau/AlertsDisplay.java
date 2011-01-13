package com.collaborynth.planningalertsau;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.graphics.drawable.Drawable;

public class AlertsDisplay extends Activity{
	SharedPreferences preferences;
	private String title;
	private String search;
	TextView adtitle;
	Bundle extras;
	LinearLayout alertresults;
	String streamTitle = "";
	public static List<AlertItem> alertitems = null;
	private SearchObject searchObj;
	
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
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertsdisplay);
        adtitle = (TextView) findViewById(R.id.AlertsDisplayTitle);
        alertresults = (LinearLayout) findViewById(R.id.AlertResults);
        extras = getIntent().getExtras();
        searchObj = new SearchObject(this);
       
        int type = extras.getInt("type");
        switch(type){
        case 1:
        	title = new String("Search By Address");
        	search = "applications.rss?address="+extras.getString("value");
        	search += "&radius="+extras.getString("radius");
        	search = search.replaceAll("\\s", "%20");
        	break;
        case 2:
        	title = new String("Alerts in my Suburb");
        	search = "applications.rss?suburb="+extras.getString("value")+"&state="+extras.getString("state");
        	search = search.replaceAll("\\s", "%20");
        	break;
        case 3:
        	title = new String("Search by postcode");
        	search = "applications.rss?postcode="+extras.getString("value");
        	break;
        case 4:
        	title = new String("Search by Council Area");
        	search = "authorities/"+extras.getString("value")+"/applications.rss";
        	search = search.replaceAll("\\s", "_");
        	search = search.toLowerCase();
        	break;
        case 5:
        	title = new String("Alerts by Location");
        	LocationManager locationManager;
        	LocationListener locationListener;
        	locationListener = new MyLocationListener();
        	String context = Context.LOCATION_SERVICE;
        	locationManager = (LocationManager)getSystemService(context);
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
        	search = "applications.rss?lat="+lat+"&lng="+lng+"&radius="+preferences.getString("radius", "");      	
        }
        searchObj.setSearchValues(extras.getString("value"), extras.getString("state"), extras.getString("radius"), extras.getInt("type"));
        adtitle.setText(title);
        String url = "http://www.planningalerts.org.au/" + search;
        Log.d("URL", url);
        
        alertitems = searchObj.getResults(url);
        for(int i = 0; i < alertitems.size(); i++){
        	final AlertItem item = alertitems.get(i);
        	TextView tvr = new TextView(alertresults.getContext());
        	//Log.d("FinRes", item.getTitle());
        	tvr.setText(Html.fromHtml("<b>"+item.getTitle() + "</b><br />" +item.getDescription()));
        	tvr.setId(100+i);
        	tvr.setOnClickListener(new OnClickListener() {
        		public void onClick(View v){
        			Intent myAlertsIntent = new Intent(v.getContext(),AlertWebDisplay.class);
        			myAlertsIntent.putExtra("type", 1);
        			myAlertsIntent.putExtra("link", item.getURL());
        			startActivityForResult(myAlertsIntent,0);
        		}
        	});
        	tvr.setBackgroundResource(R.drawable.border);
        	tvr.setPadding(5, 5, 5, 5);
        	alertresults.addView(tvr);
        }
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alertsdisplaymenu, menu);
		return true;
	}
	    
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i = null;
		switch (item.getItemId()) 
		{
		case R.id.mapit:
			if(alertitems.size() == 0){
				Toast.makeText(AlertsDisplay.this,
						"Sorry, cannot view the map if there aren't any results",
						Toast.LENGTH_LONG).show();
			} else {
				i = new Intent(AlertsDisplay.this, MapAlertsDisplay.class);
				startActivity(i);
			}
			break;
		case R.id.savesearch:
			searchObj.saveSearch();
			break;
		case R.id.About:
			final Dialog adialog = new Dialog(AlertsDisplay.this);
			adialog.setContentView(R.layout.aboutdialog);
			adialog.setTitle("About PlanningAlertsAU");
			adialog.setCancelable(true);
			TextView tv = (TextView) adialog.findViewById(R.id.AboutText);
			tv.setText(R.string.About);
			Button button = (Button) adialog.findViewById(R.id.CloseDialog);
			button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adialog.dismiss();
			}
			});
			adialog.show();
			break;

	}
		return true;
	}			
}