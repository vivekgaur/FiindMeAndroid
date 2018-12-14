package com.example.fiindmenew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainPageActivity extends Activity {
	private Button btnUser;
	private Button btnMerchant;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		addListenerOnUserButton();
		addListenerOnMerchantButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_page, menu);
		return true;
	}
	
	public void addListenerOnUserButton() {
		 
		btnUser = (Button) findViewById(R.id.btnUser);
		
 
		btnUser.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				//EditText editText = (EditText) findViewById(R.id.u_zip_code);
				//zipCode = editText.getText().toString(); 
				//showDialog(TIME_DIALOG_ID);
				//Toast.makeText(UserFindActivity.this, "TEST", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainPageActivity.this, UserLoginActivity.class);
				
				startActivity(intent);
			}
 
		});
 
	}
	
	public void addListenerOnMerchantButton() {
		 
		btnMerchant = (Button) findViewById(R.id.btnMerchant);
		
 
		btnMerchant.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				//showDialog(TIME_DIALOG_ID);
				//Toast.makeText(UserFindActivity.this, "TEST", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainPageActivity.this, MerchantLoginActivity.class);				
				startActivity(intent);
			}
 
		});
 
	}

}
