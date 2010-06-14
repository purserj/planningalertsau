package com.openaustralia;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.ContentValues;

import com.openaustralia.RssParser.RssFeed;;


public class AlertsDisplay extends Activity{
	SharedPreferences preferences;
	private String title;
	private String search;
	TextView adtitle;
	Bundle extras;
	LinearLayout alertresults;
	String streamTitle = "";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertsdisplay);
        adtitle = (TextView) findViewById(R.id.AlertsDisplayTitle);
        alertresults = (LinearLayout) findViewById(R.id.AlertResults);
        extras = getIntent().getExtras();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
        	search = "";
        	break;
        case 3:
        	title = new String("Alerts in my Postcode");
        	search = "";
        	break;
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
        	int eventType = xpp.getEventType();
        	int inItem = 0;
        	while (eventType != XmlPullParser.END_DOCUMENT) {
        		int i = 0;
        		Log.d("eventcount", Integer.toString(i));
        		Log.d("eventType", Integer.toString(eventType));
        		String title = "";
        		if(eventType == XmlPullParser.START_DOCUMENT){
        			
        		}else if(eventType == XmlPullParser.START_TAG){
        			String name = xpp.getName();
        			Log.d("XmlParser", name);
        			if(name.equalsIgnoreCase("item")){
        				inItem = 1;
        			}else if(name.equalsIgnoreCase("title")){
        				title = xpp.getText();
        				//Log.d("XmlParser", xpp.getText());
        			}	
        		}
        		eventType = xpp.next();
        		i++;
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