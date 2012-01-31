package com.org.androbot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity implements OnClickListener{
	
	private Button done;
	private EditText url;
	private EditText botid;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		botid=(EditText) findViewById(R.id.botid);
		url = (EditText) findViewById(R.id.url);
		done = (Button) findViewById(R.id.done);
		done.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v==done)
		{
			String BOTID = botid.getText().toString();
			String URL   = url.getText().toString();
			if(BOTID!=null && URL!=null && BOTID.length() > 0 && URL.length() >0 )
			{
				Intent intent = new Intent();
				intent.putExtra("BOTID", BOTID);
				intent.putExtra("URL", URL);
				setResult(Activity.RESULT_OK,intent);
				finish();
			}
			
			
		}
	}
}
