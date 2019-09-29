package com.example.ryuon.popup.ManualControl;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ryuon.popup.Bluetooth.BluetoothHelper;
import com.example.ryuon.popup.R;

import java.util.ArrayList;

public class ManualLampActivity extends AppCompatActivity implements ManualControl {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_lamp);
//        ArrayList<String> module_name = (ArrayList<String>) getIntent().getSerializableExtra("moduleNames");
//        BluetoothHelper bluetoothHelper = new BluetoothHelper();
//        bluetoothHelper.connectDevice(module_name);
    }
}
