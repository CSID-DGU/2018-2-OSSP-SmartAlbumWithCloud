package com.zjianhao.LJHdata;

import com.zjianhao.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapData extends Activity
{
    private Map<String,List<String>> photoListByLoc = new HashMap<>();
    List<String> locs = new ArrayList<>();
    List<String> AllPathes = new ArrayList<>();
    private static final String TAG = "TEST1";
    static String city;

public String getCity(double latitude, double longitude)
    {

        LogUtil.isDebug(true);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://api.map.baidu.com/geocoder/v2/?ak=F4T8ucQkc9l7C10PLW38VtSM&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=1&coordtype=wgs84ll&mcode=73:5B:FC:73:85:F9:C4:05:21:49:82:87:21:61:5E:BF:87:FD:42:17;com.zjianhao.baiddupoi").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                String addr = parseJson2(string.substring(string.indexOf("(")+1,string.length()-1));
                Log.d(TAG,addr);
                city=addr;
            }
        });
        return city;
    }

    public String parseJson2(String json){
        try {
            JSONObject object = new JSONObject(json);
            JSONObject result = object.getJSONObject("result");
            String formatAddr = result.getString("formatted_address");
            JSONArray pois = result.getJSONArray("pois");
            if (pois.length()>0){
                return pois.getJSONObject(0).getString("addr");
            }
            return formatAddr;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getLocationsFromAlbum()
    {
    Uri uri  = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
    }

    private ArrayList<String> getPathOfAllImages()
    {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

        int lastIndex;
        while (cursor.moveToNext())
        {
            String absolutePathOfImage = cursor.getString(columnIndex);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;
            if (!TextUtils.isEmpty(absolutePathOfImage))
            {
                result.add(absolutePathOfImage);
            }
        }
        for (String string : result)
        {
            Log.i("getPathOfAllImages", "|" + string + "|");
        }
        return result;
    }
}
