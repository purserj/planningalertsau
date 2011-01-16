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
import android.database.Cursor;
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
	int sid;
	Context ct;
	Utilities utils;
	
	private PlanningAlertsDBHelper dbhelper;
	private SQLiteDatabase db;
	
	private List<AlertItem> alertitems;
	
	public SearchObject(Context ct){
		this.ct = ct;
		dbhelper = new PlanningAlertsDBHelper(ct);
		
	}
	
	public SearchObject(Context ct, int sid){
		this.sid = sid;
		dbhelper = new PlanningAlertsDBHelper(ct);
		alertitems = getSavedResults();
		this.ct = ct;
		utils = new Utilities();
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
		sid = (int)db.insert("searches", null, cv);
		db.close();
		saveSearchResults();
	}
	
	public void saveSearchResults(){
		db = dbhelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		for(int i = 0; i < alertitems.size(); i++){
			final AlertItem item = alertitems.get(i);
			cv = new ContentValues();
			cv.put("result_search_id", sid);
			cv.put("result_title", item.getTitle());
			cv.put("result_description", item.getDescription());
			cv.put("result_date", item.getDate());
			cv.put("result_url", item.getURL());
			cv.put("result_long", item.getLong());
			cv.put("result_lat", item.getLat());
			db.insert("search_results", null, cv);
		}
		db.close();
	}
	
	public List<AlertItem> getSavedResults(){
		alertitems = new ArrayList<AlertItem>();
		db = dbhelper.getReadableDatabase();
		String query = "SELECT * FROM search_results WHERE result_search_id="+Integer.toString(this.sid);
		Cursor cur = db.rawQuery(query, null);
		Log.d("Query", query);
		Log.d("Number of records", Integer.toString(cur.getCount()));
		AlertItem currentItem = null;
		cur.moveToFirst();
		while(cur.isAfterLast() == false){
			currentItem = new AlertItem();
			currentItem.setTitle(cur.getString(2));
			currentItem.setDescription(cur.getString(3));
			currentItem.setGeoPoint(cur.getDouble(7), cur.getDouble(6));
			currentItem.setDate(cur.getString(4));
			currentItem.setURL(cur.getString(5));
			alertitems.add(currentItem);
			cur.moveToNext();
		}
		return alertitems;
	}
	
	public void clearSavedResults(){
		db = dbhelper.getWritableDatabase();
		db.delete("search_results", "result_search_id=?", new String[] {Integer.toString(this.sid)});
	}
	
	public void deleteSearch(){
		db = dbhelper.getWritableDatabase();
		db.delete("search_results", "result_search_id=?", new String[] {Integer.toString(this.sid)});
		db.delete("searches", "search_id=?", new String[] {Integer.toString(sid)});
	}
	
	public List<AlertItem> updateSearch(Context ct){
		db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM searches WHERE search_id = "+this.sid, null);
		cur.moveToFirst();
		String url = utils.buildUrl(cur.getInt(1), cur.getString(2), cur.getString(4), cur.getString(3), ct);
		clearSavedResults();
		alertitems = getResults(url);
		saveSearchResults();
		return alertitems;
	}
}
