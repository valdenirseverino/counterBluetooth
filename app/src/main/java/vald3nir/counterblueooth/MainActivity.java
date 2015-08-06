package vald3nir.counterblueooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {


    @ViewById
    TextView numDispostivos;

    @ViewById
    TextView logDispositivos;


    @Click(R.id.pesquisar)
    public void onClick(View v) {

        logDispositivos.setText("");
        numDispostivos.setText("");

        bluetoothDevices = new ArrayList<>();
        logDispositivos.append("Adapter: " + bluetoothAdapter);

        CheckBTState();
    }

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(ActionFoundReceiver, filter);


    }

    @AfterViews
    public void afterViews() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        logDispositivos.append("Adapter: " + bluetoothAdapter);
        CheckBTState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBTState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null)
            bluetoothAdapter.cancelDiscovery();

        unregisterReceiver(ActionFoundReceiver);
    }

    private void CheckBTState() {

        if (bluetoothAdapter == null) {

            logDispositivos.append("\n\nBluetooth NOT supported. Aborting.");

        } else {

            if (bluetoothAdapter.isEnabled()) {
                logDispositivos.append("\n\nBluetooth is enabled...");

                bluetoothAdapter.startDiscovery();

            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                logDispositivos.append("\n\nDevice: " + device.getName() + ", " + device);

                bluetoothDevices.add(device);

            } else {

                if (BluetoothDevice.ACTION_UUID.equals(action)) {

                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

                    for (Parcelable anUuidExtra : uuidExtra) {
                        logDispositivos.append("\n\nDevice: " + device.getName() + ", " + device + ", Service: " + anUuidExtra.toString());
                    }


                } else {

                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                        logDispositivos.append("\n\nDiscovery Started...");

                    } else {

                        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                            logDispositivos.append("\n\nDiscovery Finished");
                            numDispostivos.setText(bluetoothDevices.size() + "");

//                            for (BluetoothDevice device : bluetoothDevices) {
//
//                                logDispositivos.append("\nGetting Services for " + device.getName() + ", " + device);
//
//                                if (!device.fetchUuidsWithSdp()) {
//                                    logDispositivos.append("\nSDP Failed for " + device.getName());
//                                }
//                            }
                        }
                    }


                }


            }


        }
    };


}
