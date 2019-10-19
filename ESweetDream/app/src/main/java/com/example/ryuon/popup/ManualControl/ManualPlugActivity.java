package com.example.ryuon.popup.ManualControl;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ryuon.popup.Bluetooth.BluetoothHelper;
import com.example.ryuon.popup.Module_Object.Plug;
import com.example.ryuon.popup.R;

import java.util.ArrayList;

public class ManualPlugActivity extends AppCompatActivity implements ManualControl {

    ArrayList<Plug> plug;
    int deviceIndex;
    Byte plugNum, onoff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_plug);

        plug = (ArrayList<Plug>)getIntent().getSerializableExtra("plug");

        deviceIndex = BluetoothHelper.findingIndex("멀티탭");



    }

    public void plug1On(View view) {
        plugNum = 1;
        onoff   = 1;
        BluetoothHelper.send_Data(deviceIndex, plugNum, onoff);
    }

    public void plug1Off(View view) {
        plugNum = 1;
        onoff   = 0;
        BluetoothHelper.send_Data(deviceIndex, plugNum, onoff);
    }

    public void plug2On(View view) {
        plugNum = 2;
        onoff   = 1;
        BluetoothHelper.send_Data(deviceIndex, plugNum, onoff);
    }

    public void plug2Off(View view) {
        plugNum = 2;
        onoff   = 0;
        BluetoothHelper.send_Data(deviceIndex, plugNum, onoff);
    }

}
