package com.example.fiindmenew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MerchantLandingActivity extends Activity {
	private Button btnCreateDeal;
	private Button btnAlerts;
	private Button btnMyDeals;
	private static final String TAG_MERCHANT_ID = "merchant_id";
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_landing);
		addListenerOnCreateDealButton();
		addListenerOnMyDealsButton();
		addListenerOnAlertsButton();
		
	}
	public void addListenerOnCreateDealButton() {
		 
		btnCreateDeal = (Button) findViewById(R.id.btnCreateDeal);
		
 
		btnCreateDeal.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MerchantLandingActivity.this, MerchantCreateDealActivity.class);
				
				startActivity(intent);
			}
 
		});
 
	}
	public void addListenerOnMyDealsButton() {
		 
		btnMyDeals = (Button) findViewById(R.id.btnMyDeals);
		
 
		btnMyDeals.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MerchantLandingActivity.this, MerchantListViewActivity.class);
				
				startActivity(intent);
			}
 
		});
 
	}
	
	public void addListenerOnAlertsButton() {
		 
		btnAlerts = (Button) findViewById(R.id.btnAlerts);
		
 
		btnAlerts.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MerchantLandingActivity.this, MerchantAlertActivity.class);
				intent.putExtra(TAG_MERCHANT_ID,"4200");
				startActivity(intent);
			}
 
		});
 
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mechant_landing, menu);
		return true;
	}

}
