package com.zjianhao.album;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DbHelper extends Context {



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



    @Override
    public AssetManager getAssets() {
        return null;
    }

    @Override
    public Resources getResources() {
        return null;
    }

    @Override
    public PackageManager getPackageManager() {
        return null;
    }

    @Override
    public ContentResolver getContentResolver() {
        return null;
    }

    @Override
    public Looper getMainLooper() {
        return null;
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void setTheme(int i) {

    }

    @Override
    public Resources.Theme getTheme() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return null;
    }

    @Override
    public String getPackageResourcePath() {
        return null;
    }

    @Override
    public String getPackageCodePath() {
        return null;
    }

    @Override
    public SharedPreferences getSharedPreferences(String s, int i) {
        return null;
    }

    @Override
    public FileInputStream openFileInput(String s) throws FileNotFoundException {
        return null;
    }

    @Override
    public FileOutputStream openFileOutput(String s, int i) throws FileNotFoundException {
        return null;
    }

    @Override
    public boolean deleteFile(String s) {
        return false;
    }

    @Override
    public File getFileStreamPath(String s) {
        return null;
    }

    @Override
    public File getFilesDir() {
        return null;
    }

    @Override
    public File getNoBackupFilesDir() {
        return null;
    }

    @Nullable
    @Override
    public File getExternalFilesDir(@Nullable String s) {
        return null;
    }

    @Override
    public File[] getExternalFilesDirs(String s) {
        return new File[0];
    }

    @Override
    public File getObbDir() {
        return null;
    }

    @Override
    public File[] getObbDirs() {
        return new File[0];
    }

    @Override
    public File getCacheDir() {
        return null;
    }

    @Override
    public File getCodeCacheDir() {
        return null;
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        return null;
    }

    @Override
    public File[] getExternalCacheDirs() {
        return new File[0];
    }

    @Override
    public File[] getExternalMediaDirs() {
        return new File[0];
    }

    @Override
    public String[] fileList() {
        return new String[0];
    }

    @Override
    public File getDir(String s, int i) {
        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory) {
        return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory, @Nullable DatabaseErrorHandler databaseErrorHandler) {
        return null;
    }

    @Override
    public boolean deleteDatabase(String s) {
        return false;
    }

    @Override
    public File getDatabasePath(String s) {
        return null;
    }

    @Override
    public String[] databaseList() {
        return new String[0];
    }

    @Override
    public Drawable getWallpaper() {
        return null;
    }

    @Override
    public Drawable peekWallpaper() {
        return null;
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return 0;
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return 0;
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {

    }

    @Override
    public void setWallpaper(InputStream inputStream) throws IOException {

    }

    @Override
    public void clearWallpaper() throws IOException {

    }

    @Override
    public void startActivity(Intent intent) {

    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle bundle) {

    }

    @Override
    public void startActivities(Intent[] intents) {

    }

    @Override
    public void startActivities(Intent[] intents, Bundle bundle) {

    }

    @Override
    public void startIntentSender(IntentSender intentSender, @Nullable Intent intent, int i, int i1, int i2) throws IntentSender.SendIntentException {

    }

    @Override
    public void startIntentSender(IntentSender intentSender, @Nullable Intent intent, int i, int i1, int i2, @Nullable Bundle bundle) throws IntentSender.SendIntentException {

    }

    @Override
    public void sendBroadcast(Intent intent) {

    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String s) {

    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String s) {

    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String s, @Nullable BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s1, @Nullable Bundle bundle) {

    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle) {

    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle, @Nullable String s) {

    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, @Nullable String s, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s1, @Nullable Bundle bundle) {

    }

    @Override
    public void sendStickyBroadcast(Intent intent) {

    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s, @Nullable Bundle bundle) {

    }

    @Override
    public void removeStickyBroadcast(Intent intent) {

    }

    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {

    }

    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, BroadcastReceiver broadcastReceiver, @Nullable Handler handler, int i, @Nullable String s, @Nullable Bundle bundle) {

    }

    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {

    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        return null;
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, @Nullable String s, @Nullable Handler handler) {
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {

    }

    @Nullable
    @Override
    public ComponentName startService(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent intent) {
        return false;
    }

    @Override
    public boolean bindService(Intent intent, @NonNull ServiceConnection serviceConnection, int i) {
        return false;
    }

    @Override
    public void unbindService(@NonNull ServiceConnection serviceConnection) {

    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName componentName, @Nullable String s, @Nullable Bundle bundle) {
        return false;
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String s) {
        return null;
    }

    @Nullable
    @Override
    public String getSystemServiceName(@NonNull Class<?> aClass) {
        return null;
    }

    @Override
    public int checkPermission(@NonNull String s, int i, int i1) {
        return 0;
    }

    @Override
    public int checkCallingPermission(@NonNull String s) {
        return 0;
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String s) {
        return 0;
    }

    @Override
    public int checkSelfPermission(@NonNull String s) {
        return 0;
    }

    @Override
    public void enforcePermission(@NonNull String s, int i, int i1, @Nullable String s1) {

    }

    @Override
    public void enforceCallingPermission(@NonNull String s, @Nullable String s1) {

    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String s, @Nullable String s1) {

    }

    @Override
    public void grantUriPermission(String s, Uri uri, int i) {

    }

    @Override
    public void revokeUriPermission(Uri uri, int i) {

    }

    @Override
    public int checkUriPermission(Uri uri, int i, int i1, int i2) {
        return 0;
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int i) {
        return 0;
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int i) {
        return 0;
    }

    @Override
    public int checkUriPermission(@Nullable Uri uri, @Nullable String s, @Nullable String s1, int i, int i1, int i2) {
        return 0;
    }

    @Override
    public void enforceUriPermission(Uri uri, int i, int i1, int i2, String s) {

    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int i, String s) {

    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int i, String s) {

    }

    @Override
    public void enforceUriPermission(@Nullable Uri uri, @Nullable String s, @Nullable String s1, int i, int i1, int i2, @Nullable String s2) {

    }

    @Override
    public Context createPackageContext(String s, int i) throws PackageManager.NameNotFoundException {
        return null;
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration configuration) {
        return null;
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        return null;
    }
}
