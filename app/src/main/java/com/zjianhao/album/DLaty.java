package com.zjianhao.album;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjianhao.R;
import com.zjianhao.bean.Photo;
import com.zjianhao.view.TouchImageView;

import org.json.JSONArray;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.io.*;
import java.net.*;

public class DLaty  extends AppCompatActivity {
    @InjectView(R.id.selected_image)
    TouchImageView selectedimage;

    private ImageLoader imageloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlaty);
        // Show Image
        ButterKnife.inject(this);

    }
    @OnClick (R.id.post_img)
    protected void OnclickUploadImage(){
        imageloader = ImageLoader.getInstance();
        Photo photo = (Photo)this.getIntent().getExtras().get("photo");


        Uri uri = Uri.parse(photo.getImgUrl());
        Log.e("ERROR : ",uri.getPath());
        imageloader.displayImage(uri.getPath(), selectedimage);

        AndroidNetworking.initialize(getApplicationContext());


        AndroidNetworking.post("http://210.94.194.48:8080/")
                //.addJSONObjectBody(jsonObject) // posting json
                .addFileBody(new File(photo.getLocation()))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Successful POST :" ,photo.getLocation());
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("Unsuccessful POST!!! :" ,photo.getLocation());
                    }
                });

    }
//    JSONObject jsonObject = new JSONObject();
//try {
//        jsonObject.put("firstname", "Rohit");
//        jsonObject.put("lastname", "Kumar");
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }



}
