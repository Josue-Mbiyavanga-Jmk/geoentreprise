package com.iduyatech.geoentreprise.App;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Memory.Constant;
import com.iduyatech.geoentreprise.Memory.Preferences;
import com.iduyatech.geoentreprise.Utils.GPS;


/**
 * Created by Kevin lukanga on 12/02/2019.
 */


public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public GPS gp=null;


    private static Context applicationContext=null;
    private static volatile Handler applicationHandler = null;
    private static Point displaySize = new Point();
    private static float density = 1;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Stetho.initializeWithDefaults(this);
        Fresco.initialize(this);
        Preferences.initialize(this);
        DatabaseManager.init(this);


        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());
        checkDisplaySize();
        density = AppController.applicationContext.getResources().getDisplayMetrics().density;

    }

    public GPS getGp() {
        return gp;
    }

    public void setGp(GPS gp) {
        this.gp = gp;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager)AppController.applicationContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getSize(displaySize);

                   /* if(android.os.Build.VERSION.SDK_INT < 16) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }*/
                }
            }
        } catch (Exception ignored) {
        }
    }
}
