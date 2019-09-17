package org.yamalab.twitter2neomatrixex6x.twitterconnector;

//import org.yamalab.android.twitter2neomatrixex1.twitterconnector.Tweet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.yamalab.twitter2neomatrixex6x.service.SdLog;
import org.yamalab.twitter2neomatrixex6x.service.Util;

import java.util.StringTokenizer;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/*
 * TwitterControllerGUI   Arduino    AdkThread   AdkService     TwitterController
            ------------------------------------------>-------------> Twitter Login, name
         ��� <-----------------------------------------<--------------  set CallbackURL
           <--------------------------------------------------------  loadUrlTask 
         [open WebView]
 
            ------------------------------------------>-------------> Another Twitter Login, name
         ��� <-----------------------------------------<--------------  set CallbackURL
           <--------------------------------------------------------  loadUrlTask 
         [open WebView]
 
                                        ------------->------------> getNewTweet
            <----------------------------------------<------------- getDownloadHashTag
            ----------------------------------------->-------------> downloadHashTag
                                                                      [getNewTweet from twitter]
                              <----------------------<-------------  message
            <----------------------------------------
            
                                        ------------->-------------> tweet
            <----------------------------------------<-------------- tweet message
                                                                      [tweet to the twitter]
            
            
 */
public class TwitterController  {
	static final String TAG = "TwitterController";
		
	public String CONSUMER_KEY = "4LymT9OFQzI5oKeUcHmJwU9ZX"; // dev.twitter.com ������
	public String CONSUMER_SECRET = "QQedmOElwODBVGekCidFeAAtx1DnkeCk3zOi0F8S4a0XjjlGIz"; // dev.twitter.com ������
	public String CALLBACK_URL = "http://www.yama-lab.org/twitter2NeoMatrix/" ; // "myapp://oauth";
	
	private String nechatterStatus;
	private TwitterApplication service;
	private Context context;
	public RequestToken requestToken = null;
	public Twitter twitter = null;
	private Tweet mTweet = null;
	private TwitterLoginController mTwitterLoginController=null;
//	public OAuthAuthorization twitterOauth;
	private boolean accessingWeb=false;
    /** Called when the activity is first created. */
    private SdLog sdLog;
    
    public TwitterController(Context ct, TwitterApplication svs) {
    	context=ct;
        service=svs;
		Log.d(TAG,"TwitterController");
//        mTwitterController=activity.mTwitterController;
        //������������������������������������������������������������
	    SharedPreferences pref = context.getSharedPreferences("Twitter_setting", context.MODE_PRIVATE);
	    //���������������������K���v������������������������������������������������������
		nechatterStatus  = pref.getString("status", "");
        mTweet = new Tweet(this);
		mTwitterLoginController = new TwitterLoginController(this);
    }

    public void setSdLog(SdLog x){
    	sdLog=x;
	}

    private void connectTwitter() throws TwitterException{
		//���Q���l:http://groups.google.com/group/twitter4j/browse_thread/thread/d18c179ba0d85351
		//���p���������������������������������������������������I
    	setAccessingWeb(true);
		ConfigurationBuilder confbuilder  = new ConfigurationBuilder(); 

		confbuilder.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET); 
		confbuilder.setGZIPEnabled(false);
        if(service==null) {
        	Log.d(TAG,"connectTwitter service==null");
        	return;
        }
//        Configuration configuration = confbuilder.build();
		twitter = new TwitterFactory(confbuilder.build()).getInstance();
//        twitterOauth = new OAuthAuthorization(configuration);

