package com.medtronic.neuro.RechargeReciever;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothScanner;
    ScanFilter mScanFilter;
    ScanSettings mScanSettings;
    Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppContext = this;

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        ScanFilter.Builder builder = new ScanFilter.Builder();
        ByteBuffer data = ByteBuffer.allocate(23);
        ByteBuffer mask = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C2-97CA-407C-84F8-B62AAC4E9020"));
        data.put(0, (byte)0xBE);
        data.put(1, (byte)0xAC);
        for (int i = 2; i <= 17; i++) {
            data.put(i, uuid[i-2]);
        }
        for (int i = 0; i <= 17; i++) {
            mask.put((byte) 0x01);
        }
        builder.setManufacturerData(224, data.array(), mask.array());
        mScanFilter = builder.build();

        ScanSettings.Builder settings = new ScanSettings.Builder();
        settings.setReportDelay(0);
        settings.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = settings.build();

        mBluetoothScanner.startScan(Arrays.asList(mScanFilter), mScanSettings, mScanCallback);
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord scanRecord = result.getScanRecord();
            byte[] data = scanRecord.getManufacturerSpecificData(224);
            if (data != null) {
                TextView txt = (TextView)findViewById(R.id.detector_text_updater);
                txt.setText("Found Beacon!");
            } else {
                TextView txt = (TextView)findViewById(R.id.detector_text_updater);
                txt.setText("Found Beacon Failed!");
            }
            int rssi = result.getRssi();
        }
    };

    public byte[] getIdAsByte(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
