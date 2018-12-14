package com.example.fiindmenew;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class JSONParser {
	private InputStream JSONStream;
	static JSONObject jObj = null;
	// constructor
	public JSONParser() {
		
	}
	
	public JSONObject getJSONFromStr(String jsonStr) {
		// try parse the string to a JSON object
        try {
            jObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;		
		
	}
	
	/**Need to define this one for JSON without the top Array Names
	 * @param jsonStr
	 * @return JSONArray 
	 */
	public JSONArray getJSONArrayFromStr(String jsonStr) {
		JSONArray jArray = null;
		// try parse the string to a JSON object
        try {
            jArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jArray;		
		
	}
	
	
}
