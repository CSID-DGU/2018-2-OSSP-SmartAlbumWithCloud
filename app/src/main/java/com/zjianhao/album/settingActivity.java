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

import static com.zjianhao.holder.SettingHolder.SETTING_TIME;
import static com.zjianhao.holder.SettingHolder.SUB_SETTING_DAY;
import static com.zjianhao.holder.SettingHolder.SUB_SETTING_MONTH;
import static com.zjianhao.holder.SettingHolder.SUB_SETTING_YEAR;

public class settingActivity extends Activity {
    private SettingHolder mySetting;
    private Spinner s;
    Map<String, ArrayList<PhotoDatabase>> photoListByLoc = new HashMap<>();
    Map<String, ArrayList<PhotoDatabase>> photoListByDate = new HashMap<>();
    Set<String> locations = new HashSet<>();
    Set<String> dates = new HashSet<>();
    ArrayList<PhotoDatabase> dbs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mySetting = new SettingHolder();
        final TextView tv = (TextView)findViewById(R.id.textView4); //        값들어오는지 확인용
        s = (Spinner)findViewById(R.id.spinner_time);
        s.setEnabled(false);
        dbs = getDatabase();
        for(PhotoDatabase pdb : dbs)
        {
            if(pdb.location==null)
            {
                pdb.location="NoSuchLocation";
            }
            if(pdb.date==null)
            {
                pdb.date="NoSuchDate";
            }
            locations.add(pdb.location);
            dates.add(pdb.date);
        }

        for(String loc : locations)
        {
            ArrayList<PhotoDatabase> mapArr = new ArrayList();
            for(PhotoDatabase pdb : dbs)
            {
                if(pdb.location.equals(loc)) {
                    mapArr.add(pdb);
                }
            }
            photoListByLoc.put(loc,mapArr);
        }

        for (String date : dates)
        {
            ArrayList<PhotoDatabase> mapArr = new ArrayList();
            for(PhotoDatabase pdb : dbs)
            {
                if(pdb.date.equals(date)) {
                    mapArr.add(pdb);
                }
            }
            photoListByDate.put(date,mapArr);
        }//추가코드
        DirFileManager dfm = new DirFileManager();
        dfm.copyFileByMap("/DCIM/장소분류테스트",locations,photoListByLoc);//정상구동확인 BY LJH 18.11.24
        dfm.copyFileByMap("/DCIM/날짜별분류테스트",dates,photoListByDate);


        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SUB_SETTING_YEAR: // Year
                        mySetting.setSort_time_type(SUB_SETTING_YEAR);
                        break;
                    case SettingHolder.SUB_SETTING_MONTH: // Month
                        mySetting.setSort_time_type(SUB_SETTING_MONTH);
                        break;
                    case SettingHolder.SUB_SETTING_DAY: // Day
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
        }else{
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
        Toast.makeText(getApplicationContext(), "Setting Saved",Toast.LENGTH_SHORT).show();
        finish(); // Return to Previous Page
    }

    public void click_radioButton_time(View view) {
        mySetting.setSort_type(SettingHolder.SETTING_TIME);
        s.setEnabled(true);
//        numStr = String.valueOf(sort_type);    // 값이 잘 들어오는지 확인용 Toast
//        Toast.makeText(this, numStr, Toast.LENGTH_SHORT).show();
    }

    public void click_radioButton_location(View view) {
        mySetting.setSort_type(SettingHolder.SETTING_LOCATION);
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

        startActivity(intent);
    }
    public void click_cloud_directory_setting(View view){
        Intent intent = new Intent(getApplicationContext(),QueryFilesInFolderActivity.class);
        startActivity(intent);
    }



    private ArrayList<String> getPathOfAllImages() {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);


        int lastIndex;
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(columnIndex);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;
            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                result.add(absolutePathOfImage);
            }
        }
        return result;
    }//정상작동 확인 18.11.18 LJH

    public String getCity(double latitude, double longitude) {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        String outputStr = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ljhtest", "GEOCODER GETFROMLOC 오류발생");
        }
        if (list != null) {
            if (list.size() == 0) {
                outputStr = "NoSuchLocation";
            } else {
                outputStr = list.get(0).getLocality();//나중에 대한민국, 서울특별시 이런식으로 하고 싶으면 여기 수정하면될듯
            }
        }
        return outputStr;
    }

    public String parseJson2(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONObject result = object.getJSONObject("result");
            String formatAddr = result.getString("formatted_address");
            JSONArray pois = result.getJSONArray("pois");
            if (pois.length() > 0) {
                return pois.getJSONObject(0).getString("addr");
            }
            return formatAddr;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLatFromExif(ExifInterface exif) {
        String str;
        str = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        return str;
    }


    public ArrayList<PhotoDatabase> getDatabase() {
        ArrayList<PhotoDatabase> db = new ArrayList<>();
        Cursor mManagedCursor;
        mManagedCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (mManagedCursor != null) {
            mManagedCursor.moveToFirst();
            int nSize = mManagedCursor.getColumnCount();
            while (true) {
                String bucket_display_name = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)); // 버킷의 이름
                String bucket_id = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)); // 버킷 ID
                long date_taken = mManagedCursor.getLong(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)); // 촬영날짜. 1/1000초 단위
                String description = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.DESCRIPTION)); // Image에 대한 설명
                String is_private = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.IS_PRIVATE)); // 공개 여부
                Double latitude =mManagedCursor.getDouble(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE)); // 위도
                Double longitude =mManagedCursor.getDouble( mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE)); // 경도
                String mini_thumb_magic = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC)); // 작은 썸네일
                String orientation = mManagedCursor.getString( mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)); // 사진의 방향. 0, 90, 180, 270
                String picasa_id =mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.PICASA_ID)); // 피카사에서 매기는 ID
                String id = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)); // 레코드의 PK
                String data = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)); // 데이터 스트림. 파일의 경로
                String title = mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)); // 제목
                String display_name = mManagedCursor.getString( mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)); // 파일 표시명
                String date_modified =mManagedCursor.getString(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED)); // 최후 갱신 날짜. 초단위
                Long date_added = mManagedCursor.getLong( mManagedCursor.getColumnIndex( MediaStore.Images.ImageColumns.DATE_ADDED)); // 추가 날짜. 초단위

                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(date_taken);
                Date d = cal.getTime();
                SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd");//date 포맷을 바꾸고 싶다면 수정. 여기 수정하면 연도별,월별,일별 수정 가능
                String date = sd.format(d);//극혐숫자로 반환된 datetaken을 날짜로 바꾸어주는식

                PhotoDatabase pdb = new PhotoDatabase();
                pdb.date=date;
                pdb.path=data;
                pdb.title=display_name;
                pdb.location=getCity(latitude,longitude);
                db.add(pdb);
                if (mManagedCursor.isLast()) {
                    break;
                } else {
                    mManagedCursor.moveToNext();
                }
            }

        }
        return db;
    }


}



