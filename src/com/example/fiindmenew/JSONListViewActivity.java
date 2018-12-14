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
import org.apache.http.client.utils.URLEncodedUtils;
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
import java.util.LinkedList;
import java.util.List;

public class JSONListViewActivity extends ListActivity {
	// JSON Node names
	private static final String TAG_DEALS = "";
	private static final String TAG_DEAL_ID = "deal_id";
	private static final String TAG_MERCHANT_ID = "merchant_id_fk";
	private static final String TAG_TITLE = "title";
	private static final String TAG_STARTTIME = "start_time";
	private static final String TAG_ENDTIME = "end_time";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DESC = "description";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_IMAGE = "imageView";
	private static final int IMG_FOOD = 1;
	private static final int IMG_AUTO = 2;
	private static final int IMG_ENTERTAINMENT = 3;
	private static final int IMG_BEAUTY = 4;
	int random = 0;
	int [] imageArray = {R.drawable.ic_burger,R.drawable.ic_food,R.drawable.ic_pork,R.drawable.ic_steak};
	String desc = null;
	InputStream inputS = null;
	String zipCode = null;
	String category = null;
	String start_time = null;
	String URI = null;
	String img = null;
	
	// deals JSONArray
	JSONArray deals = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonlist_view);
		GlobalVariables glob = (GlobalVariables)(getApplicationContext());
		//HttpContext localContext = new BasicHttpContext();
		//ResponseHandler<String> handler = new BasicResponseHandler();
        //String URI = "http://10.0.2.2/~vgaur/fiindme/index.php/find/deal" + "/" + zipCode; 
      	glob.setDeviceID(JSONListViewActivity.this);
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
     				Intent in = new Intent(getApplicationContext(), SingleJSONItemActivity.class);
     				in.putExtra(TAG_DEAL_ID,deal_id);
     				in.putExtra(TAG_MERCHANT_ID, merchant_id);
     				in.putExtra(TAG_DESC, desc);
     				in.putExtra(TAG_STATUS,status);
     				in.putExtra(TAG_TITLE, title);
     				in.putExtra(TAG_STARTTIME, starttime);
     				in.putExtra(TAG_ENDTIME, endtime);
     				in.putExtra(TAG_CATEGORY, category);
     				in.putExtra(TAG_IMAGE, Integer.toString(random));
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
        
        if(deals == null)
        	return;
        
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
                if(category.equals("Food")){
                	random = (int )(Math.random() * 3);
                	img = Integer.toString(imageArray[random]);
                	map.put(TAG_IMAGE,img);
                }
                else if(category.equals("Auto")){
                	map.put(TAG_IMAGE,Integer.toString(R.drawable.ic_auto));               	
                }
                else if(category.equals("Entertainment")){
                	map.put(TAG_IMAGE,Integer.toString(R.drawable.ic_entertainment));               	
                }
                else {
                	map.put(TAG_IMAGE,Integer.toString(R.drawable.ic_beauty));
                	
                }
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
                new String[] { TAG_DEAL_ID, TAG_MERCHANT_ID, TAG_TITLE,TAG_STARTTIME,TAG_ENDTIME,TAG_DESC,TAG_IMAGE}, new int[] {
                        R.id.deal_id, R.id.merchant_id, R.id.title, R.id.starttime,R.id.endtime,R.id.status,R.id.imageView });
        //Now populate
        setListAdapter(adapter);
     
	}
	
	
	private class LongRunningGetIO extends AsyncTask <Void, Void, String> {
		

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
	        zipCode = in.getStringExtra("zipCode");
	        category = in.getStringExtra("category");
	        start_time = in.getStringExtra("start_time");
	        List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

	        //pairs.add(new BasicNameValuePair("id",zipCode));  
	        //pairs.add(new BasicNameValuePair("password","1234"));  
	        
	      	//GlobalVariables glob = (GlobalVariables)(getApplicationContext());
			//HttpContext localContext = new BasicHttpContext();
			//ResponseHandler<String> handler = new BasicResponseHandler();
	        //URI = URI + "/" + zipCode;
	        URI = addLocationToUrl(URI);
	      	//glob.setDeviceID(JSONListViewActivity.this);
	        //String URI = glob.getCategoriesUrl();
	     
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
			JSONListViewActivity.this.parsePopulateListView(results);
			
			
			}
		}
		
		protected String addLocationToUrl(String url){
			if(!url.endsWith("?"))
				url += "?";

			List<NameValuePair> params = new LinkedList<NameValuePair>();

			if(zipCode.equals(""))
				zipCode = "94568";
			params.add(new BasicNameValuePair("id", zipCode));
			params.add(new BasicNameValuePair("category", category));
			params.add(new BasicNameValuePair("start_time", start_time));

			String paramString = URLEncodedUtils.format(params, "utf-8");

			url += paramString;
			return url;
		}

}
