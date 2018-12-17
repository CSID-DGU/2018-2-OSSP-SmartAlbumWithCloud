package com.zjianhao.album;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zjianhao.R;
import com.zjianhao.adapter.ImageAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;

import java.io.IOException;
import java.util.*;
import java.net.*;

import android.view.Display;
import android.view.View;
import android.widget.*;
public class GActivity extends AppCompatActivity {

    GActivity(){}
    GActivity(ArrayList<PhotoDatabase> pdb){
        photoDatabase=pdb;
    }
    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList;
   ArrayList<PhotoDatabase> photoDatabase=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g);
        this.imageGrid = (GridView) findViewById(R.id.gridview);
        this.bitmapList = new ArrayList<Bitmap>();
        photoDatabase = getIntent().getParcelableArrayListExtra("key");
        String testText ="";

        if(!(photoDatabase==null))
        for(PhotoDatabase pdb : photoDatabase)
            try {
                bitmapList.add(urlImageToBitmap(pdb.path));
                //String text = pdb.path + "ADDED";
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
          }
           catch(Exception e)
            {
                //String text = "Exception Throwed : " + e.getMessage();
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
//                        .show();
            }

            this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));
            this.imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {

               // Toast.makeText(getApplicationContext(), "" + position,Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(GActivity.this, FullImageActivity.class);
                //Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                intent1.putExtra("key", position);
                //i.putExtra("markertitle", title);
                intent1.putParcelableArrayListExtra("list",photoDatabase);
                startActivity(intent1);

            }
        });
    }





    private Bitmap urlImageToBitmap(String imageUrl) throws Exception {
        Bitmap result = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(imageUrl,options);
        result = Bitmap.createScaledBitmap(bmp,250,250,true);//썸네일사진의 해상도 조절
        return result;
    }
}
