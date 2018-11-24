package com.zjianhao.LJHdata;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjianhao.R;

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

public class ljhtestActivity extends AppCompatActivity {

    static String city = "default";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ljhtest);
        String teststr = "";
        ArrayList<String> testArrList = new ArrayList<>();
        ArrayList<String> titleArray = new ArrayList<>();
        testArrList = getPathOfAllImages();

        Map<String, ArrayList<PhotoDatabase>> photoListByLoc = new HashMap<>();
        Map<String, ArrayList<PhotoDatabase>> photoListByDate = new HashMap<>();

        Map<String, ArrayList<String>> fileToLocMap = new HashMap<>();
        Set<String> locations = new HashSet<>();
        Set<String> dates = new HashSet<>();

        List<String> ljhTest = new ArrayList<>();
        ArrayList<PhotoDatabase> dbs = new ArrayList<>();
        dbs = getDatabase();

        for(PhotoDatabase pdb : dbs)
        {
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



        String tttest = "";
        /*for(String loc : locations) {

            int ssize = photoListByLoc.get(loc).size();

            tttest+=loc+" : " + ssize + "개\n";
            for(int i = 0; i<ssize ; i++)
            tttest+=" " +photoListByLoc.get(loc).get(i).date;

            tttest+="\n";
        }//테스트용 코드*/
        DirFileManager dfm = new DirFileManager();
       //dfm.copyFileByMap("/DCIM/장소분류테스트",locations,photoListByLoc);//정상구동확인 BY LJH 18.11.24
        dfm.copyFileByMap("/DCIM/날짜별분류테스트",dates,photoListByDate);
        TextView tv = new TextView(this);
        tv.setText(tttest);
        ScrollView sv = new ScrollView(this);
        sv.addView(tv);
        setContentView(sv);
    }

    private ArrayList<String> getPathOfAllImages() {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = Images.Media.EXTERNAL_CONTENT_URI;
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
        mManagedCursor = getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (mManagedCursor != null) {
            mManagedCursor.moveToFirst();
            int nSize = mManagedCursor.getColumnCount();
            while (true) {
                String bucket_display_name = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.BUCKET_DISPLAY_NAME)); // 버킷의 이름
                String bucket_id = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.BUCKET_ID)); // 버킷 ID
                long date_taken = mManagedCursor.getLong(mManagedCursor.getColumnIndex(Images.ImageColumns.DATE_TAKEN)); // 촬영날짜. 1/1000초 단위
                String description = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.DESCRIPTION)); // Image에 대한 설명
                String is_private = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.IS_PRIVATE)); // 공개 여부
                Double latitude =mManagedCursor.getDouble(mManagedCursor.getColumnIndex(Images.ImageColumns.LATITUDE)); // 위도
                Double longitude =mManagedCursor.getDouble( mManagedCursor.getColumnIndex(Images.ImageColumns.LONGITUDE)); // 경도
                String mini_thumb_magic = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.MINI_THUMB_MAGIC)); // 작은 썸네일
                String orientation = mManagedCursor.getString( mManagedCursor.getColumnIndex(Images.ImageColumns.ORIENTATION)); // 사진의 방향. 0, 90, 180, 270
                String picasa_id =mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.PICASA_ID)); // 피카사에서 매기는 ID
                String id = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns._ID)); // 레코드의 PK
                String data = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.DATA)); // 데이터 스트림. 파일의 경로
                String title = mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.TITLE)); // 제목
                String display_name = mManagedCursor.getString( mManagedCursor.getColumnIndex(Images.ImageColumns.DISPLAY_NAME)); // 파일 표시명
                String date_modified =mManagedCursor.getString(mManagedCursor.getColumnIndex(Images.ImageColumns.DATE_MODIFIED)); // 최후 갱신 날짜. 초단위
                Long date_added = mManagedCursor.getLong( mManagedCursor.getColumnIndex( Images.ImageColumns.DATE_ADDED)); // 추가 날짜. 초단위

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

