package com.buthmathearo.articlemanagement.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.buthmathearo.articlemanagement.util.BitmapCache;

public class AppController extends Application {
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static AppController mInstance;
	private static final String TAG = AppController.class.getSimpleName();

	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}
	
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}
	
	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
		}
		return mImageLoader;
	}
	
	public <T> void addToRequestQueue(Request<T> request, String tag) {
		request.setTag(TextUtils.isEmpty(tag)? TAG : tag);
		getRequestQueue().add(request);
	}
	
	public <T> void addToRequestQueue(Request<T> request) {
		getRequestQueue().add(request);
	}
	
	public void cancelPendingRequest(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	
}
