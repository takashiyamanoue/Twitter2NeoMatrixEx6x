
package org.yamalab.twitter2neomatrixex6x.twitterconnector;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterLoginController {
	static final String TAG = "TwitterLoginController";

    Context context;
    TwitterController mTwitterController;
    Twitter mTwitter;
   RequestToken requestToken;
	TwitterLoginController(TwitterController tc) {
		Log.d(TAG,"TwitterLoginController");
        context=tc.getContext();
        mTwitterController=tc;
//		webView = (WebView)findViewById(R.id.twitterlogin);
    }

	private class setOAuthTask extends AsyncTask<String, Void, String> {
		 @Override
	     protected String doInBackground(String... params ) {
	    	Log.d(TAG, "connectTwitterTask.doInBackground - " );
	    	try{
	    		String token=params[0];
	    		String verifier=params[1];
                setOAuth(token,verifier);
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
			  mTwitterController.setAccessingWeb(false);
	    }
	}
			
	/* */
    protected void setOAuth(String oauthToken, String oauthVerifier) {
		Log.d(TAG,"setOAuth token="+oauthToken+" verifier="+oauthVerifier);

        mTwitter=mTwitterController.twitter;
//		mTwitter=mTwitterController.twitterOauth;
        requestToken=mTwitterController.requestToken;
			AccessToken accessToken = null;
	        if(mTwitter==null) {
	        	Log.d(TAG,"setOAuth activity.twitter==null");
	        	return;
	        }

			try {
				accessToken = mTwitter.getOAuthAccessToken(
						requestToken, oauthVerifier);

		        SharedPreferences pref=context.getSharedPreferences(
		        		  "Twitter_setting",context.MODE_PRIVATE);

		        SharedPreferences.Editor editor=pref.edit();
		        editor.putString("oauth_token",accessToken.getToken());
		        editor.putString("oauth_token_secret",accessToken.getTokenSecret());
		        editor.putString("status","available");

		        editor.commit();
		        
//		        context.showTabContents(R.id.main_tweet_label);
		        //finish();
			} catch (TwitterException e) {
	    		Log.d(TAG,"setOAuth error:"+e.toString());
				e.printStackTrace();
			}
    }
    public void startOAuthTask(String oAuthToken, String oAuthVerifier){
		new setOAuthTask().execute(oAuthToken,oAuthVerifier);
    }
}

