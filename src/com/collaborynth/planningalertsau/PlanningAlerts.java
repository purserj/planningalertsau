package com.collaborynth.planningalertsau;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PlanningAlerts extends Activity {
    /** Called when the activity is first created. */
	SharedPreferences preferences;
	Button newSearch;
	Button savedSearches;
	String stypeStr;
	
	private PlanningAlertsDBHelper dbhelper;
	private SQLiteDatabase db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        newSearch = (Button) findViewById(R.id.NewSearch);
        savedSearches = (Button) findViewById(R.id.SavedSearches);
        dbhelper = new PlanningAlertsDBHelper(this);
                
        newSearch.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v){
        		final Dialog searchDialog = new Dialog(PlanningAlerts.this);
        		searchDialog.setContentView(R.layout.searchview);
        		searchDialog.setCancelable(true);
        		searchDialog.show();
        		
        		final Spinner stype = (Spinner) searchDialog.findViewById(R.id.SearchTypeSpinner);
        		final EditText ev = (EditText) searchDialog.findViewById(R.id.SearchText);
        		final TextView rt = (TextView) searchDialog.findViewById(R.id.RadiusLabel);
        		final EditText rv = (EditText) searchDialog.findViewById(R.id.RadiusText);
        		final Spinner state = (Spinner) searchDialog.findViewById(R.id.StateSpinner);
        		rv.setEnabled(false);
        		state.setEnabled(false);
        	    stype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	           switch(pos){
        	           case 0:
        	        	   rv.setEnabled(true);
        	        	   state.setEnabled(false);
        	        	   break;
        	           case 1:
        	        	   rv.setEnabled(false);
        	        	   state.setEnabled(true);
        	        	   break;
        	           case 2:
        	        	   rv.setEnabled(false);
        	        	   state.setEnabled(false);
        	        	   break;
        	           case 3:
        	        	   rv.setEnabled(false);
        	        	   state.setEnabled(false);
        	        	   break;
        	           case 4:
        	        	   rv.setEnabled(true);
        	        	   state.setEnabled(false);
        	        	   ev.setEnabled(false);
        	        	   break;
        	           }
        	        }
        	        public void onNothingSelected(AdapterView<?> parent) {
        	        }
        	    });
        		Button results = (Button) searchDialog.findViewById(R.id.ShowResultsButton);
        		results.setOnClickListener(new OnClickListener() {
        			
        			public void onClick(View v){
        				searchDialog.dismiss();
        				Intent resultsIntent = new Intent(v.getContext(),AlertsDisplay.class);
        				resultsIntent.putExtra("value", ev.getText().toString());
        				resultsIntent.putExtra("radius", rv.getText().toString());
        				resultsIntent.putExtra("state", state.getSelectedItem().toString());
        				String stypest = stype.getSelectedItem().toString();
        				if(stypest.equalsIgnoreCase("Address")){
        					resultsIntent.putExtra("type", 1);
        				} else if(stypest.equalsIgnoreCase("Suburb")){
        					resultsIntent.putExtra("type", 2);
        				} else if(stypest.equalsIgnoreCase("Postcode")){
        					resultsIntent.putExtra("type", 3);
        				} else if(stypest.equalsIgnoreCase("Council Area")){
        					resultsIntent.putExtra("type", 4);
        				} else if(stypest.equalsIgnoreCase("Current Location")){
        					resultsIntent.putExtra("type", 5);
        				}
        				startActivityForResult(resultsIntent,0);
        			}
        		});
        	}
        });
        
        savedSearches.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v){
        		Intent savedSearchesIntent = new Intent(v.getContext(),SavedSearchesDisplay.class);
        		startActivityForResult(savedSearchesIntent,0);
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