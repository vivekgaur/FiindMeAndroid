package com.example.fiindmenew;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MerchantSingleDealActivity extends Activity {
	// JSON Node names
	private static final String TAG_DEALS = "";
	private static final String TAG_DEAL_ID = "deal_id";
	private static final String TAG_MERCHANT_ID = "merchant_id_fk";
	private static final String TAG_TITLE = "title";
	private static final String TAG_STARTTIME = "start_time";
	private static final String TAG_ENDTIME = "end_time";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DESC = "description";
	String deal_id = "";
	String title = "";
	String description = "";
	Button btnConfirm;
	Button btnBack;
	EditText confirmCode;
	TextView statusView;
	InputStream inputS = null;
	boolean request = false;
	String URI = null;
	GlobalVariables glob;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_single_deal);
		btnConfirm = (Button) findViewById(R.id.m_btnConfirm);
		btnBack = (Button) findViewById(R.id.m_btnBack);
		confirmCode = (EditText) findViewById(R.id.m_confirm_code);
		
		//Back button invisible
		btnBack.setVisibility(View.INVISIBLE);
		
		//GET the URI
		glob = (GlobalVariables)(getApplicationContext());		
      	glob.setDeviceID(MerchantSingleDealActivity.this);
        URI = glob.getCategoriesUrl();
		
        //Code confirm button
		addListenerOnConfirmButton();
		// getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        title = in.getStringExtra(TAG_TITLE);
        String status = in.getStringExtra(TAG_STATUS);
        String [] str = status.split(": ");
        str[1].replaceAll(" ","");
        if(!str[1].equals("SOLD")){
        	btnConfirm.setVisibility(View.INVISIBLE);
        	confirmCode.setVisibility(View.INVISIBLE);       	
        }
        deal_id = in.getStringExtra(TAG_DEAL_ID);
        String merchant_id = in.getStringExtra(TAG_MERCHANT_ID);
        String starttime = in.getStringExtra(TAG_STARTTIME);
        String endtime = in.getStringExtra(TAG_ENDTIME);
        description = in.getStringExtra(TAG_DESC);
        
        // Displaying all values on the screen
        TextView lbltitle = (TextView) findViewById(R.id.m_title);
        TextView lblstatus = (TextView) findViewById(R.id.m_status);
        TextView lblstarttime = (TextView) findViewById(R.id.m_starttime);
        TextView lblendtime = (TextView) findViewById(R.id.m_endtime);
        TextView lblDesc = (TextView) findViewById(R.id.m_description);
        TextView lblDealId = (TextView) findViewById(R.id.m_deal_id);
        
        lbltitle.setText(title);
        lblstatus.setText(status);
        lblstarttime.setText(starttime);
        lblstarttime.setText(endtime);
        lblDesc.setText(description);
        lblDealId.setText(deal_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.merchant_single_deal, menu);
		return true;
	}
	
	public void addListenerOnConfirmButton() {
		 btnConfirm.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 new AsyncConfirmCode().execute();
			 }

		 });
	}
	
	/** Called when the user clicks the Back button */
	public void backToList(View view) {
	// Do something in response to button
	Intent intent = new Intent(this, MerchantLandingActivity.class);	
	startActivity(intent);
	}
	
	public void parsePopulateListView(String inputStr){
		if(request){
			// Displaying all values on the screen
	        TextView lbltitle = (TextView) findViewById(R.id.m_title);
	        TextView lblstatus = (TextView) findViewById(R.id.m_status);
	        TextView lblDealId = (TextView) findViewById(R.id.m_deal_id);
	        lbltitle.setText(title);
	        lblstatus.setText("DEAL IS CONFIRMED!");
	        lblDealId.setText(deal_id);
			btnConfirm.setVisibility(View.INVISIBLE);
        	confirmCode.setVisibility(View.INVISIBLE);   
        	btnBack.setVisibility(View.VISIBLE);
		}
		else{
			confirmCode.setText("Invalid Code");
			URI = glob.getCategoriesUrl();			
		}
		
	}
	public class AsyncConfirmCode extends AsyncTask <Void, Void, String> {
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
	        String zipCode = in.getStringExtra("zipCode");
	        
	        List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

	        pairs.add(new BasicNameValuePair("id",zipCode));  
	        String[] str = deal_id.split(": "); 
	        str[1].replaceAll(" ","");
	        String code = ((TextView)findViewById(R.id.m_confirm_code)).getText().toString();
	        URI = URI + "/" + "merchant" + "/" + str[1] + "/" + code; 
			HttpPut httpPut = new HttpPut(URI);
			String jsonStr = null;
			try {
				
				HttpResponse response = httpClient.execute(httpPut);
				
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
			MerchantSingleDealActivity.this.parsePopulateListView(results);
			
			
			}
		}

}
