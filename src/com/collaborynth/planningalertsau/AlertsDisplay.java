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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
        Utilities utils = new Utilities();
        adtitle = (TextView) findViewById(R.id.AlertsDisplayTitle);
        alertresults = (LinearLayout) findViewById(R.id.AlertResults);
        extras = getIntent().getExtras();
        
        int type = extras.getInt("type");
        switch(type){
        case 1:
        	title = new String("Search By Address");
        	break;
        case 2:
        	title = new String("Alerts in my Suburb");
        	break;
        case 3:
        	title = new String("Search by postcode");
        	break;
        case 4:
        	title = new String("Search by Council Area");
        	break;
        case 5:
        	title = new String("Alerts by Location");
        }
        adtitle.setText(title);
        if(extras.getInt("sid") == 0){
        	searchObj = new SearchObject(this);
	        searchObj.setSearchValues(extras.getString("value"), extras.getString("state"), extras.getString("radius"), extras.getInt("type"));
	        
	        String url = utils.buildUrl(extras.getInt("type"),
	        		extras.getString("value"), 
	        		extras.getString("radius"), 
	        		extras.getString("state"),
	        		this);
	        Log.d("URL", url);
	        
	        alertitems = searchObj.getResults(url);
        } else {
        	searchObj = new SearchObject(this, extras.getInt("sid"));
        	alertitems = searchObj.getSavedResults();
        }
        if(alertitems != null && alertitems.size() > 0){
        	updateAlerts();
        } else {
        	final Dialog nDialog = new Dialog(this);
        	nDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	nDialog.setContentView(R.layout.noresultsdialog);
        	nDialog.setTitle("Search Results");
        	nDialog.show();
        	Button closebutt = (Button) nDialog.findViewById(R.id.CloseButton);
        	closebutt.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
					
				}
        		
        	});
        	if(nDialog.isShowing() == false){
        			super.finish();
        	}
        }
        
	}
	
	public void updateAlerts(){
		alertresults.removeAllViewsInLayout();
		for(int i = 0; i < alertitems.size(); i++){
        	final AlertItem item = alertitems.get(i);
        	TextView tvr = new TextView(alertresults.getContext());
        	//Log.d("FinRes", item.getTitle());
        	tvr.setText(Html.fromHtml("<span color=#ffffff><b>"+item.getTitle() + "</b><br />" +item.getDescription()+"</span>"));
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
		if(extras.getInt("sid") == 0){
			inflater.inflate(R.menu.alertsdisplaymenu, menu);
		} else {
			inflater.inflate(R.menu.savedsearchmenu, menu);
		}
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
				MapAlertsDisplay.searchObj = searchObj;
				startActivity(i);
			}
			break;
		case R.id.savesearch:
			searchObj.saveSearch();
			Toast.makeText(AlertsDisplay.this, "Your search has been saved", Toast.LENGTH_LONG).show();
			break;
		case R.id.updatesearch:
			Toast.makeText(AlertsDisplay.this,
					"Updating results",
					Toast.LENGTH_LONG).show();
			alertitems = searchObj.updateSearch(this);
			updateAlerts();
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
		case R.id.Delete:
			searchObj.deleteSearch();
			this.finish();
	}
		return true;
	}			
}