package com.example.fiindmenew;

import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class GlobalVariables extends Application{
   
    public static String ANDROID_DEVICE_ID = "";
    public static String _categoriesUrl = "";
    public static String _categoriesUrlPrefix = "http://";
    public static String _categoriesUrlSuffix = "fiindme/index.php/find/deal";
    //public static String _deviceIp = "23.23.86.89:8080/";
    public static String _deviceIp = "10.0.2.1";
    public static String _emulatorIp = "10.0.2.2";

    public GlobalVariables(){
    	//super();
    }
    @Override
    public void onCreate() {
        // Here you could pull values from a config file in res/raw or somewhere else
        // but for simplicity's sake, we'll just hardcode values        
        super.onCreate();
    }
    
    
    

    public void setDeviceID(Context context){
    	ANDROID_DEVICE_ID = getDeviceID(context);   	
    }
    
    public String getDeviceID(Context context) {
        TelephonyManager manager = 
            (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId;
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //Tablet
             deviceId = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);

        } else {
            //Mobile
             deviceId = manager.getDeviceId();

        }
        return deviceId;
    }
    
    public String getCategoriesUrl() {
        // TODO: Remove before production
        if (!ANDROID_DEVICE_ID.equals("000000000000000")) {
           _categoriesUrl = _categoriesUrlPrefix + _deviceIp + _categoriesUrlSuffix;
        }
        else {
            _categoriesUrl = _categoriesUrlPrefix + _emulatorIp + "/~vgaur/" + _categoriesUrlSuffix;
        }
        return _categoriesUrl;
    }
}