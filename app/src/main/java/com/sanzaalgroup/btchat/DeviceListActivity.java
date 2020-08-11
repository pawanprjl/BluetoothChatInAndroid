package com.sanzaalgroup.btchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // initialize adapters
        ArrayAdapter<String> pairedDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        // initialize our listviews
        ListView pairedDevicesListView = findViewById(R.id.paired_devices);
        pairedDevicesListView.setAdapter(pairedDeviceArrayAdapter);

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
}