package com.collaborynth.planningalertsau;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchObject {
	
	private String variable;
	private String state;
	private String radius;
	private int stype;
	
	private PlanningAlertsDBHelper dbhelper;
	private SQLiteDatabase db;
	
	private List<AlertItem> alertitems;
	
	public SearchObject(Context ct){
		dbhelper = new PlanningAlertsDBHelper(ct);
		
	}
	
	public SearchObject(Context ct, int sid){
		dbhelper = new PlanningAlertsDBHelper(ct);
	}
	
	public void setSearchValues(String var, String statestr, String radiusstr, int typeint){
		variable = var;
		state = statestr;
		radius = radiusstr;
		stype = typeint;
	}
	
	public List<AlertItem> getResults(String url){
		try{
        	URL urlc = new URL(url);
        	//Log.d("Url_Query", urlc.getQuery());
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
            //Log.d("Raw", out.toString());
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
        					} else if (name.equalsIgnoreCase("description")){
        						currentitem.setDescription(xpp.nextText());
        					} else if (name.contains("point")){
        						String[] lon_lat = xpp.nextText().split(" ");
        						currentitem.setGeoPoint(Double.parseDouble(lon_lat[0]), 
        								Double.parseDouble(lon_lat[1]));
        					} else if(name.equalsIgnoreCase("link")){
        						currentitem.setURL(xpp.nextText());
        					} else if(name.equalsIgnoreCase("pubDate")){
        						currentitem.setDate(xpp.nextText());
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
        }catch(MalformedURLException e){
        	
        }catch(XmlPullParserException e){
        		Log.e("XmlParserError", e.getMessage());
        }catch(IOException e){
        	
        }
        
        return alertitems;
	}
	
	public void saveSearch(){
		db = dbhelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("search_type", Integer.toString(stype));
		cv.put("search_value", variable);
		cv.put("search_state", state);
		cv.put("search_radius", radius);
		db.insert("searches", null, cv);
		
		for(int i = 0; i < alertitems.size(); i++){
		}
	}
	
	public List<AlertItem> getSavedResults(int sid){
		return alertitems;
	}
}
