package com.example.fiindmenew;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class MerchantCreateDealActivity extends Activity {
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
	static final int STARTTIME_DIALOG_ID = 2;
	static final int ENDTIME_DIALOG_ID = 4;
	private Button btnMSubmit;
	private Button btnMStartTime;
	private Button btnMEditStartTime;
	private Button btnMEndTime;
	private Button btnMEditEndTime;
	int startHour,startMin,endHour,endMin;
	String businessId;
	String desc;
	String disc;
	EditText merchantId;
	EditText editMStartTime;
	EditText editMEndTime;
	EditText mTitle;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_create_deal);
		merchantId = (EditText) findViewById(R.id.merchant_id);
		merchantId.setText("Business ID: 4200");
		setVisibilityOptions();
		addListenerOnButton();
		addListenerOnStartTimeButton();
		addListenerOnEndTimeButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.merchant_create_deal, menu);
		return true;
	}
	
	public void setVisibilityOptions(){
		btnMEditStartTime = (Button) findViewById(R.id.btnMEditStartTime);
		btnMEditEndTime = (Button) findViewById(R.id.btnMEditEndTime);
		editMStartTime = (EditText) findViewById(R.id.editMStartTime);
		editMEndTime = (EditText) findViewById(R.id.editMEndTime);
		btnMEditStartTime.setVisibility(View.INVISIBLE);
		btnMEditEndTime.setVisibility(View.INVISIBLE);
		editMStartTime.setVisibility(View.INVISIBLE);
		editMEndTime.setVisibility(View.INVISIBLE);		
	}
	public void addListenerOnButton() {
		 
		btnMSubmit = (Button) findViewById(R.id.btnMSubmit);
		
 
		btnMSubmit.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				
				businessId = merchantId.getText().toString(); 
				
				EditText description = (EditText) findViewById(R.id.m_description);
				desc = description.getText().toString(); 
				
				disc = "10";
				
				Intent intent = new Intent(MerchantCreateDealActivity.this, MerchantConfirmDealActivity.class);
				editMStartTime = (EditText) findViewById(R.id.editMStartTime);
				editMEndTime = (EditText) findViewById(R.id.editMEndTime);
				mTitle = (EditText) findViewById(R.id.m_title);
				String startTime = editMStartTime.getText().toString();
				String endTime = editMEndTime.getText().toString();
				String title = mTitle.getText().toString();
				intent.putExtra("startTime",startTime);
				intent.putExtra("endTime",endTime);
				intent.putExtra("merchant_id",businessId);
				intent.putExtra("description",desc);
				intent.putExtra("discount",disc);		
				intent.putExtra("title",title);	
				startActivity(intent);
			}
 
		});
 
	}
	public void addListenerOnStartTimeButton() {
		 
		btnMStartTime = (Button) findViewById(R.id.btnMStartTime);
		
 
		btnMStartTime.setOnClickListener(new OnClickListener() {
 
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    final Calendar c = Calendar.getInstance();
			    startHour = c.get(Calendar.HOUR_OF_DAY);
			    startMin = c.get(Calendar.MINUTE);
			    showDialog(STARTTIME_DIALOG_ID);
			}
 
		});
 
	}
	public void addListenerOnEndTimeButton() {
		btnMEndTime = (Button) findViewById(R.id.btnMEndTime);
		
		 
		btnMEndTime.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    final Calendar c = Calendar.getInstance();
			    startHour = c.get(Calendar.HOUR_OF_DAY);
			    startMin = c.get(Calendar.MINUTE);
			    showDialog(ENDTIME_DIALOG_ID);
			}
		});
 
	}
	
	@Override
	 protected Dialog onCreateDialog(int id) {
		switch(id){
		case STARTTIME_DIALOG_ID:
			return new TimePickerDialog(this,
				      startTimeSetListener,
				      startHour,startMin, false);
		case ENDTIME_DIALOG_ID:
			return new TimePickerDialog(this,
				      endTimeSetListener,
				      endHour,endMin,false);
		default:
			return null;
		}		
	}
	
	private TimePickerDialog.OnTimeSetListener startTimeSetListener
	  = new TimePickerDialog.OnTimeSetListener(){

	   @Override
	   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    // TODO Auto-generated method stub
		startHour = hourOfDay;
		startMin  = minute;
		view.setCurrentHour(startHour);
		view.setCurrentMinute(startMin);
		btnMEditStartTime.setVisibility(View.VISIBLE);
		String starttime = pad(startHour) + ":" + pad(startMin);
		editMStartTime.setText(starttime);
		editMStartTime.setVisibility(View.VISIBLE);
		btnMStartTime.setVisibility(View.INVISIBLE);
	   }
	 };
	 
	 private TimePickerDialog.OnTimeSetListener endTimeSetListener
	  = new TimePickerDialog.OnTimeSetListener(){

	   @Override
	   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    // TODO Auto-generated method stub
		   endHour = hourOfDay;
		   endMin  = minute;
		   view.setCurrentHour(endHour);
		   view.setCurrentMinute(endMin);
		   btnMEditEndTime.setVisibility(View.VISIBLE);
		   String endtime = pad(endHour) + ":" + pad(endMin);
		   editMEndTime.setText(endtime);
		   editMEndTime.setVisibility(View.VISIBLE);
		   btnMEndTime.setVisibility(View.INVISIBLE);
		   
	   }
	 };
	 
	 private static String pad(int c) {
			if (c >= 10)
			   return String.valueOf(c);
			else
			   return "0" + String.valueOf(c);
	}	
}
