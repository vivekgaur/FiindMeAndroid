package com.example.fiindmenew;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

//package com.authorwjf.http_get;

public class JSONListActivity extends Activity implements OnClickListener{
	InputStream is = null;
	static JSONObject jObj = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_json_list);
		findViewById(R.id.my_button).setOnClickListener(this);
		new LongRunningGetIO().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jsonlist, menu);
		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		Button b = (Button)findViewById(R.id.my_button);
		b.setClickable(false);
		
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
			//HttpContext localContext = new BasicHttpContext();
			//ResponseHandler<String> handler = new BasicResponseHandler();
			HttpGet httpGet = new HttpGet("http://10.0.2.2/~vgaur/fiindme/index.php/find/deal");
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet);
				response.getStatusLine().getStatusCode();
				//text = httpClient.execute(httpGet, handler);
				HttpEntity entity = response.getEntity();
				//Set the input stream
				is = entity.getContent();
				text = getASCIIContentFromEntity(entity);
				} 
				catch (ClientProtocolException e) {  
					e.printStackTrace();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  catch (Exception e) {
					//Log.("[GET REQUEST]", "Network exception", e);
					return e.getLocalizedMessage();
			}
			httpClient.getConnectionManager().shutdown(); 
			return text;
		}
		
		protected void onPostExecute(String results) {
			if (results!=null) {
				String formData = new String(getIntent().getExtras().getString("zipCode"));
				formData += " " +new String(getIntent().getExtras().getString("category"));
				Integer h = getIntent().getExtras().getInt("hour");
				formData += " " + h.toString();
				Integer m = getIntent().getExtras().getInt("minute");
				formData += " " + m.toString();
				//formData = new String(getIntent().getExtras().getString("minute"));
				formData += " ";
				formData += results;

				EditText et = (EditText)findViewById(R.id.edit_json);
				et.setText(formData);
			}
			Button b = (Button)findViewById(R.id.my_button);
			b.setClickable(true);
			}
		}
}
