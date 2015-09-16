package com.wx.bluetooth;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {

	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

	// view
	private ListView textOutList;

	private Button sendBtn;

	private EditText textIn;

	// array adapter
	private ArrayAdapter<String> msgArrayAdapter;

	// sendBtn listener
	private OnClickListener sendBtnOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

		}
	};

	// socket
	private BluetoothSocket socket;

	private BluetoothAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// get textOutList
		textOutList = (ListView) findViewById(R.id.textOutList);
		msgArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		textOutList.setAdapter(msgArrayAdapter);

		// get send button
		sendBtn = (Button) findViewById(R.id.sendButton);
		sendBtn.setOnClickListener(sendBtnOnClickListener);

		// get textIn
		textIn = (EditText) findViewById(R.id.textIn);

		init();

		String address = getIntent().getStringExtra(SearchActivity.DEVICE_ADDRESS);
		// String address = bundle.getString(SearchActivity.DEVICE_ADDRESS);
		connect(address);

	}

	private void init() {

		if (adapter == null) {
			adapter = BluetoothAdapter.getDefaultAdapter();
		}
	}

	public void connect(String address) {
		Log.i("wx", "address:" + address);
		BluetoothDevice device = adapter.getRemoteDevice(address);

		// other method
		Method m = null;
		try {
			m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
		}

		BluetoothSocket socket = null;
		try {
			socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		// try {
		// socket = device.createRfcommSocketToServiceRecord(MY_UUID);
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (Exception e) {
		// }
		try {
			socket.connect();
			textIn.setText("success:" + socket);
		} catch (IOException e) {
			e.printStackTrace();
			textIn.setText("failure:" + address + e.toString());
		}
	}

}
