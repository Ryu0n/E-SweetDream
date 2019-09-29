package com.example.ryuon.popup.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ryuon.popup.AutoControl.AutoControlConditionActivity;


import com.example.ryuon.popup.Bluetooth.BluetoothHelper;
import com.example.ryuon.popup.CustomViewAdapter.GroupControl.ListViewBtnAdapter_Blind;
import com.example.ryuon.popup.CustomViewAdapter.GroupControl.ListViewBtnAdapter_Lamp;
import com.example.ryuon.popup.CustomViewAdapter.GroupControl.ListViewBtnAdapter_Plug;
import com.example.ryuon.popup.CustomViewAdapter.GroupControl.ListViewBtnAdapter_Sensor;

import com.example.ryuon.popup.ManualControl.ManualBlindActivity;
import com.example.ryuon.popup.ManualControl.ManualLampActivity;
import com.example.ryuon.popup.ManualControl.ManualPlugActivity;


import com.example.ryuon.popup.Module_Object.Blind;
import com.example.ryuon.popup.Module_Object.Group;
import com.example.ryuon.popup.Module_Object.Lamp;
import com.example.ryuon.popup.Module_Object.Plug;
import com.example.ryuon.popup.Module_Object.Sensor;
import com.example.ryuon.popup.Module_Object.module;
import com.example.ryuon.popup.R;

import java.util.ArrayList;
//import android.widget.TextView;

public class GroupControlActivity extends AppCompatActivity implements ListViewBtnAdapter_Plug.ListBtnClickListener, ListViewBtnAdapter_Lamp.ListBtnClickListener2, ListViewBtnAdapter_Blind.ListBtnClickListener4{

    public static final String EXTRA_MESSAGE="message";
    //테스트용 모듈로 사용자가 임의로 아무거나 넣어도 정상작동하게 코드를 짜놨음.(단 정해진 모듈 이외의 이름은 안먹힐수도?)
    // 실제 데이터에서는 ***반드시!!!!!***** <Sort>를 해서 무드등을 첫번째에 오게 해야한다.
    // 그리고 같은 모듈끼리는 연속적으로 있도록 Sort를 해야할 것임.
    //(코드상 0번째 무드등의 리스트뷰 /나머지 리스트뷰 2개가 설정되어있음)



    //    ArrayList<String> moduleList;
//    String[] moduleList = {"무드등","무드등", "멀티탭","멀티탭","블라인드","블라인드","센서모듈"} ;
    ArrayList<module> moduleList     = new ArrayList<>();
    ArrayList<module> sortModuleList = new ArrayList<>();
    //무드등의 수. 사용하는 곳은 loadItemsFromDB2 와 loadItemsFromDB 에서 사용한다.
    public int count = 0;
    //리스트뷰가 2개 필요하다. 하나는 무드등전용 리스트뷰(listview), 나머지는 다른 모듈의 리스트뷰(listview2),마지막은 센서리스트뷰(listview3)
    ListView listview,listview2,listview3,listview4 ;

    //어댑터도 2개가 있어야 한다.(무드등은 adapter, 나머지 모듈은 adapter2, adapter3, adapter4)
    ListViewBtnAdapter_Plug plugAdapter;
    ListViewBtnAdapter_Lamp lampAdapter;
    ListViewBtnAdapter_Sensor sensorAdapter;
    ListViewBtnAdapter_Blind blindAdapter;

    ArrayList<Plug> plug;
    ArrayList<Lamp> lamp;
    ArrayList<Blind> blind;
    ArrayList<Sensor> sensor;

    ArrayList<String> module_name;

    BluetoothHelper bluetoothHelper;

