package com.zjianhao.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.zjianhao.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.InjectView;

/**
 * Created by 张建浩（Clarence) on 2016-6-26 19:41.
 * the author's website:http://www.zjianhao.cn
 * the author's github: https://github.com/zhangjianhao
 */
public class Photo_dlAty extends AppCompatActivity {
    @InjectView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @InjectView(R.id.info_time_tv)
    TextView infoTimeTv;
    @InjectView(R.id.info_week)
    TextView infoWeek;
    @InjectView(R.id.info_filename)
    TextView infoFilename;
    @InjectView(R.id.info_size)
    TextView infoSize;
    @InjectView(R.id.info_filepath_tv)
    TextView infoFilepathTv;
    @InjectView(R.id.info_location_tv)
    TextView infoLocationTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dl);
    }

    public String getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public String parseJson(String json){
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

}