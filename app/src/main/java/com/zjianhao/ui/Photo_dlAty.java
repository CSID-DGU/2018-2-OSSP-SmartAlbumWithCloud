package com.zjianhao.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjianhao.R;
import com.zjianhao.bean.Photo;
import com.zjianhao.view.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 张建浩（Clarence) on 2016-6-26 19:41.
 * the author's website:http://www.zjianhao.cn
 * the author's github: https://github.com/zhangjianhao
 */
public class Photo_dlAty extends AppCompatActivity {
    @InjectView(R.id.selected_photo)
    TouchImageView selected_photo;
    @InjectView(R.id.photo_name)
    TextView photoName;

    private Photo photo;
    private ImageLoader imageloader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dl);
        ButterKnife.inject(this);
        imageloader = ImageLoader.getInstance();
        photo = getIntent().getParcelableExtra("photo");
        imageloader.displayImage(photo.getImgUrl(), selected_photo);
        photoName.setText(photo.getName());
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, R.anim.activity_exit_anim);
        }
        return super.onKeyDown(keyCode, event);
    }
    @OnClick({R.id.photo_send, R.id.photo_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_back:
                Log.e("Going Back : ", "photo_deail_back");
                finish();
                overridePendingTransition(0, R.anim.activity_exit_anim);
                break;
            case R.id.photo_send:
                Log.e("Sending : ", photo.getLocation());


                break;
            default:
                Log.e("Something Wrong", "ERROR");

        }
    }
    public void shareSingleImage() {
        // Get uri from file
        String url = photo.getImgUrl();
        Uri imageUri  = Uri.parse(photo.getImgUrl());

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }


}