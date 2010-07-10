package com.collaborynth.planningalertsau;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class AlertsOverlay extends ItemizedOverlay{
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mcontext = null;
	
	public AlertsOverlay(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		}

	public AlertsOverlay(Drawable defaultMarker, Context context) {
		  super(defaultMarker);
		  mcontext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
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
		  return true;
		}
}
