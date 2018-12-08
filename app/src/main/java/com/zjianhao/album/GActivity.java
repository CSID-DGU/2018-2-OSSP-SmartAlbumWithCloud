package com.zjianhao.album;

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
        for(PhotoDatabase pdb : photoDatabase)

        {
            testText +=pdb.path+"\n";
        }
        Toast.makeText(getApplicationContext(), testText, Toast.LENGTH_LONG).show();
        if(!(photoDatabase==null))
        for(PhotoDatabase pdb : photoDatabase)
            try {
                bitmapList.add(urlImageToBitmap(pdb.path));
                String text = pdb.path + "ADDED";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
                        .show();
          }
           catch(Exception e)
            {
                String text = "Exception Throwed";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
                        .show();
            }
        this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList));
    }

    private Bitmap urlImageToBitmap(String imageUrl) throws Exception {
        Bitmap result = null;
        URL url = new URL(imageUrl);
        if(url != null) {
            result = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        return result;
    }
}
