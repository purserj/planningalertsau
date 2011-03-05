package com.collaborynth.planningalertsau;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapAlertsDisplay extends MapActivity {

	List<AlertItem> alerts = null;
	List<Overlay> overlays = null;
	Double centrelon = null;
	Double centrelan = null;
	MapView map = null;
	MapController mcontroller = null;
	Projection projection = null;
	public static SearchObject searchObj;
	
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsalertdisplay);
        map = (MapView) findViewById(R.id.mapView);
        map.setBuiltInZoomControls(true);
        mcontroller = map.getController();
        projection = map.getProjection();
        List<Overlay> mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.mapindicator);
        Drawable threemonthsindicator = this.getResources().getDrawable(R.drawable.mapindicator3months);
        threemonthsindicator.setBounds(0,0, 
        		threemonthsindicator.getIntrinsicWidth(),
        		threemonthsindicator.getIntrinsicHeight());
        Drawable sixmonthsindicator = this.getResources().getDrawable(R.drawable.mapindicator6months);
        sixmonthsindicator.setBounds(0,0, 
        		sixmonthsindicator.getIntrinsicWidth(),
        		sixmonthsindicator.getIntrinsicHeight());
        AlertsOverlay itemizedoverlay = new AlertsOverlay(drawable);
        alerts = AlertsDisplay.alertitems;
        //Log.d("alerts_num", Integer.toString(alerts.size()));
        mapOverlays.clear();
        for(int i = 0; i < alerts.size(); i++)
        {
        	AlertItem ai = alerts.get(i);
        	OverlayItem ol = new OverlayItem(ai.getGP(), 
        			ai.getTitle(), 
        			ai.getDate() + "\n" + ai.getDescription());
        	switch(dateDifference(Date.parse(ai.getDate()))){
        	case 1:
        		break;
        	case 2:
        		ol.setMarker(threemonthsindicator);
        		break;
        	case 3:
        		ol.setMarker(sixmonthsindicator);
        		break;
        	}
        	itemizedoverlay.addOverlay(ol);
        	if(i == 0){
        		mcontroller.animateTo(ai.getGP());
        		mcontroller.setZoom(17);
        	}
        	//Log.d("number of overlays", Integer.toString(itemizedoverlay.size()));
        	//Log.d("GeoPoint", Integer.toString(ai.getGP().getLatitudeE6()));
        	//Log.d("GeoPoint", Integer.toString(ai.getGP().getLongitudeE6()));
        }

        mapOverlays.add(itemizedoverlay);
        //Log.d("number of overlays", Integer.toString(mapOverlays.size()));
    }
	
	private int dateDifference(long date){
		Date dte = new Date();
		Long diff = new Long(dte.getTime() - date);
		Long onemonth = new Long("2628000000");
		Long threemonths = new Long("7884000000");
		Long sixmonths = new Long("15768000000");
		int retValue = 0;
		if(diff.compareTo(onemonth) == 0 || 
				diff.compareTo(onemonth) == -1){
			retValue = 1;
		} else if(diff.compareTo(onemonth) == 1 &&
				diff.compareTo(threemonths) == 0 || 
				diff.compareTo(threemonths) == 1 &&
				diff.compareTo(sixmonths) == -1){
			retValue = 2;
		} else if(diff.compareTo(sixmonths) == 0 ||
				diff.compareTo(sixmonths) == 1){
			retValue = 3;
		}
		return retValue;
	}
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void setAlerts(List<AlertItem> als){
    	alerts = als;
    }

    public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapalertsmenu, menu);
		return true;
	}
    
    public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i = null;
		switch (item.getItemId()) 
		{
		case R.id.savesearch:
			searchObj.saveSearch();
			Toast.makeText(MapAlertsDisplay.this, "Your search has been saved", Toast.LENGTH_LONG).show();
			break;
		case R.id.About:
			final Dialog adialog = new Dialog(MapAlertsDisplay.this);
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
		case R.id.BuildPath:
			buildPath();
			break;
		}
		return true;
	}			
    
    private void buildPath(){
    	// connect to map web service
    	StringBuilder urlString = new StringBuilder();
    	urlString.append("http://maps.google.com/maps?f=d&hl=en");
    	for(int i = 0; i < alerts.size(); i++){
    		GeoPoint gp = alerts.get(i).getGP();
    		if(i == 0){
    			urlString.append("&saddr=");//from
    			urlString.append( Double.toString((double)gp.getLatitudeE6()/1.0E6 ));
    			urlString.append(",");
    	    	urlString.append( Double.toString((double)gp.getLongitudeE6()/1.0E6 ));
    		} else if(i == alerts.size()){
    			urlString.append("&daddr=");//to
    	    	urlString.append( Double.toString((double)gp.getLatitudeE6()/1.0E6 ));
    	    	urlString.append(",");
    	    	urlString.append( Double.toString((double)gp.getLongitudeE6()/1.0E6 ));
    		} else {
    			
    		}
    	urlString.append("&ie=UTF8&0&om=0&output=kml");
    	
    	// get the kml (XML) doc. And parse it to get the coordinates(direction route).
    	Document doc = null;
    	HttpURLConnection urlConnection= null;
    	URL url = null;
    	try
    	{ 
    	url = new URL(urlString.toString());
    	urlConnection=(HttpURLConnection)url.openConnection();
    	urlConnection.setRequestMethod("GET");
    	urlConnection.setDoOutput(true);
    	urlConnection.setDoInput(true);
    	urlConnection.connect(); 

    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder();
    	doc = db.parse(urlConnection.getInputStream()); 

    	if(doc.getElementsByTagName("GeometryCollection").getLength()>0)
    	{
    		//String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName();
    		String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getFirstChild().getNodeValue() ;
    		String [] pairs = path.split(" "); 
    		String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude lngLat[1]=latitude lngLat[2]=height
    		// src
    		GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
    		//map.getOverlays().add(new MyOverLay(startGP,startGP,1));
    		GeoPoint gp1;
    		GeoPoint gp2 = startGP; 
    		/*for(int i=1;i<pairs.length;i++) // the last one would be crash
    		{
    			lngLat = pairs[i].split(",");
    			gp1 = gp2;
    			// watch out! For GeoPoint, first:latitude, second:longitude
    			gp2 = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
    			map.getOverlays().add(new MyOverLay(gp1,gp2,2,color));
    			Log.d("xxx","pair:" + pairs[i]);
    		}*/
    		//map.getOverlays().add(new MyOverLay(dest,dest, 3)); // use the default color
    	} 
    	}
    	catch (MalformedURLException e)
    	{
    	e.printStackTrace();
    	}
    	catch (IOException e)
    	{
    	e.printStackTrace();
    	}
    	catch (ParserConfigurationException e)
    	{
    	e.printStackTrace();
    	}
    	catch (SAXException e)
    	{
    	e.printStackTrace();
    	}
    	}
    }
    
    private class AlertsOverlay extends ItemizedOverlay{
    	
    	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    	Context mcontext = null;
    	
    	public AlertsOverlay(Drawable defaultMarker) {
    		  super(boundCenterBottom(defaultMarker));
    		}

    	public AlertsOverlay(Drawable defaultMarker, Context context) {
    		  super(boundCenterBottom(defaultMarker));
    		  mcontext = context;
    	}

    	@Override
    	protected OverlayItem createItem(int i) {
    		// TODO Auto-generated method stub
    		OverlayItem oitem = mOverlays.get(i);
    		return oitem;
    	}

    	@Override
    	public int size() {
    		// TODO Auto-generated method stub
    		return mOverlays.size();
    	}
    	
    	public void addOverlay(OverlayItem overlay) {
    	    mOverlays.add(overlay);
    	    populate();
    	}
    	
    	protected boolean onTap(int index) {
    		String alert = mOverlays.get(index).getTitle() + "\n"
    						+ mOverlays.get(index).getSnippet();
    		Toast.makeText(getBaseContext(), alert, Toast.LENGTH_LONG).show();
    		return true;
    	}
    }

}
