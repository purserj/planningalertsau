package com.openaustralia;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
        //adtitle.setText(title);
        String url = "http://www.planningalerts.org.au/" + search;
        Log.d("URL", url);
        RssParser rp = new RssParser(url);
        rp.parse();
        RssFeed feed = rp.getFeed();
        
        try{
        	Log.d("returnedarray", feed.description);
        }catch(NullPointerException e){
        	Log.e("Error", "bugger");
        }
        
        for(int i = 0; i < feed.items.size(); i++){
        	TextView tv = new TextView(alertresults.getContext());
        	tv.setId(100+i);
        	tv.setText(feed.items.get(i).title);
        	alertresults.addView(tv);
        }
	}
}