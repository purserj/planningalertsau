package com.collaborynth.planningalertsau;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SavedSearchesDisplay extends Activity{
	
	public static List<AlertItem> alertitems = null;
	private SearchObject searchObj;
	LinearLayout savedsearches;
	PlanningAlertsDBHelper dbhelper;
	SQLiteDatabase db;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savedsearches);
        savedsearches = (LinearLayout) findViewById(R.id.SavedSearches);
        dbhelper = new PlanningAlertsDBHelper(this);
        db = dbhelper.getReadableDatabase();
        
        final Cursor cur = db.query("searches", null, null, null, null, null, null);
        cur.moveToFirst();
        int i = 0;
        String typestr = null;
        while(cur.isAfterLast() == false){
        	TextView tvr = new TextView(savedsearches.getContext());
        	final int sidstr = cur.getInt(0);
        	final int typeint = cur.getInt(1);
        	
        	switch(typeint){
        		case(1):
        			typestr = "Address";
        			break;
        		case(2):
        			typestr = "Suburb";
        			break;
        		case(3):
        			typestr = "Postcode";
        			break;
        		case(4):
        			typestr = "Council Area";
        			break;
        		case(5):
        			typestr = "Location";
        			break;
        	}
        	tvr.setText(Html.fromHtml("<b>Search Type:"+typestr+"</b><br />"+cur.getString(2)));
        	tvr.setId(100+i);
        	tvr.setBackgroundResource(R.drawable.border);
        	tvr.setPadding(5, 5, 5, 5);
        	tvr.setOnClickListener(new OnClickListener() {
        		public void onClick(View v){
        			Intent alertsIntent = new Intent(v.getContext(),AlertsDisplay.class);
        			alertsIntent.putExtra("sid", sidstr);
        			alertsIntent.putExtra("type", typeint);
        			startActivityForResult(alertsIntent,0);
        		}
        	});
        	Log.i("Title", cur.getString(1));
        	savedsearches.addView(tvr);
        	cur.moveToNext();
        	i++;
        }
        db.close();
	}
	
	public void onResume(){
		super.onResume();
		dbhelper = new PlanningAlertsDBHelper(this);
        db = dbhelper.getReadableDatabase();
		savedsearches.removeAllViews();
		final Cursor cur = db.query("searches", null, null, null, null, null, null);
        cur.moveToFirst();
        int i = 0;
        String typestr = null;
        while(cur.isAfterLast() == false){
        	TextView tvr = new TextView(savedsearches.getContext());
        	final int sidstr = cur.getInt(0);
        	final int typeint = cur.getInt(1);
        	
        	switch(typeint){
        		case(1):
        			typestr = "Address";
        			break;
        		case(2):
        			typestr = "Suburb";
        			break;
        		case(3):
        			typestr = "Postcode";
        			break;
        		case(4):
        			typestr = "Council Area";
        			break;
        		case(5):
        			typestr = "Location";
        			break;
        	}
        	tvr.setText(Html.fromHtml("<b>Search Type:"+typestr+"</b><br />"+cur.getString(2)));
        	tvr.setId(100+i);
        	tvr.setBackgroundResource(R.drawable.border);
        	tvr.setPadding(5, 5, 5, 5);
        	tvr.setOnClickListener(new OnClickListener() {
        		public void onClick(View v){
        			Intent alertsIntent = new Intent(v.getContext(),AlertsDisplay.class);
        			alertsIntent.putExtra("sid", sidstr);
        			alertsIntent.putExtra("type", typeint);
        			startActivityForResult(alertsIntent,0);
        		}
        	});
        	Log.i("Title", cur.getString(1));
        	savedsearches.addView(tvr);
        	cur.moveToNext();
        	i++;
        }
        db.close();
		
	}
}
