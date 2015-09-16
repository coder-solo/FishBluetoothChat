package com.wx.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	public static final String DEVICE_ADDRESS = "device_address";

	// view
	private Button searchButton;

	private ListView scanDevicesList;

	private TextView scanDevicesTitle;

	private ListView bondDevicesList;

	private TextView bondDevicesTitle;

	private Button boundPcButton;

	private ArrayAdapter<String> scanDevicesArrayAdapter;

	private ArrayAdapter<String> bondDevicesArrayAdapter;

	// adapter
	private BluetoothAdapter adapter;
	private BluetoothReceiver receiver;
	
	// different scanData
	private List<String> differentCheckList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_search);

		// get searchButton and add OnClickListener
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new SearchButtonClickListener());

		// get scanDevicesList
		scanDevicesList = (ListView) findViewById(R.id.scanDevicesList);
		scanDevicesTitle = (TextView) findViewById(R.id.scanDevicesTitle);

		scanDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		scanDevicesList.setAdapter(scanDevicesArrayAdapter);
		scanDevicesList.setOnItemClickListener(onItemClickListener);

		// get bondDevicesList
		bondDevicesList = (ListView) findViewById(R.id.bondDevicesList);
		bondDevicesTitle = (TextView) findViewById(R.id.bondDevicesTitle);

		bondDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		bondDevicesList.setAdapter(bondDevicesArrayAdapter);
		bondDevicesList.setOnItemClickListener(onItemClickListener);

		// get boundPcButton and add OnClickListener
		boundPcButton = (Button) findViewById(R.id.boundPcButton);
		boundPcButton.setOnClickListener(boundPcButtonListener);

		// init adapter
		init();

		// init bonded devices
		setBondDevicesList();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		// cancel discovery
		if (adapter.isDiscovering()) {
			adapter.cancelDiscovery();
		}

		// unregister broadcast listeners
		this.unregisterReceiver(receiver);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg3) {

			// cancel discovery
			if (adapter.isDiscovering()) {
				adapter.cancelDiscovery();
			}

			// get device bluetooth address
			String address = ((TextView) view).getText().toString();

			toChatActivity(address.substring(address.length()-17));
		}

	};

	private void toChatActivity(String address) {
		Intent intent = new Intent(this, ChatActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString(DEVICE_ADDRESS, address);
		// intent.putExtras(intent);
		intent.putExtra(DEVICE_ADDRESS, address);
		startActivity(intent);
	}

	private void init() {

		// init adapter
		if (adapter == null) {
			adapter = BluetoothAdapter.getDefaultAdapter();
			// has not bluetooth
			if (adapter == null) {
				Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			}
			// register bluetoothReceiver
			if (receiver == null) {
				receiver = new BluetoothReceiver();
				IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				registerReceiver(receiver, intentFilter);

				intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				registerReceiver(receiver, intentFilter);
			}
		}
		
	}

	private void setBondDevicesList() {

		differentCheckList = new ArrayList<String>();
		// get bond devicesList
		if (adapter != null) {
			Set<BluetoothDevice> bondDevicesSet = adapter.getBondedDevices();

			bondDevicesArrayAdapter.clear();
			for (BluetoothDevice bondDevice : bondDevicesSet) {
				bondDevicesArrayAdapter.add(bondDevice.getName() + "\n" + bondDevice.getAddress());
				differentCheckList.add(bondDevice.getAddress());
			}

			if (bondDevicesArrayAdapter.isEmpty()) {
				bondDevicesTitle.setVisibility(View.GONE);
			} else {
				bondDevicesTitle.setVisibility(View.VISIBLE);
			}
		}
	}

	private class BluetoothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// found bluetooth device
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(!differentCheckList.contains(device.getAddress())){
					differentCheckList.add(device.getAddress());
					scanDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {

				String titleSelect = getResources().getString(R.string.title_select);
				setTitle(titleSelect);
				setProgressBarIndeterminateVisibility(false);

				// no devices
				if (scanDevicesArrayAdapter.isEmpty()) {
					String noDevice = getResources().getString(R.string.no_scan);
					scanDevicesArrayAdapter.add(noDevice);
				}
			}
		}
	}

	private class SearchButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			// is bluetooth enable
			if (!adapter.isEnabled()) {
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
				return;
			}

			// if adapter is discovering, cancel it
			if (adapter.isDiscovering()) {
				adapter.cancelDiscovery();
			}

			// set scan devices title visible
			scanDevicesTitle.setVisibility(View.VISIBLE);

			// set title
			setTitle("scaning");
			setProgressBarIndeterminateVisibility(true);

			// reset devices adapter
			scanDevicesArrayAdapter.clear();

			// reset bonded devices
			setBondDevicesList();
			
			// search bluetooth
			adapter.startDiscovery();
		}

	}

	private OnClickListener boundPcButtonListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			// String address = "48:5A:B6:5E:91:E2"; // pc
			String address = "4C:21:D0:0A:58:B4"; // deng

			toChatActivity(address);
		}
	};
}
