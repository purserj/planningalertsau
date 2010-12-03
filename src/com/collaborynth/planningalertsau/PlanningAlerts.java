package com.collaborynth.planningalertsau;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PlanningAlerts extends Activity {
    /** Called when the activity is first created. */
	SharedPreferences preferences;
	Button myAlerts;
	Button mySuburb;
	Button myPostCode;
	Button getLocal;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        myAlerts = (Button) findViewById(R.id.MyAlerts);
        mySuburb = (Button) findViewById(R.id.MySuburb);
        myPostCode = (Button) findViewById(R.id.MyPostCode);
        getLocal = (Button) findViewById(R.id.LocalAlerts);
                
        myAlerts.setOnClickListener(new OnClickListener() {
        	
               	public void onClick(View v){
               		String addr = preferences.getString("Address", "");
                    String town = preferences.getString("town", "");
                    String state = preferences.getString("state", "");
                    String pcode = preferences.getString("post_code", "");
                    String radius = preferences.getString("radius", "");
               		if(addr.matches("") || town.matches("") 
               				|| state.matches("") || pcode.matches("")
               				|| radius.matches("")){
               			Toast.makeText(PlanningAlerts.this,
            					"You haven't set your location details. Please make sure they are all set.",
            					Toast.LENGTH_LONG).show();
               			Intent PrefsIntent = new Intent(PlanningAlerts.this, PlanningPreferences.class);
               			startActivity(PrefsIntent);
        			} else {
               			Intent myAlertsIntent = new Intent(v.getContext(),AlertsDisplay.class);
               			myAlertsIntent.putExtra("type", 1);
               			startActivityForResult(myAlertsIntent,0);
               		}
        	}
        });
        
        mySuburb.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v){
        		String town = preferences.getString("town", "");
        		if(town.matches("")){
        			Toast.makeText(PlanningAlerts.this,
        					"You haven't set your Town/Suburb details.",
        					Toast.LENGTH_LONG).show();
        			Intent PrefsIntent = new Intent(PlanningAlerts.this, PlanningPreferences.class);
           			startActivity(PrefsIntent);
        		} else {
        			Intent mySuburbsIntent = new Intent(v.getContext(),AlertsDisplay.class);
        			mySuburbsIntent.putExtra("type",2);
        			startActivityForResult(mySuburbsIntent,0);
        		}
        	}
        });
        
        myPostCode.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		String pcode = preferences.getString("post_code", "");
        		if(pcode.matches("")){
        			Toast.makeText(PlanningAlerts.this,
        					"You haven't set your Postcode",
        					Toast.LENGTH_LONG).show();
        			Intent PrefsIntent = new Intent(PlanningAlerts.this, PlanningPreferences.class);
           			startActivity(PrefsIntent);
        		} else {
        			Intent myPostcodeIntent = new Intent(v.getContext(),AlertsDisplay.class);
        			myPostcodeIntent.putExtra("type",3);
        			startActivityForResult(myPostcodeIntent,0);
        		}
        	}
        });
        
        getLocal.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v){
        		String radius = preferences.getString("radius", "");
        		
        		if(radius.matches("")){
        			Toast.makeText(PlanningAlerts.this,
        					"You haven't set the distance you want the search to encompass",
        					Toast.LENGTH_LONG).show();
        			Intent PrefsIntent = new Intent(PlanningAlerts.this, PlanningPreferences.class);
           			startActivity(PrefsIntent);
        		} else {
        			 final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        			    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
        			        buildAlertMessageNoGps();
        			    } else {
        			    	Intent LocalIntent = new Intent(v.getContext(),AlertsDisplay.class);
        			    	LocalIntent.putExtra("type",4);
        			    	startActivityForResult(LocalIntent,0);
        			    }
        		}
        	}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.Preferences:
			// Launch Preference activity
			Intent i = new Intent(PlanningAlerts.this, PlanningPreferences.class);
			startActivity(i);
			// A toast is a view containing a quick little message for the user.
			Toast.makeText(PlanningAlerts.this,
					"Set your location details.",
					Toast.LENGTH_LONG).show();
			break;
		case R.id.About:
			final Dialog adialog = new Dialog(PlanningAlerts.this);
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

		}
		return true;
	}
    
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       launchGPSOptions(); 
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void launchGPSOptions() {
        final ComponentName toLaunch = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 0);
    }  
        
}