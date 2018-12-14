package com.example.fiindmenew;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleJSONItemActivity extends Activity {
	// XML node keys
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
	int [] imageArray = {R.drawable.ic_burger,R.drawable.ic_food,R.drawable.ic_pork,R.drawable.ic_steak};
	String deal_id = "";
	String title = "";
	String description = "";
	String category = "";
	TextView tv;
	ImageView img;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_jsonitem);
		addListenerOnButton();
		// getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        title = in.getStringExtra(TAG_TITLE);
        String status = in.getStringExtra(TAG_STATUS);
        deal_id = in.getStringExtra(TAG_DEAL_ID);
        String merchant_id = in.getStringExtra(TAG_MERCHANT_ID);
        String starttime = in.getStringExtra(TAG_STARTTIME);
        String endtime = in.getStringExtra(TAG_ENDTIME);
        description = in.getStringExtra(TAG_DESC);
        int image = Integer.parseInt(in.getStringExtra(TAG_IMAGE));
        category = in.getStringExtra(TAG_CATEGORY);
        
        // Displaying all values on the screen
        TextView lbltitle = (TextView) findViewById(R.id.title);
        TextView lblstatus = (TextView) findViewById(R.id.status);
        TextView lblstarttime = (TextView) findViewById(R.id.starttime);
        TextView lblendtime = (TextView) findViewById(R.id.endtime);
        TextView lblDesc = (TextView) findViewById(R.id.description);
        img = (ImageView)findViewById(R.id.imageView);
        if(category.equals("Food")){
        	img.setImageResource(imageArray[image]);
        }
        else if(category.equals("Auto")){
        	img.setImageResource(R.drawable.ic_auto);
        }
        else if(category.equals("Entertainment")){
        	img.setImageResource(R.drawable.ic_entertainment);
        }
        else {
        	img.setImageResource(R.drawable.ic_beauty);	
        }
        
        lbltitle.setText(title);
        lblstatus.setText(status);
        lblstarttime.setText(starttime);
        lblendtime.setText(endtime);
        lblDesc.setText(description);
        
        tv = (TextView) findViewById(R.id.timeleft);
        long currentTime = System.currentTimeMillis();
        long diff = _converHHMMToMilli(endtime) - _converHHMMToMilli(starttime);
        
        MyCounter counter = new MyCounter(diff,1000,tv);
        counter.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_jsonitem, menu);
		return true;
	}
	
	public void addListenerOnButton() {
		 
		Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
		
 
		btnConfirm.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SingleJSONItemActivity.this, UserConfirmActivity.class);
				intent.putExtra("deal_id", deal_id);
				intent.putExtra(TAG_DESC, description);
				intent.putExtra(TAG_TITLE, title);
				startActivity(intent);
			}
 
		});
	}
	/** Called when the user clicks the Confirm button */
	public void sendConfirm(View view) {
	    // Do something in response to button
		//Intent intent = new Intent(this, ConfirmActivity.class);	
		//startActivity(intent);
	}
	
	private long _converHHMMToMilli(String endTime){
		String [] hhmm = endTime.split(":");
		String h = hhmm[1];
		String m = hhmm[2];
		h = h.replaceAll("\\s","");
		m = m.replaceAll("\\s","");
		int hour = Integer.parseInt(h);
		int min = Integer.parseInt(m);
		long milli = hour * 3600 * 1000;
		milli = milli + (min * 60 * 1000);
		return milli;
		
	}

}
