package com.sanzaalgroup.btchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;

    // return intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // initialize new device adapter
    ArrayAdapter<String> newDeviceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // set result canceled when the user backs out
        setResult(Activity.RESULT_CANCELED);


        // initialize adapters
        ArrayAdapter<String> pairedDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        newDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);


        // initialize our listviews
        ListView pairedDevicesListView = findViewById(R.id.paired_devices);
        pairedDevicesListView.setAdapter(pairedDeviceArrayAdapter);
        pairedDevicesListView.setOnItemClickListener(mDeviceClickListener);

        ListView newDeviceListView = findViewById(R.id.new_devices);
        newDeviceListView.setAdapter(newDeviceArrayAdapter);
        newDeviceListView.setOnItemClickListener(mDeviceClickListener);

        Button scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDiscovery();
                view.setVisibility(View.GONE);
            }
        });


        // register receivers for broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // if there is any paired devices, add each one to adapter
        if(pairedDevices.size() > 0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);

            for (BluetoothDevice device : pairedDevices){
                pairedDeviceArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }else{
            pairedDeviceArrayAdapter.add("no paired devices");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mBtAdapter != null){
            mBtAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        setTitle("Scanning...");

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if(mBtAdapter.isDiscovering()){
            mBtAdapter.cancelDiscovery();
        }

        if(!mBtAdapter.startDiscovery()){
            Toast.makeText(this, "error during start discovery", Toast.LENGTH_SHORT).show();
        };
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                    newDeviceArrayAdapter.add(device.getName()+"\n"+device.getAddress());
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                setTitle("Select Device");

                if(newDeviceArrayAdapter.getCount() == 0){
                    newDeviceArrayAdapter.add("no device found");
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // cancel discovery because it's costly and we are about to connect
            mBtAdapter.cancelDiscovery();

            // get the device mac address
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            // create the result intent and include the mac address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // set the result
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

}