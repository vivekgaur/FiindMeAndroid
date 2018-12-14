package com.example.fiindmenew;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MerchantAlertActivity extends Activity {
	GlobalVariables glob;
	String URI = null;
	String total_deals = null;
	String total_users = null;
	boolean request = false;
	InputStream inputS = null;
	TextView userZ = null;
	TextView userT = null;
	TextView userA = null;

	// JSON Node names
	private static final String TAG_TOTAL_DEALS = "total_deals";
	private static final String TAG_TOTAL_USERS = "total_users";
	private static final String TAG_ZIP_CODE = "zip_code";
	
	//JSON OBJECT
	JSONObject JObj;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_alert);
		userZ = (TextView) findViewById(R.id.m_total_deals);
		userT = (TextView) findViewById(R.id.m_total_users);
		userA = (TextView) findViewById(R.id.m_trend);
		//GET the URI
		glob = (GlobalVariables)(getApplicationContext());		
		glob.setDeviceID(MerchantAlertActivity.this);
		URI = glob.getCategoriesUrl();
		//Button to create a deal
		addListenerOnButton();
		new AsyncGetAlerts().execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.merchant_alert, menu);
		return true;
	}
	
	public void addListenerOnButton() {
		 
		Button btnCreate = (Button) findViewById(R.id.btnCreate);
		
 
		btnCreate.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MerchantAlertActivity.this, MerchantCreateDealActivity.class);				
				startActivity(intent);
			}
 
		});
	}
	public void parsePopulateListView(String inputStr){
		// Creating JSON Parser instance
	    JSONParser jParser = new JSONParser();
	    
	    //Get JSON Object
	    JObj = jParser.getJSONFromStr(inputStr);
	    
	    if(JObj == null)
	    	return;
	    
	    //Store each JSON item in variable
		try {
			total_deals = new String("Total Deals in ") + JObj.getString(TAG_ZIP_CODE) + ": " +  JObj.getString(TAG_TOTAL_DEALS);
			total_users = new String("Total Users in ") + JObj.getString(TAG_ZIP_CODE) + ": " +  JObj.getString(TAG_TOTAL_USERS);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userZ.setText(total_deals);
		userT.setText(total_users);
		userA.setText("Trend : HOT");
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(50); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		userA.startAnimation(anim);

	}
	
	public class AsyncGetAlerts extends AsyncTask <Void, Void, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n>0) {
				byte[] b = new byte[4096];
				n =  in.read(b);
				if (n>0) out.append(new String(b, 0, n));
			}
			return out.toString();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			Intent in = getIntent();
	        // Get ZipCode values from previous intent
	        String merchant_id = in.getStringExtra("merchant_id");
	        
	        List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

	        pairs.add(new BasicNameValuePair("id",merchant_id));  
	        
	        URI = URI + "/" + "total" + "/" + merchant_id; 
			HttpGet httpGet = new HttpGet(URI);
			String jsonStr = null;
			try {
				
				HttpResponse response = httpClient.execute(httpGet);
				
				switch(response.getStatusLine().getStatusCode()){
					case 200:
					case 201:
						request = true;
						break;
					case 400:
					case 501:
						request = false;
						break;
				}
				
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
			MerchantAlertActivity.this.parsePopulateListView(results);
			
			
			}
		}

}
