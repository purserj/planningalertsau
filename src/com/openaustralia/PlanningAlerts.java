package com.openaustralia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
        	
        	@Override
        	public void onClick(View v){
        		Intent myAlertsIntent = new Intent(v.getContext(),AlertsDisplay.class);
        		myAlertsIntent.putExtra("type", 1);
        		startActivityForResult(myAlertsIntent,0);
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
					"Here you can maintain your user credentials.",
					Toast.LENGTH_LONG).show();
			break;

		}
		return true;
	}
        
}