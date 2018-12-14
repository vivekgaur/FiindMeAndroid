package com.example.fiindmenew;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

public class UserFindActivity extends Activity {

	private TextView tvDisplayTime;
	private TimePicker u_start_timePicker;
	int nextMinute = 0;
	private Button btnSearch;
	public static final int TIME_PICKER_INTERVAL=15;
	private int hour;
	private int minute;
	private String zipCode;
	private String category;
	private String provider;
	static final int TIME_DIALOG_ID = 999;
	double lng;
	double lat;
	String inputS = null;
	EditText editText;
	Spinner spinnerCategory = null;
	
	// XML node keys
	static final String KEY_ITEM = "code"; // parent node
	static final String KEY_POSTAL_ID = "postalcode";
	
	//GPSTracker Class
	GPSTracker gps;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_find);
		
		//To Avoid poping up of keyboard due to scrollview
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setCurrentTimeOnView();
		addListenerOnButton();
		setCurrentZipCode();
		addListenerOnSpinnerItemSelection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_find, menu);
		return true;
	}
	
	// display current time
	public void setCurrentTimeOnView() {
	 
		u_start_timePicker = (TimePicker) findViewById(R.id.u_start_timePicker);
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		
		
		u_start_timePicker.setCurrentHour(hour);
		u_start_timePicker.setCurrentMinute(minute);
	 
	}
	
	public void setCurrentZipCode(){
		// create class object
        gps = new GPSTracker(UserFindActivity.this);
        //gps.showSettingsAlert();
		// check if GPS enabled		
        if(gps.canGetLocation()){
        	
        	lat = gps.getLatitude();
        	lng = gps.getLongitude();
        	
        	// \n is for new line
        	//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lng, Toast.LENGTH_LONG).show();	
        }else{
        	// can't get location
        	// GPS or Network is not enabled
        	// Ask user to enable GPS/network in settings
        	gps.showSettingsAlert();
        }
        if((lat!=0.0) && (lng!=0.0))
        	new asynGetZipCode().execute();
        	
        	
	}
	public void parsePopulateListView(String inputStr){
		Log.d("GEONAMES",inputStr);
		XMLParser parser = new XMLParser();
		Document doc = parser.getDomElement(inputStr); // getting DOM element
		ArrayList<String> zipCodeList = new ArrayList<String>();
		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			// adding each child node to HashMap key => value
			String zipCode = parser.getValue(e, KEY_POSTAL_ID);
			// adding HashList to ArrayList
			zipCodeList.add(zipCode);
		}
		editText = (EditText) findViewById(R.id.u_zip_code);
		editText.setText(zipCodeList.get(0));
	}
	public void addListenerOnButton() {
		 
		btnSearch = (Button) findViewById(R.id.btnSearch);
		
 
		btnSearch.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				editText = (EditText) findViewById(R.id.u_zip_code);
				zipCode = editText.getText().toString(); 
				//showDialog(TIME_DIALOG_ID);
				//Toast.makeText(UserFindActivity.this, "TEST", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(UserFindActivity.this, JSONListViewActivity.class);
				//Post the Data from the form
				if(zipCode == null)
					zipCode = new String("94568");
				intent.putExtra("zipCode",zipCode);
				if(category == null)
					category = new String("Food");
				intent.putExtra("category", category);
				hour = u_start_timePicker.getCurrentHour();
				minute = u_start_timePicker.getCurrentMinute();
				intent.putExtra("start_time",hour + ":" +minute);
				//intent.putExtra("minute",minute);
				startActivity(intent);
			}
 
		});
 
	}
	
	public void addListenerOnSpinnerItemSelection() {
		spinnerCategory = (Spinner) findViewById(R.id.u_category);
		spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				category = parent.getItemAtPosition(pos).toString();
			 }

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				category = parent.getItemAtPosition(0).toString();
				
			}
			
				
				
		});
	  }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		id = TIME_DIALOG_ID;
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			//return new CustomTimePickerDialog(this, 
             //                           timePickerListener, hour, minute,false);
			return new CustomTimePickerDialog(this, 
		                                     timePickerListener, hour,CustomTimePickerDialog.getRoundedMinute(Calendar.getInstance().get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL) ,false);
		 
 
		}
		return null;
	}
 
	private CustomTimePickerDialog.OnTimeSetListener timePickerListener = 
            new CustomTimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
 
			// set current time into textview
			//tvDisplayTime.setText(new StringBuilder().append(pad(hour))
			//		.append(":").append(pad(minute)));
 
			// set current time into timepicker
			u_start_timePicker.setCurrentHour(hour);
			u_start_timePicker.setCurrentMinute(minute);
 
		}
	};
 
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	
	private TimePicker.OnTimeChangedListener mStartTimeChangedListener =
				new TimePicker.OnTimeChangedListener() {

					public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
						updateDisplay(view, hourOfDay, minute);          
					}
	};

	private TimePicker.OnTimeChangedListener mNullTimeChangedListener =
			new TimePicker.OnTimeChangedListener() {
		
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			
				}
	};	
	private void updateDisplay(TimePicker timePicker, int hourOfDay, int minute) 
	{ 
		
		nextMinute = getRoundedMinute(minute);
		
		// remove ontimechangedlistener to prevent stackoverflow/infinite loop
		timePicker.setOnTimeChangedListener(mNullTimeChangedListener);
		
		// set minute
		timePicker.setCurrentMinute(nextMinute);
		
		// look up ontimechangedlistener again
		timePicker.setOnTimeChangedListener(mStartTimeChangedListener);

		// update the date variable for use elsewhere in code
		//date.setMinutes(nextMinute);  
	}
	public static int getRoundedMinute(int minute){
        if(minute % TIME_PICKER_INTERVAL != 0){
           int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
           minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
           if (minute == 60)  minute=0;
        }

       return minute;
   }
	
	private class asynGetZipCode extends AsyncTask <Void, Void, String> {
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
			
			String user = "vivekgaur";
	        String GEOURI = "http://api.geonames.org/findNearbyPostalCodes?lat=" + lat + "&lng=" + lng + "&username=" + user;
			HttpGet httpGet = new HttpGet(GEOURI);
			
			try {
				
				HttpResponse response = httpClient.execute(httpGet);
				
				response.getStatusLine().getStatusCode();
				
				HttpEntity entity = response.getEntity();
				//Get the XML content
				inputS = EntityUtils.toString(entity);
				
			
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return inputS;
		}
		
		protected void onPostExecute(String results) {
			
			//Call the parse and populate function of outer JSONListViewActivity class
			UserFindActivity.this.parsePopulateListView(results);
			return;
		}
	}
}