    Group receivedData_selected_group;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_control);

        receivedData_selected_group = (Group) getIntent().getSerializableExtra("selectedGroup");

        moduleList.addAll(receivedData_selected_group.getModule_List());
        sortModule();

        module_name = new ArrayList<>();
        for (int i = 0; i < sortModuleList.size(); i++) {
            module_name.add(sortModuleList.get(i).getName());
        }

        if (thread == null) {
            bluetoothHelper = new BluetoothHelper();
            bluetoothHelper.setModuleName(module_name);
            thread = new Thread(bluetoothHelper);
            thread.start();
        }


        //간단한 팝업창이 뜨는 부분
        CharSequence text="자동제어조건을 설정하세요";
        int duration=Toast.LENGTH_LONG;
        Toast toast=Toast.makeText(this,text,duration);
        toast.show();

        //독자적으로 어레이리스트를 생성한다.
        lamp   = new ArrayList<Lamp>() ;
        plug   = new ArrayList<Plug>();
        blind  = new ArrayList<Blind>() ;
        sensor = new ArrayList<Sensor>() ;

        // 무드등이 있을경우 색버튼을 포함하는 리스트뷰를 따로 생성해야 하므로 조건문을 붙여봤다.
        if(sortModuleList.get(0).getName().contains("무드등")){
            // 아이템 로드, 아래의 자바파일과 레이아웃과 일관성 있게 인자로 들어가는 어레이리스트도 2번으로 썼다.(헷갈리지말라고)
            loadItemsFromLampDB(sortModuleList);
            // Adapter 생성, ListViewBtnAdapter2라는 자바파일은 무드등전용으로 만든 것이다. 헷갈리지말자.
            // ListViewBtnAdapter_Lamp.java 랑 레이아웃에 listView_btn_item2.xml 이랑 짝임 ㅋ
            lampAdapter = new ListViewBtnAdapter_Lamp(this, R.layout.listview_btn_item2, lamp,this);

            // 리스트뷰 참조 및 Adapter달기, group_list_mood는
            listview2 = (ListView)findViewById(R.id.group_list_mood);
            listview2.setAdapter(lampAdapter);
        }

        // 멀티탭 - 멀티탭 리스트 뷰 연결
        loadItemsFromPlugDB(sortModuleList);
        plugAdapter = new ListViewBtnAdapter_Plug(this, R.layout.listview_btn_item, plug,this) ;
        listview = (ListView)findViewById(R.id.group_list_plug);
        listview.setAdapter(plugAdapter);

        // 블라인드 - 블라인드 리스트 뷰 연결
        loadItemsFromBlindDB(sortModuleList);
        blindAdapter = new ListViewBtnAdapter_Blind(this, R.layout.listview_btn_item4, blind, this);
        listview4 = (ListView)findViewById(R.id.group_list_blind);
        listview4.setAdapter(blindAdapter);

        // 센서모듈 - 센서모듈 리스트 뷰 연결
        loadItemsFromSensorDB(sortModuleList);
        sensorAdapter = new ListViewBtnAdapter_Sensor(this, R.layout.listview_btn_item3, sensor) ;
        listview3 = (ListView)findViewById(R.id.group_list_sensor);
        listview3.setAdapter(sensorAdapter);

        Intent intent=getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        TextView messageView = (TextView)findViewById(R.id.sleepTime_result);
        messageView.setText(message);
    }

    //멀티탭 전용 어레이 리스트 생성
    public boolean loadItemsFromPlugDB(ArrayList<module> list) {
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().contains("멀티탭")){
                plug.add((Plug)list.get(i));
            }
        }
        return true ;
    }

    //무드등 전용 어레이 리스트 생성
    public boolean loadItemsFromLampDB(ArrayList<module> list) {
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().contains("무드등")) {
                lamp.add((Lamp)list.get(i));
            }
        }
        return true ;
    }

    //블라인드 전용 어레이 리스트 생성
    public boolean loadItemsFromBlindDB(ArrayList<module> list) {
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().contains("블라인드")){
                blind.add((Blind)list.get(i));
            }
        }
        return true ;
    }

    //센서모듈 전용 어레이 리스트 생성
    public boolean loadItemsFromSensorDB(ArrayList<module> list) {
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().contains("센서모듈")){
                sensor.add((Sensor)list.get(i));
            }
        }
        return true ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_auto_control_condition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_autocontrolcondition) {
            Intent intent = new Intent(this, AutoControlConditionActivity.class);
            intent.putExtra("selectedGroup", receivedData_selected_group);
            startActivityForResult(intent, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {

        }
    }


    // 각 모듈을 수동으로 제어하기 위해 버튼을 눌렀을 때 각각의 화면으로 이동[1]
    // 첫번째 오버라이드:  플러그와 블라인드를 수동제어하기 위해서 포지션만 받아서 인텐트
    @Override
    public void onListBtnClick(int event_position) {
        if (event_position == 1) {
            Intent intent = new Intent(this, ManualPlugActivity.class);
            intent.putExtra("moduleNames", module_name);
            startActivity(intent);
        } else if (event_position == 2){
            Intent intent = new Intent(this, ManualBlindActivity.class);
            intent.putExtra("moduleNames", module_name);
            startActivity(intent);
        }
    }

    // 각 모듈을 수동으로 제어하기 위해 버튼을 눌렀을 때 각각의 화면으로 이동[2]
    // 두번째 오버라이드: 포지션과 그에 따른 id값을 받아서 같은 포지션에서 보내줄 인텐트를 id로 구별한다.
    @Override
    public void onListBtnClick(int event_position, int id) {
        if (event_position == 0) {
            if (id == R.id.mood_button) {
                Intent intent = new Intent(this, PopupActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ManualLampActivity.class);
//                intent.putExtra("moduleNames", module_name);
                startActivity(intent);
            }
        }
    }

    //COLOR버튼을 눌렀을 때 팝업이 뜨는 창
    public void onPopupClicked(View view) {
        Intent intent = new Intent(this, PopupActivity.class);
        startActivity(intent);
    }


    public void onSwitchClicked(View view){
        Switch control = (Switch) findViewById(R.id.switch1);
//        TextView t = (TextView)findViewById(R.id.testt);
        boolean on = control.isChecked();
//        if (on) {
//            t.setText("1");
//        }
//        Button manual_control  = (Button)findViewById(R.id.button1);
//        Button manual_control2 = (Button)findViewById(R.id.mood_button1);
//        Button manual_control3 = (Button)findViewById(R.id.button2);

        if (!lamp.isEmpty()) {
            lampAdapter.setActivate(on);
            lampAdapter.notifyDataSetChanged();
        }

        if (!plug.isEmpty()) {
            plugAdapter.setActivate(on);
            plugAdapter.notifyDataSetChanged();
        }

        if (!blind.isEmpty()) {
            blindAdapter.setActivate(on);
            blindAdapter.notifyDataSetChanged();
        }


//        if(on) {
////            manual_control .setEnabled(false);
////            manual_control2.setEnabled(false);
////            manual_control3.setEnabled(false);
//            plugAdapter.setActivate(on);
//            plugAdapter.notifyDataSetChanged();
//        } else {
//            manual_control .setEnabled(true);
//            manual_control2.setEnabled(true);
//            manual_control3.setEnabled(true);
//            plugAdapter.notifyDataSetChanged();
//        }
    }

    public void sortModule(){
        sortModuleList = new ArrayList<>();
        for(int i = 0; i < moduleList.size(); i++){
            if(moduleList.get(i).getName().contains("무드등")){
                sortModuleList.add(moduleList.get(i));
            }
        }
        for(int i = 0; i < moduleList.size(); i++){
            if(moduleList.get(i).getName().contains("멀티탭")){
                sortModuleList.add(moduleList.get(i));
            }
        }
        for(int i = 0; i < moduleList.size(); i++){
            if(moduleList.get(i).getName().contains("블라인드")){
                sortModuleList.add(moduleList.get(i));
            }
        }
        for(int i = 0; i < moduleList.size(); i++){
            if(moduleList.get(i).getName().contains("센서모듈")){
                sortModuleList.add(moduleList.get(i));
            }
        }
    }

    @Override
    public void onBackPressed() {
        thread.interrupt();
        thread = null;
        finish();
    }
}