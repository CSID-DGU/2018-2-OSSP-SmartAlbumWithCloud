package com.zjianhao.album;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Spinner;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.zjianhao.R;
import com.zjianhao.holder.SettingHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
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


//https://developer.android.com/guide/components/services#Basics
public class FileUploaderService extends IntentService {
    DirFileManager dfm = new DirFileManager();
    private SettingHolder mySetting;
    private Spinner s;
    Map<String, ArrayList<PhotoDatabase>> photoListByLoc = new HashMap<>();
    Map<String, ArrayList<PhotoDatabase>> photoListByDate = new HashMap<>();
    Set<String> locations = new HashSet<>();
    Set<String> dates = new HashSet<>();
    ArrayList<PhotoDatabase> dbs = new ArrayList<>();


    private GoogleSignInAccount mSignInAccount;
    private DriveResourceClient mDriveResourceClient;
    private DriveClient mDriveClient;
    private DriveId myDriveId; // Main Folder
    private List<DriveId> childDriveList;
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public FileUploaderService(){

        super("FileUploaderService");
        myDriveId = settingActivity.myDriveId;
        mSignInAccount = MainActivity.mGoogleSignInAccount;
        mDriveClient = BaseDemoActivity.mDriveClient;
        mDriveResourceClient = BaseDemoActivity.mDriveResourceClient;
    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent){
        myDriveId = settingActivity.myDriveId;
        String folderId = myDriveId.asDriveFolder().toString();
        mySetting = settingActivity.mySetting;
        createFolder("Temp아아아");
        Log.d("FileUploaderService", "Normal Execution");


        // Sort Local Directory by Settings
        sortLocalDirectory();




    }


    /**
     * Create Folder
     * @param folderName
     */
    protected void createFolder(String folderName){

        mDriveResourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = myDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();
                    return mDriveResourceClient.createFolder(parentFolder, changeSet);
                });

    }

    protected void uploadFile(){

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

    private void sortLocalDirectory(){
        dbs = getDatabase(mySetting.getSort_time_type());
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

        // 추가~~~~~~~~~~~~~~
        switch (mySetting.getSort_type()) {
            case SETTING_TIME: // Sort Type 이 time 이면~
                switch (mySetting.getSort_time_type()) { // sub sort type을 점검한다
                    case SUB_SETTING_YEAR :
                        dfm.copyFileByMap(mySetting.getLocal_directory(), dates, photoListByDate); //절대경로 store/emulator/0 붙어있는거 때버려야할듯
                        break;
                    case SUB_SETTING_MONTH :
                        dfm.copyFileByMap(mySetting.getLocal_directory(), dates, photoListByDate); //절대경로 store/emulator/0 붙어있는거 때버려야할듯
                        break;
                    case SUB_SETTING_DAY :
                        dfm.copyFileByMap(mySetting.getLocal_directory(), dates, photoListByDate); //절대경로 store/emulator/0 붙어있는거 때버려야할듯
                        break;
                    default :
                        Log.d("Unexpected Error", "settingActivity sub_sort_type unselected");
                        break;
                }
                break;

            case SETTING_LOCATION :// Sort Type 이 location 이면~
                dfm.copyFileByMap(mySetting.getLocal_directory(), locations, photoListByLoc);
                break;

            default :
                Log.d("Unexpected Error", "settingActivity sort_type unselected");
                break;
        }
    }

    public ArrayList<PhotoDatabase> getDatabase(int sortType) {
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
                SimpleDateFormat sd;
                String date ="";
                switch(sortType)
                {
                    case SUB_SETTING_DAY:
                        sd = new SimpleDateFormat("yyyy.MM.dd");//date 포맷을 바꾸고 싶다면 수정. 여기 수정하면 연도별,월별,일별 수정 가능
                        date += sd.format(d);//극혐숫자로 반환된 datetaken을 날짜로 바꾸어주는식
                        break;
                    case SUB_SETTING_MONTH:
                        sd = new SimpleDateFormat("yyyy.MM");//date 포맷을 바꾸고 싶다면 수정. 여기 수정하면 연도별,월별,일별 수정 가능
                        date += sd.format(d);//극혐숫자로 반환된 datetaken을 날짜로 바꾸어주는식
                        break;
                    case SUB_SETTING_YEAR:
                        sd = new SimpleDateFormat("yyyy");
                        date += sd.format(d);//극혐숫자로 반환된 datetaken을 날짜로 바꾸어주는식
                        break;
                }

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
