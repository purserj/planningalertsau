package com.collaborynth.planningalertsau;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AlertWebDisplay extends Activity {
	
	WebView wv = null;
	Bundle extras;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webalertdisplay);
		extras = getIntent().getExtras();
	    String url = extras.getString("link");   
	    wv = (WebView) findViewById(R.id.webview);
	    wv.setWebViewClient(new AlertWebDisplayClient());
	    
	    wv.loadUrl(url);
	}
	
	private class AlertWebDisplayClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
}
