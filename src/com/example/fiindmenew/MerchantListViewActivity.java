package com.example.fiindmenew;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MerchantListViewActivity extends ListActivity {
	// JSON Node names
	private static final String TAG_DEALS = "";
	private static final String TAG_DEAL_ID = "deal_id";
	private static final String TAG_MERCHANT_ID = "merchant_id_fk";
	private static final String TAG_TITLE = "title";
	private static final String TAG_STARTTIME = "start_time";
	private static final String TAG_ENDTIME = "end_time";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DESC = "description";
	String desc = null;
	InputStream inputS = null;
	String zipCode = null;
	String URI = null;
	
	// deals JSONArray
	JSONArray deals = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonlist_view);
		
		//GET the URI
		GlobalVariables glob = (GlobalVariables)(getApplicationContext());		
      	glob.setDeviceID(MerchantListViewActivity.this);
        URI = glob.getCategoriesUrl();
        
		//AsyncTask for GET and Parsing
		new LongRunningGetIO().execute();
		setItemClickForBtn();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jsonlist_view, menu);
		return true;
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
	
	private void setItemClickForBtn(){
		// selecting single ListView item
     	ListView lv = getListView();
     //ListView lv = (ListView) findViewById(R.id.listview);
     	lv.setOnItemClickListener(new OnItemClickListener() {
     		@Override
     		public void onItemClick(AdapterView<?> parent, View view,
     				int position, long id) {
     					// getting values from selected ListItem
     				String deal_id = ((TextView) view.findViewById(R.id.deal_id)).getText().toString();
     				String merchant_id = ((TextView) view.findViewById(R.id.merchant_id)).getText().toString();
     				//String description = ((TextView) view.findViewById(R.id.description)).getText().toString();
     				String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
     				String status = ((TextView) view.findViewById(R.id.status)).getText().toString();
     				String starttime = ((TextView) view.findViewById(R.id.starttime)).getText().toString();
     				String endtime = ((TextView) view.findViewById(R.id.endtime)).getText().toString();
     				
     				// Starting new intent
     				Intent in = new Intent(MerchantListViewActivity.this,MerchantSingleDealActivity.class);
     				in.putExtra(TAG_DEAL_ID,deal_id);
     				in.putExtra(TAG_MERCHANT_ID, merchant_id);
     				in.putExtra(TAG_DESC, desc);
     				in.putExtra(TAG_STATUS,status);
     				in.putExtra(TAG_TITLE, title);
     				in.putExtra(TAG_STARTTIME, starttime);
     				in.putExtra(TAG_STARTTIME, endtime);
     				startActivity(in);

     			}
     		});	
		
	}
	public void parsePopulateListView(String inputStr){
		
		//variable "inputS" should already be populated by the LongRunningGetIO.doInBackground
		//HashMap for ListView
		ArrayList<HashMap<String, String>> dealList = new ArrayList<HashMap<String, String>>();
		// Creating JSON Parser instance
        JSONParser jParser = new JSONParser();
 
        // getting JSON Array from JSON String
        deals = jParser.getJSONArrayFromStr(inputStr);
        
        try{
        	
        	
            //loop through all the deals    
            for(int i = 0; i < deals.length(); i++){
            	JSONObject c = deals.getJSONObject(i);
            	
            	//Store each JSON item in variable
            	String deal_id = new String("Deal: ") + c.getString(TAG_DEAL_ID);
                String merchant_id = new String(" Merchant: ") + c.getString(TAG_MERCHANT_ID);
                String title = c.getString(TAG_TITLE);
                String starttime = new String("Start Time: ") + extractTime(c.getString(TAG_STARTTIME));
                String endtime = new String(" End Time: ") + extractTime(c.getString(TAG_ENDTIME));
                String status = new String("Status: ") + c.getString(TAG_STATUS);
                desc = c.getString(TAG_DESC);
            	
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();
                
             // adding each child node to HashMap key => value
                map.put(TAG_DEAL_ID, deal_id);
                map.put(TAG_MERCHANT_ID, merchant_id);
                map.put(TAG_TITLE, title);
                map.put(TAG_STARTTIME, starttime);
                map.put(TAG_ENDTIME, endtime);
                map.put(TAG_STATUS, status);
                map.put(TAG_DESC, desc);
 
                // adding HashList to ArrayList
                dealList.add(map);                             
            	
            }
        } catch(JSONException e){
        	e.printStackTrace();
        }
		
        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new SimpleAdapter(this, dealList,
                R.layout.activity_jsonlist_view,
                new String[] { TAG_DEAL_ID, TAG_MERCHANT_ID, TAG_TITLE,TAG_STARTTIME,TAG_ENDTIME,TAG_STATUS }, new int[] {
                        R.id.deal_id, R.id.merchant_id, R.id.title, R.id.starttime,R.id.endtime,R.id.status });
        //Now populate
        setListAdapter(adapter);
     
	}
	
	
	public class LongRunningGetIO extends AsyncTask <Void, Void, String> {
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
	        //pairs.add(new BasicNameValuePair("password","1234"));  

	        
	        

	        
			//HttpContext localContext = new BasicHttpContext();
			//ResponseHandler<String> handler = new BasicResponseHandler();
	        String merchantId = "4200";
	        URI = URI + "/" + "merchant" + "/" + merchantId; 
			HttpGet httpGet = new HttpGet(URI);
			String jsonStr = null;
			try {
				
				HttpResponse response = httpClient.execute(httpGet);
				
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
			MerchantListViewActivity.this.parsePopulateListView(results);
			
			
			}
		}

}