		// requestToken���������N���������X���������������B
		try {
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
//			requestToken = twitterOauth.getOAuthRequestToken(CALLBACK_URL);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"connectTwitter-twitter.getOAuthError "+e.toString());
			e.printStackTrace();
		}catch (Exception e){
			Log.d(TAG,"connectTwitter-twitter.getOAuthError "+e.toString());
			e.printStackTrace();
		}
        if(requestToken==null) {
        	Log.d(TAG,"connectTwitter requestToken==null");
//        	return;
        }

	}

    
	private class connectTwitterTask extends AsyncTask<Void, Void, String> {
		 String resultURL;
		 @Override
	     protected String doInBackground(Void... params ) {
	    	Log.d(TAG, "connectTwitterTask.doInBackground - " );
	    	try{
	    	   connectTwitter();
	    	}
	    	catch(Exception e){
	    		setAccessingWeb(false);
	    		Log.d(TAG,"tweetTask error:"+e.toString());
				e.printStackTrace();
				return "";
			}
    		setAccessingWeb(false);
    		if(requestToken!=null){
    		   String rtn=requestToken.getAuthorizationURL();
	    	   return rtn;
    		}
    		else{
	    		Log.d(TAG,"requestToken was null when connecting twitter. ");
	    		service.parseCommand("activity twitter set loginMessage",
	    				"requestToken was null when connecting the twitter.");
    			return "";
    		}
	     }
	     @Override
	     protected void onPostExecute(String result) {
	         super.onPostExecute(result);
	         resultURL=result;
//			 service.sendCommandToActivity("twitter show label","main_login_label");
	         service.parseCommand("activity twitter loadUrl",resultURL);
	    }
	}
	
    final private boolean isConnected(String nechatterStatus){
		if(nechatterStatus != null && nechatterStatus.equals("available")){
			return true;
		}else{
			return false;
		}
	}
    
    
    public void disconnectTwitter(){
		
        SharedPreferences pref=context.getSharedPreferences("Twitter_setting",context.MODE_PRIVATE);

        SharedPreferences.Editor editor=pref.edit();
        editor.remove("oauth_token");
        editor.remove("oauth_token_secret");
        editor.remove("status");

        editor.commit();
        
        //finish();
	}

	public boolean parseCommand(String x, String v){
		String subcmd= Util.skipSpace(x);
		String[] rest=new String[1];
		String[] match=new String[1];
	   	if(this.service==null) return false;
	   	if(this.mTweet==null) return false;
//		Log.d(TAG,"parseCommand("+x+","+v+")");
		sdLog.put(TAG+" - "+"parseCommand("+x+","+v+")");
		int [] intv = new int[1];
		String tweetMessage="";
		if(v==null) return false;
		if(Util.parseKeyWord(subcmd,"tweet",rest)){
		   	String subsub=Util.skipSpace(rest[0]);
		    SharedPreferences pref = context.getSharedPreferences("Twitter_setting", context.MODE_PRIVATE);
			nechatterStatus  = pref.getString("status", "");
			if(accessingWeb) return true;
			accessingWeb=true;
			String vv=mTweet.getUploadTag()+" "+v;
	   		service.parseCommand("activity twitter tweet",vv);
		   	if(isConnected(nechatterStatus)){
		   	    this.mTweet.tweet(vv);
		   	}
		   	else{
		   		/* */
//					new connectTwitterTask().execute("");
		   		/* */
		   	}
		   	return true;
		}
		else
		if(Util.parseKeyWord(subcmd,"getNewTweet",rest)){
//			this.service.sendCommandToActivity("twitterGUI getDow","option");
		    SharedPreferences pref = context.getSharedPreferences("Twitter_setting", context.MODE_PRIVATE);
			nechatterStatus  = pref.getString("status", "");
			if(accessingWeb) return true;
			accessingWeb=true;
//			mTweet.setDownloadHashTag(v);
		   	if(isConnected(nechatterStatus)){
//		   		if(tweetMessage==null) return false;
		   	    mTweet.getHashTweet();
		   	}
		   	else{
		   		/* */
//					new connectTwitterTask().execute("");
		   		/* */
		   	}
		   	return true;
		}
		else
		if(Util.parseKeyWord(subcmd,"set ",rest)){
		   	String subsub=Util.skipSpace(rest[0]);
		   	if(subsub.equals("uploadHashTag")){
		   		mTweet.setUploadHashTag(v);
		   		service.parseCommand("activity twitter set uploadHashTag", v);
		   		return true;
		   	}
		   	else
		   	if(subsub.equals("downloadHashTag")){
		   		service.parseCommand("activity twitter set downloadHashTag",v);
		   		mTweet.setDownloadHashTag(v);
		   		return true;
		   	}
		   	else
		   	if(subsub.equals("OAuth")){
		   		StringTokenizer st=new StringTokenizer(v);
		   		String oAuthToken=st.nextToken();
		   		String oAuthVerifier=st.nextToken();
		   		mTwitterLoginController.startOAuthTask(oAuthToken,oAuthVerifier);

		   		return true;
		   	}
		   	else
		   	if(subsub.equals("accessingweb")){
		   		if(v.equals("true")){
		   			
		   		}
		   		else
		   		if(v.equals("false")){
		   			this.setAccessingWeb(false);		   			
		   		}

		   		return true;
		   	}
		   	
		   	return false;
		}
		else
		if(subcmd.equals("login")){
			loginTwitter();
		}
		else
		if(subcmd.equals("logout")){
			this.disconnectTwitter();
		}
		return false;
	}
	private void loginTwitter(){
		if(isConnected(nechatterStatus)){
			
			//disconnectTwitter();
			/*
			Intent intent2=new Intent();
			intent2.setClassName("org.yamalab.android.AdkTwitter","org.yamalab.android.AdkTwitter.Tweet");
			intent2.setAction(Intent.ACTION_VIEW);
			activity.startActivityForResult(intent2,0);
			*/
//			service.sendCommandToActivity("twitter show label","main_tweet_label");
			service.parseCommand("activity twitter show label", "main_tweet_label");
		}else{
			
			try {
//				connectTwitter();
				new connectTwitterTask().execute();
			} catch (Exception e) {
				//showToast(R.string.nechatter_connect_error);
			}
		}
		
	}

	public void setAccessingWeb(boolean x){
		accessingWeb=x;
	}
	public TwitterApplication getService(){
		return service;
	}
	public Context getContext(){
		return context;
	}

}
