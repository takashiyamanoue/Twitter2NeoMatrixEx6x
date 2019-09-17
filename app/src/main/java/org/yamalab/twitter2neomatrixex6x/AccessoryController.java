package org.yamalab.twitter2neomatrixex6x;

// import android.app.Activity;

import android.content.res.Resources;
import android.view.View;

// import android.widget.LinearLayout;


public abstract class AccessoryController {

	protected AdkTwitterActivity mHostActivity;

	public AccessoryController(AdkTwitterActivity activity) {
		mHostActivity = activity;
	}

	protected View findViewById(int id) {
		return mHostActivity.findViewById(id);
	}

	protected Resources getResources() {
		return mHostActivity.getResources();
	}

	void accessoryAttached() {
		onAccesssoryAttached();
	}
	abstract protected void onAccesssoryAttached();

}