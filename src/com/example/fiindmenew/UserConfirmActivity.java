package com.example.fiindmenew;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.fiindmenew.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
public class UserConfirmActivity extends Activity {
	// All static variables
	//static final String URL = "http://api.androidhive.info/pizza/?format=xml";
	static final String URL = "http://10.0.2.15/~vgaur/confirm.xml";
	
	// JSON Node names
	private static final String TAG_DEALS = "";
	private static final String TAG_DEAL_ID = "deal_id";
	private static final String TAG_MERCHANT_ID = "merchant_id_fk";
	private static final String TAG_TITLE = "title";
	private static final String TAG_STARTTIME = "start_time";
	private static final String TAG_ENDTIME = "end_time";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DESC = "description";
	
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_ID = "id";
	static final String KEY_NAME = "name";
	static final String KEY_COST = "cost";
	static final String KEY_DESC = "description";
	static final String KEY_CONFIRM = "confirmation";
	InputStream inputS;
	String str;
	
	String deal_id;
	String description;
	String title;
	String URI;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_user_confirm);
	GlobalVariables glob = (GlobalVariables)(getApplicationContext());
	//HttpContext localContext = new BasicHttpContext();
	//ResponseHandler<String> handler = new BasicResponseHandler();
    //String URI = "http://10.0.2.2/~vgaur/fiindme/index.php/find/deal" + "/" + zipCode; 
  	glob.setDeviceID(UserConfirmActivity.this);
    URI = glob.getCategoriesUrl();
	new LongRunningGetIO().execute();
	
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
Intent intent = new Intent(this, UserFindActivity.class);
//EditText editText = (EditText) findViewById(R.id.edit_message);
//String message = editText.getText().toString();
//intent.putExtra(EXTRA_MESSAGE, message);
startActivity(intent);
}

public void parsePopulateListView(String inputStr){
	String [] confirmStr = inputStr.split(":");
	int index = confirmStr[1].indexOf("}");
	String confirmStr1 = confirmStr[1].substring(0,index);
	String outStr = "Title: " + title + "\n";
	outStr += deal_id + "\n";
	outStr += "Confirm ID: " + confirmStr1 + "\n";
	TextView text = (TextView) findViewById(R.id.confirm_label);
	text.setText(outStr);
	

}


private class LongRunningGetIO extends AsyncTask <Void, Void, String> {
	
	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		Intent in = getIntent();
        // Get ZipCode values from previous intent
        deal_id = in.getStringExtra("deal_id");
        title = in.getStringExtra("title");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

        //pairs.add(new BasicNameValuePair("id",deal_id)); 
        pairs.add(new BasicNameValuePair("staus","SOLD")); 
        //pairs.add(new BasicNameValuePair("password","1234"));       
		//HttpContext localContext = new BasicHttpContext();
		//ResponseHandler<String> handler = new BasicResponseHandler();
        String[] str = deal_id.split(": "); 
        str[1].replaceAll(" ","");
        URI = URI + "/" + str[1]; 
        URI.replaceAll(" ","");
		HttpPut httpPut = new HttpPut(URI);
		String jsonStr = null;
		try {
			UrlEncodedFormEntity param = new UrlEncodedFormEntity(pairs,HTTP.UTF_8);  
			httpPut.setEntity(param);
			HttpResponse response = httpClient.execute(httpPut);
			
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
		UserConfirmActivity.this.parsePopulateListView(results);
		
		
		}
	}
}

