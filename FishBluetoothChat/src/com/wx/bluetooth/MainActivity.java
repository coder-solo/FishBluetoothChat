package com.wx.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity{

	private Button toServerBtn;
	
	private Button toClientBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		// set Server button
		toServerBtn = (Button) findViewById(R.id.toServerBtn);
		
		// set Client button
		
	}
	
}
