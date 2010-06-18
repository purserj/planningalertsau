package com.openaustralia;

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
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.ContentValues;



public class AlertsDisplay extends Activity{
	SharedPreferences preferences;
	private String title;
	private String search;
	TextView adtitle;
	Bundle extras;
	LinearLayout alertresults;
	String streamTitle = "";
	
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
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        List<AlertItem> alertitems = null;
        int type = extras.getInt("type");
        switch(type){
        case 1:
        	title = new String("My Local Alerts");
        	String address = preferences.getString("Address", "n/a") + ",";
        	address += preferences.getString("town", "") + ",";
        	address += preferences.getString("state", "n/a") + ",";
        	address += preferences.getString("post_code", "");
        	address = address.replaceAll("\\s", "%20");
        	Log.d("Address", address);
        	search = "applications.rss?address="+address;
        	search += "&radius="+preferences.getString("radius", "n/a");
        	break;
        case 2:
        	title = new String("Alerts in my Suburb");
        	search = "applications.rss?suburb="+preferences.getString("town", "n/a")+"&state="+preferences.getString("state", "");
        	break;
        case 3:
        	title = new String("Alerts in my Postcode");
        	search = "applications.rss?postcode="+preferences.getString("post_code", "n/a");
        	break;
        case 4:
        	title = new String("Alerts by Location");
        	LocationManager locationManager;
        	LocationListener locationListener;
        	locationListener = new MyLocationListener();
        	String context = Context.LOCATION_SERVICE;
        	locationManager = (LocationManager)getSystemService(context);
        	String provider = LocationManager.GPS_PROVIDER;
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
        adtitle.setText(title);
        String url = "http://www.planningalerts.org.au/" + search;
        Log.d("URL", url);
        
        try{
        	URL urlc = new URL(url);
        	Log.d("Url_Query", urlc.getQuery());
        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        	factory.setNamespaceAware(true);
        	XmlPullParser xpp = factory.newPullParser();
        	xpp.setInput(urlc.openStream(), null);
        	InputStream input = urlc.openStream();
        	StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = input.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            Log.d("Raw", out.toString());
        	int eventType = xpp.getEventType();
        	int inItem = 0;
        	AlertItem currentitem = null;
        	while (eventType != XmlPullParser.END_DOCUMENT) {
        		
        		String name = "";
        		String title = "";
        		switch(eventType){
        			case XmlPullParser.START_DOCUMENT:
        				alertitems = new ArrayList<AlertItem>();
        				break;
        			case XmlPullParser.START_TAG:
        				name = xpp.getName();
        				if(name.equalsIgnoreCase("item")){
        					currentitem = new AlertItem();
        				} else if(currentitem != null){
        					if(name.equalsIgnoreCase("title")){
        						title = xpp.nextText();
        						currentitem.setTitle(title);
        						Log.d("Res", title);
        					} else if (name.equalsIgnoreCase("description")){
        						currentitem.setDescription(xpp.nextText());
        					}
        				}
        				break;
        			case XmlPullParser.END_TAG:
        				name = xpp.getName();
        				if(name.equalsIgnoreCase("item")){
        					alertitems.add(currentitem);

        				}
        				break;
        		}
        		eventType = xpp.next();
        	}
        	Log.i("Number of Alerts", Integer.toString(alertitems.size()));
        	for(int i = 0; i < alertitems.size(); i++){
        		AlertItem item = alertitems.get(i);
        		TextView tvr = new TextView(alertresults.getContext());
        		Log.d("FinRes", item.getTitle());
        		tvr.setText(Html.fromHtml("<b>"+item.getTitle() + "</b><br />" +item.getDescription()));
        		tvr.setId(100+i);
        		alertresults.addView(tvr);
        	}
        	
        }catch(MalformedURLException e){
        	
        }catch(XmlPullParserException e){
        		Log.e("XmlParserError", e.getMessage());
        }catch(IOException e){
        	
        }
        try{
        	//Log.d("returnedarray", feed.description);
        }catch(NullPointerException e){
        	Log.e("Error", "bugger");
        }
	}
}