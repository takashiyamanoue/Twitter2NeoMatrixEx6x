
package org.yamalab.twitter2neomatrixex6x;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.yamalab.twitter2neomatrixex6x.R;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

public class TwitterLoginControllerGUI extends AccessoryController {
	private static final String TAG = "TwitterLoginCntrlerGUI";

    private AdkTwitterActivity activity;
    WebView webView;
    TwitterControllerGUI mTwitterControllerGUI;
    Twitter mTwitter;
   RequestToken requestToken;
	TwitterLoginControllerGUI(AdkTwitterActivity hostActivity, TwitterControllerGUI tc) {
        super(hostActivity);
		Log.d(TAG,"TwitterLoginCntrller");
        activity=hostActivity;
        mTwitterControllerGUI=tc;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				final WebView webView = (WebView) findViewById(R.id.map);
//				webView.loadDataWithBaseURL(...);
				webView = (WebView)findViewById(R.id.twitterlogin);
				//リンクをタップしたときに標準ブラウザを起動させない
				webView.setWebViewClient(new WebViewClient());

				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
					webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
				}

			}
		});
		/*
		webView = (WebView)findViewById(R.id.twitterlogin);
		//リンクをタップしたときに標準ブラウザを起動させない
		webView.setWebViewClient(new WebViewClient());

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		*/
    }
    WebSettings webSettings=null;
	/**
	 *
	 */
	private String urlX;
	public void loadUrl(String x) {
		urlX=x;
//		mTwitterControllerGUI.setAccessingWeb(true);

//		WebSettings webSettings = webView.getSettings();
		webSettings = webView.getSettings();
		//?????????????[?U?[???????T?C???C???????B?????s??????
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webSettings.setJavaScriptEnabled(true);
			}
		});
		webView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				if (url != null && url.startsWith(mTwitterControllerGUI.CALLBACK_URL)) {
					String[] urlParameters = url.split("\\?")[1].split("&");

					String oauthToken = "";
					String oauthVerifier = "";

					if (urlParameters[0].startsWith("oauth_token")) {
						oauthToken = urlParameters[0].split("=")[1];
					} else if (urlParameters[1].startsWith("oauth_token")) {
						oauthToken = urlParameters[1].split("=")[1];
					}

					if (urlParameters[0].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[0].split("=")[1];
					} else if (urlParameters[1].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[1].split("=")[1];
					}

//					new setOAuthTask().execute(oauthToken,oauthVerifier);
					activity.sendCommandToService("twitter set OAuth", oauthToken + " " + oauthVerifier);
				}
			}
		});

		new loadUrlTask().execute(x);
	}

	@Override
	protected void onAccesssoryAttached() {
		// TODO Auto-generated method stub
		
	}
	
	private class loadUrlTask extends AsyncTask<String, Void, String> {
		 @Override
	     protected String doInBackground(String... params ) {
	    	Log.d(TAG, "connectTwitterTask.doInBackground - " );
	    	try{
		    	urlX=params[0];
				activity.runOnUiThread(new Runnable() {
					public void run() {
				       webView.loadUrl(urlX);
				       webView.setFocusable(true);
					}
				});
	    	}
	    	catch(Exception e){
	    		Log.d(TAG,"tweetTask error:"+e.toString());
				e.printStackTrace();
				return "error!";
			}
	    	return "ok";
	     }
	     @Override
	     protected void onPostExecute(String result) {
	         super.onPostExecute(result);
//			  mTwitterControllerGUI.setAccessingWeb(false);
			  activity.sendCommandToService("twitter set accessingweb","false");
	    }
	}
		
}

