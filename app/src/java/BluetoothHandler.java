package se.ju.frjo1425student.waiterbotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Created by jonas on 2017-11-28.
 */

public class BluetoothHandler
{
    private static final String TAG = "BluetoothHandler";
    private static final String DEVICE_MAC = "20:FA:BB:02:76:01";
    private static final String SERVICE_UUID = "bc2f4cc6-aaef-4351-9034-d66268e328f0";
    private static final String CHARACTERISTICS_UUID = "06d1e5e7-79ad-4a71-8faa-373789f7d93c";
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothGatt mBluetoothGatt;
    private static BluetoothGattCharacteristic mCharacteristic;

    static Context mContext;
    MainActivity mActivity;

    BluetoothHandler(Context Context)
    {
        this.mContext = Context;
        this.mActivity = (MainActivity) mContext;

        //Set up bluetooth
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ContextClass.getContext().getSystemService(Context.BLUETOOTH_SERVICE); //mContext
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public BluetoothAdapter getBluetoothAdapter()
    {
        return mBluetoothAdapter;
    }

    public void scanForDevice()
    {
        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        //else display toast?
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback()
            {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
                {
                    if (device.getAddress().toString().equals(DEVICE_MAC))
                    {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mBluetoothGatt = device.connectGatt(ContextClass.getContext(), false, mGattCallback); //mContext
                        Log.d(TAG, "Connected to: name: " + device.getName()
                                + " address: " + device.getAddress());
                        refreshDeviceCache(mBluetoothGatt);
                    }
                }
            };

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback()
            {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState)
                {
                    if (newState == BluetoothProfile.STATE_CONNECTED)
                    {
                        Log.d(TAG, "Connected to GATT server.");
                        mActivity.leIsConnectedMain(true);
                        Log.d(TAG, "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());

                    }
                    else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                    {
                        Log.d(TAG, "Disconnected from GATT server.");
                        mActivity.leIsConnectedMain(false);
                        //mActivity.leIsConnectedMain(false);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS)
                    {
                        Log.d(TAG, "Discovered service");
                         mCharacteristic = gatt
                                .getService(UUID.fromString(SERVICE_UUID))
                                .getCharacteristic(UUID.fromString(CHARACTERISTICS_UUID));

                        //Be notified when data characteristic is changed on Melody smart
                        gatt.setCharacteristicNotification(mCharacteristic, true);
                        BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptor(
                                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                    else
                    {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Characteristic notification
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic)
                {
                    //Get data from updated module
                    final byte[] data = characteristic.getValue();

                    String output = new String(data, StandardCharsets.US_ASCII);
                    Log.d(TAG, "Data from WaiterBot: " + output);
                    mActivity.updateStatus(output);
                }
            };

    //Called from MainActivity when user clicks button in OrdersFragment
    public void sendLeData(byte[] data)
    {
        //Create characteristic
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(
                UUID.fromString(SERVICE_UUID)).getCharacteristic(
                        UUID.fromString(CHARACTERISTICS_UUID));

        //Writing to characteristic
        characteristic.setValue(data);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void closeConnection()
    {
        Log.d(TAG, "Closing gatt connection manually.");
        if (mBluetoothGatt == null)
            return;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        mActivity.leIsConnectedMain(false);
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt)
    {
        try
        {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null)
            {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt,
                        new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException)
        {
            Log.e(TAG, "An exception occured while refreshing device");
        }
        return false;
    }
}
