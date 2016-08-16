package se.hc.presencedetectionfinal.activity;/*
package se.grouprich.presencedetection.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import se.grouprich.presencedetection.R;
import se.grouprich.presencedetection.adapter.BeaconAdapter;
import se.grouprich.presencedetection.model.Beacon;

@TargetApi(Build.VERSION_CODES.M)
public class ScanActivity extends AppCompatActivity {

    public static final String SERVICE_UUID = "19721006-2004-2007-2014-acc0cbeac000";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final long SCAN_PERIOD = 5000;

    private int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private List<Beacon> beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();

            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {

            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else {

            if (Build.VERSION.SDK_INT >= 21) {

                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<>();
                beacons = Collections.synchronizedList(new ArrayList<Beacon>());
            }

            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {

            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {

        if (mGatt == null) {

            return;
        }

        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT < 21) {

                        mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    } else {

                        mLEScanner.stopScan(mScanCallback);
                    }
                }
            }, SCAN_PERIOD);

            if (Build.VERSION.SDK_INT < 21) {

                mBluetoothAdapter.startLeScan(mLeScanCallback);

            } else {

                mLEScanner.startScan(filters, settings, mScanCallback);
            }

        } else {

            if (Build.VERSION.SDK_INT < 21) {

                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            } else {

                mLEScanner.stopScan(mScanCallback);
            }
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            final BluetoothDevice btDevice = result.getDevice();

            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            Log.i("** RECORD *****  ", String.valueOf(result.getScanRecord()));
            Log.i("** RECORD2 *****  ", String.valueOf(result.getScanRecord().getManufacturerSpecificData()));

            final List<ParcelUuid> serviceUuids = result.getScanRecord().getServiceUuids();
            final List<String> serviceUuidsString = new ArrayList<>();

            if (serviceUuids != null) {

                for (ParcelUuid serviceUuid : serviceUuids) {

                    serviceUuidsString.add(serviceUuid.toString());
                }
            }

            if (serviceUuidsString.contains(SERVICE_UUID) && newMacAddressDetected(result.getDevice().getAddress())) {

                Beacon beacon = new Beacon(String.valueOf(result.getDevice().getName()),
                        String.valueOf(result.getDevice().getAddress()),
                        String.valueOf(result.getRssi()),
                        SERVICE_UUID);

                Log.i("**== ADDED ***", "");
                beacons.add(beacon);
            }

            displayBeaconsList();
            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

            for (ScanResult sr : results) {
                Log.i("## ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {

            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {

        if (mGatt == null) {

            mGatt = device.connectGatt(this, false, gattCallback);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    scanLeDevice(false);
                }
            }, SCAN_PERIOD);

        } else {

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    scanLeDevice(false);
                }
            }, SCAN_PERIOD);
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.i("onConnectionStateChange", "Status: " + status);

            switch (newState) {

                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;

                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            final List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());

            if (!services.isEmpty()) {

                gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };

    public void displayBeaconsList() {

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.beacons_recycler_view);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final RecyclerView.Adapter adapter = new BeaconAdapter(this, beacons);

        if (recyclerView != null) {

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    public boolean newMacAddressDetected(String address) {

        if (beacons.size() != 0) {

            final Iterator<Beacon> it = beacons.iterator();

            while (it.hasNext()) {

                final Beacon beacon = it.next();
                final String addressBeacon = String.valueOf(beacon.getMacAddress());
                final boolean bool = (addressBeacon.equals(address));

                if (bool) {

                    return false;
                }
            }
        }
        return true;
    }
}
*/
