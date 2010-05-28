package com.openaustralia;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import android.os.Bundle;
import android.widget.Button;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;

public class PlanningAlerts extends MapActivity {
    /** Called when the activity is first created. */
	
	private Button locPlanAlert;
	private LocationManager locationManager;
	private MyLocationOverlay myloc;
	private  MapView mymap;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locPlanAlert = (Button) findViewById(R.id.Button_LPA);
        mymap = (MapView) findViewById(R.id.mapview1);
        myloc = new MyLocationOverlay(this, mymap);
        mymap.displayZoomControls(true);
        setContentView(R.layout.main);
        
        initmymap();
        initmyloc();
        
    }
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void initmymap(){
    	
    }
    
    public void initmyloc(){
		myloc.enableMyLocation();
		mymap.getOverlays().add(myloc);
    }
    
    public void OnResume(){
    	myloc.enableMyLocation();
    }
    
    public void OnPause(){
    	myloc.disableMyLocation();
    }
    
    public void getLocalAlerts(){
    	
    	
    }
}