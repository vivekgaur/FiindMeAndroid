package com.example.fiindmenew;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
public class MerchantConfirmDealActivity extends Activity {
	// All static variables
	//static final String URL = "http://api.androidhive.info/pizza/?format=xml";
	//static final String URL = "http://10.0.2.15/~vgaur/confirm.xml";
	
	// JSON Node names
	private static final String TAG_DEALS = "";
	private static final String TAG_DEAL_ID = "deal_id";
	private static final String TAG_MERCHANT_ID = "merchant_id_fk";
	private static final String TAG_TITLE = "title";
	private static final String TAG_STARTTIME = "start_time";
	private static final String TAG_ENDTIME = "end_time";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DESC = "description";
	
	Button btnMBack;
	InputStream inputS;
	String str;
	
	String deal_id;
	String merchant_id;
	String description;
	String title;
	String startTime;
	String endTime;
	String discount;
	String desc = null;
	String status;
	String URI = null;
	//JSON OBJECT
	JSONObject JObj;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_merchant_confirm_deal);
	addListenerOnButton();
	// Get the message from the intent
	//Intent intent = getIntent();
	//message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	//GET the URI
	GlobalVariables glob = (GlobalVariables)(getApplicationContext());		
	glob.setDeviceID(MerchantConfirmDealActivity.this);
	URI = glob.getCategoriesUrl();
	new LongRunningGetIO().execute();
	

}
public void addListenerOnButton() {
	 
	btnMBack = (Button) findViewById(R.id.m_back_button);
	

	btnMBack.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(MerchantConfirmDealActivity.this, MerchantLandingActivity.class);			
			startActivity(intent);
		}

	});

}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
getMenuInflater().inflate(R.menu.user_confirm, menu);
return true;
}

/** Called when the user clicks the Back button */
public void backToList(View view) {
// Do something in response to button
Intent intent = new Intent(this, MerchantLandingActivity.class);
startActivity(intent);
}

public void parsePopulateListView(String inputStr){
	
	Log.v("JSON", inputStr);	
	// Creating JSON Parser instance
    JSONParser jParser = new JSONParser();
    
    //Get JSON Object
    JObj = jParser.getJSONFromStr(inputStr);
    
    //Store each JSON item in variable
	try {
		deal_id = new String("Deal: ") + JObj.getString(TAG_DEAL_ID);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try {
		merchant_id = new String(" Merchant: ") + JObj.getString(TAG_MERCHANT_ID);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try {
		title = JObj.getString(TAG_TITLE);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try {
		startTime = new String("Start Time: ") + extractTime(JObj.getString(TAG_STARTTIME));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try {
		endTime = new String(" End Time: ") + extractTime(JObj.getString(TAG_ENDTIME));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try {
		status = new String("Status: ") + JObj.getString(TAG_STATUS);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    try {
		desc = JObj.getString(TAG_DESC);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    String finalStr = new String("Deal Created");
    finalStr += "\n" + title + "\n";
    finalStr += desc + "\n";
    finalStr += startTime + "\n";
    finalStr += endTime + "\n";
    finalStr += status + "\n";
    TextView text = (TextView) findViewById(R.id.m_confirm_label);
	text.setText(finalStr);
    
    
}

/**Function to extract time from yy-mm-dd hh:mm:ss
 * 
 * @param dateTime
 * @return String with hh::mm
 */
private String extractTime(String dateTime){
	String [] strArray;
	strArray = dateTime.split(" ");
	String strTime;
	int index = strArray[1].lastIndexOf(":");
	strTime = strArray[1].substring(0,index);
	return strTime;		
	
}
private class LongRunningGetIO extends AsyncTask <Void, Void, String> {
	
	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		Intent in = getIntent();
        // Get ZipCode values from previous intent
        merchant_id = in.getStringExtra("merchant_id");
        if(merchant_id == null)
			merchant_id = "id: 4200";
        String [] str = merchant_id.split(":");
        str[1] = str[1].replaceAll(" ","");
        title = in.getStringExtra("title");
        startTime = in.getStringExtra("startTime");
        endTime = in.getStringExtra("endTime");
        description = in.getStringExtra("description");
        discount = in.getStringExtra("discount");
        String quantity = "1";
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

        pairs.add(new BasicNameValuePair("merchant_id_fk",str[1])); 
        pairs.add(new BasicNameValuePair("start_time",startTime)); 
        pairs.add(new BasicNameValuePair("end_time",endTime)); 
        pairs.add(new BasicNameValuePair("title",title)); 
        pairs.add(new BasicNameValuePair("description",description)); 
        pairs.add(new BasicNameValuePair("discount",discount)); 
        pairs.add(new BasicNameValuePair("quantity",quantity)); 
        //pairs.add(new BasicNameValuePair("password","1234"));       
		//HttpContext localContext = new BasicHttpContext();
		//ResponseHandler<String> handler = new BasicResponseHandler();
        
        //String URI = "http://10.0.2.2/~vgaur/fiindme/index.php/find/deal"; 
		HttpPost httpPost = new HttpPost(URI);
		String jsonStr = null;
		try {
			UrlEncodedFormEntity param = new UrlEncodedFormEntity(pairs,HTTP.UTF_8);  
			httpPost.setEntity(param);
			HttpResponse response = httpClient.execute(httpPost);
			
			response.getStatusLine().getStatusCode();
			//text = httpClient.execute(httpGet, handler);
			HttpEntity entity = response.getEntity();
			//Set the input stream
			inputS = entity.getContent();
			//jsonStr = new String(getASCIIContentFromEntity(entity));
			} 
			catch (ClientProtocolException e) {  
				e.printStackTrace();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  catch (Exception e) {
				//Log.("[GET REQUEST]", "Network exception", e);
				return e.getLocalizedMessage();
			}
		BufferedReader streamReader = null;
		try {
			streamReader = new BufferedReader(new InputStreamReader(inputS, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    StringBuilder responseStrBuilder = new StringBuilder();

	    String inputStr;
	    try {
			while ((inputStr = streamReader.readLine()) != null)
			    responseStrBuilder.append(inputStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    jsonStr = responseStrBuilder.toString();
		httpClient.getConnectionManager().shutdown(); 

		return jsonStr;
	}
	
	protected void onPostExecute(String results) {
		//Call the parse and populate function of outer JSONListViewActivity class
		MerchantConfirmDealActivity.this.parsePopulateListView(results);
		
		
		}
	}
}

