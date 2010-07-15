package com.collaborynth.planningalertsau;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewDebug.IntToString;

public class MapAlertsDisplay extends MapActivity {

	List<AlertItem> alerts = null;
	List<Overlay> overlays = null;
	Double centrelon = null;
	Double centrelan = null;
	MapView map = null;
	MapController mcontroller = null;
	
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsalertdisplay);
        map = (MapView) findViewById(R.id.mapView);
        map.setBuiltInZoomControls(true);
        mcontroller = map.getController();
        List<Overlay> mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.mapindicator);
        AlertsOverlay itemizedoverlay = new AlertsOverlay(drawable);
        alerts = AlertsDisplay.alertitems;
        //Log.d("alerts_num", Integer.toString(alerts.size()));
        mapOverlays.clear();
        for(int i = 0; i < alerts.size(); i++)
        {
        	AlertItem ai = alerts.get(i);
        	OverlayItem ol = new OverlayItem(ai.getGP(), 
        			ai.getTitle(), 
        			ai.getDescription());
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
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void setAlerts(List<AlertItem> als){
    	alerts = als;
    }

}
