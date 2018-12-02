package com.zjianhao.album;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveId;
import com.zjianhao.R;
import com.zjianhao.local.DirectoryChooserActivity;
import com.zjianhao.local.DirectoryChooserConfig;
import com.zjianhao.holder.SettingHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zjianhao.holder.SettingHolder.SETTING_LOCATION;
import static com.zjianhao.holder.SettingHolder.SETTING_TIME;
import static com.zjianhao.holder.SettingHolder.SUB_SETTING_DAY;
import static com.zjianhao.holder.SettingHolder.SUB_SETTING_MONTH;
import static com.zjianhao.holder.SettingHolder.SUB_SETTING_YEAR;

public class settingActivity extends Activity {

    public static SettingHolder mySetting;
    private Spinner s;


    public static DriveId myDriveId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mySetting = new SettingHolder();
        final TextView tv = (TextView)findViewById(R.id.textView4); //값들어오는지 확인용
        s = (Spinner)findViewById(R.id.spinner_time);
        s.setEnabled(false);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {  // position은 0부터 시작인데, 셋팅홀더에서 YEAR=1,MONTh=2,DAY=3 으로 셋팅되어있어서 (-1) 해줌!
                    case SUB_SETTING_YEAR-1: // Year
                        mySetting.setSort_time_type(SUB_SETTING_YEAR);
                        break;
                    case SettingHolder.SUB_SETTING_MONTH-1: // Month
                        mySetting.setSort_time_type(SUB_SETTING_MONTH);
                        break;
                    case SettingHolder.SUB_SETTING_DAY-1: // Day
                        mySetting.setSort_time_type(SUB_SETTING_DAY);
                        break;
                    default:
                        Log.d("Unexpected Error", "settingActivity SUB_SETTING error");
                        break;
                }
                //tv.setText(""+ parent.getItemAtPosition(position)); //선택한 내용이 잘들어오는지 확인용
                //tv.setText(""+ sort_time_type); // 잘 저장되는지 확인용
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /* 설정값 저장하기 - 박상혁 */
    public void click_save(View view) {

        EditText editText = (EditText) findViewById(R.id.cloud_Directory);  // editText의 값을 받아옴

        if(mySetting.setCloud_directory(editText.getText().toString())) {  // 받아온 값을 string변수에 저장
            Log.d("Cloud Directory Set : ", mySetting.getCloud_directory());
        }else {
            Toast.makeText(getApplicationContext(), "Check Your Cloud Directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        editText = (EditText) findViewById(R.id.local_Directory);
        if(mySetting.setLocal_directory(editText.getText().toString())){
            Log.d("Local Directory : ", mySetting.getLocal_directory());
        }else{
            Toast.makeText(getApplicationContext(),"Check Your Local Directory",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mySetting.getSort_type() == 0){
            Toast.makeText(getApplicationContext(),"Check Your Sort Option",Toast.LENGTH_SHORT).show();
            return;
        }else if(mySetting.getSort_type()==SETTING_TIME && mySetting.getSort_time_type() == 0){
            Toast.makeText(getApplicationContext(),"Check Your Time SubSort Option",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Setting Saved",Toast.LENGTH_LONG).show();



        if(myDriveId != null) {
            Log.d("Cloud Directory Set : ", myDriveId.toString());
            Intent serviceIntent = new Intent(this,FileUploaderService.class);
            startService(serviceIntent);
        }else{
            Toast.makeText(getApplicationContext(), "Check Your Cloud Directory!", Toast.LENGTH_SHORT).show();
            return;
        }

        finish(); // Return to Previous Page
    }

    public void click_radioButton_time(View view) {
        mySetting.setSort_type(SettingHolder.SETTING_TIME);
        s.setEnabled(true);
//        numStr = String.valueOf(sort_type);    // 값이 잘 들어오는지 확인용 Toast
//        Toast.makeText(this, numStr, Toast.LENGTH_SHORT).show();
    }

    public void click_radioButton_location(View view) {
        mySetting.setSort_type(SETTING_LOCATION);
        s.setEnabled(false);
//        numStr = String.valueOf(sort_type);    // 값이 잘 들어오는지 확인용 Toast
//        Toast.makeText(this, numStr, Toast.LENGTH_SHORT).show();
    }

    public void click_local_directory_setting(View view){
        Intent intent = new Intent(getApplicationContext(),com.zjianhao.local.DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        intent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
        startActivityForResult(intent, 1088);
    }
    public void click_cloud_directory_setting(View view){
        Intent intent = new Intent(getApplicationContext(),QueryFilesInFolderActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  // DirectoryChooserActivity에서 설정한 경로 받아와서 텍스트박스에 띄워주기
        if(requestCode == 1088) {
            try {
                EditText et = (EditText) findViewById(R.id.local_Directory);
                mySetting.setLocal_directory(data.getStringExtra("RESULT_DIR"));
                Log.d("PATH", mySetting.getLocal_directory());
                et.setText(mySetting.getLocal_directory());
            }catch (NullPointerException e) {
                mySetting.setLocal_directory("");
            }
        }
    }




}