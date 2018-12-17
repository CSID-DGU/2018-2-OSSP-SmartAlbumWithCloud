package com.zjianhao.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjianhao.R;

import java.io.File;
import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity {
    ArrayList<PhotoDatabase> photoDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        Intent i = getIntent();
        int position = i.getExtras().getInt("key");
        photoDatabase = i.getParcelableArrayListExtra("list");
        //Toast.makeText(getApplicationContext(), "" + position+", "+photoDatabase.get(position).path,Toast.LENGTH_SHORT).show();
        Bitmap bitmap = BitmapFactory.decodeFile(photoDatabase.get(position).path);
        BitmapFactory.decodeFile(photoDatabase.get(position).path);
        ImageView imgView = (ImageView)findViewById(R.id.imageView4);
        imgView.setImageBitmap(bitmap);
        TextView textView = (TextView)findViewById(R.id.fulltextview);
        String text = "Title: "+photoDatabase.get(position).title+"\nDate : "+photoDatabase.get(position).date;
        textView.setText(text);

    }
}
