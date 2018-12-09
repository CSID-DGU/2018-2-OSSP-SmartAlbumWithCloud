package com.zjianhao.album;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zjianhao.R;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import static java.security.AccessController.getContext;


public class AlbumMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Map<String, ArrayList<PhotoDatabase>> photoListByLoc = new HashMap<>();
    Set<String> locations = new HashSet<>();
    Set<String> usedLocations = new HashSet<>();
    Set<PhotoDatabase> mainPicturesByLoc = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void clickClose(View v)
    {
        Intent intent = new Intent(this,GActivity.class);
        startActivity(intent);
    }//테스트용 액티비티와 연결되게 함.

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<PhotoDatabase> dbs = new ArrayList<>();
        dbs = getDatabase();
        for(PhotoDatabase pdb : dbs)
        {
            if(pdb.location==null)
                pdb.location="NoSuchLocation";
            locations.add(pdb.location);
        }
        for(String loc : locations) {
            ArrayList<PhotoDatabase> mapArr = new ArrayList();
            for (PhotoDatabase pdb : dbs) {
                if (pdb.location.equals(loc)) {
                    mapArr.add(pdb);
                }
            }
            photoListByLoc.put(loc, mapArr);

            for(PhotoDatabase pdb : dbs) {
                if(pdb.location.equals(loc)){
                    if(!usedLocations.contains(loc)) {
                        usedLocations.add(loc);
                        mainPicturesByLoc.add(pdb);
                    }
                }
            }

        }
        mMap = googleMap;
        LatLng seoul = new LatLng(37.5574813,126.9998631);//동국대좌표
        for(PhotoDatabase pdb : mainPicturesByLoc)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(pdb.latitude,pdb.longitude));
            markerOptions.title(pdb.location);
            markerOptions.snippet(Integer.toString(photoListByLoc.get(pdb.location).size())+"개의 사진들");
            Bitmap bm=createUserBitmap(pdb.path);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));
            mMap.addMarker(markerOptions).showInfoWindow();
        }

        //mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul")); <--마커를 찍고 싶다면 사
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,8));//첫번째 인자는 중심이 되는 좌표, 두번째 인자는 줌 레벨
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                Intent intent1 = new Intent(AlbumMapActivity.this, GActivity.class);
                String title = marker.getTitle();
                intent1.putExtra("markertitle", title);
                intent1.putParcelableArrayListExtra("key",photoListByLoc.get(marker.getTitle()));
                startActivity(intent1);
               String text = "Map.get("+ marker.getTitle() + ") called..";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                return false;
            }
        });
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
                int id = mManagedCursor.getInt(mManagedCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)); // 레코드의 PK
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
                pdb.id = id;
                pdb.date=date;
                pdb.path=data;
                pdb.title=display_name;
                pdb.location=getCity(latitude,longitude);
                pdb.longitude = longitude;
                pdb.latitude = latitude;
                pdb.thumb=mini_thumb_magic;
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


    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;
        if(width > height)
        {
            if(maxResolution < width)
            {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < height)
            {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    private Bitmap createUserBitmap(String path) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            Bitmap bitmap = BitmapFactory.decodeFile(path); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {}
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }
    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

}
