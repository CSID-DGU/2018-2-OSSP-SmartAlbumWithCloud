package com.zjianhao.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.BaseAdapter;

import java.util.Hashtable;
import java.util.Stack;

public class LjhImageLoader {

    Hashtable<Integer, Bitmap> loadImages;
    Hashtable<Integer, String> positionRequested;
    BaseAdapter listener;
    int runningCount = 0;
    Stack<ItemPair> queue;
    ContentResolver resolver;

    public LjhImageLoader(ContentResolver r) {

        loadImages = new Hashtable<Integer, Bitmap>();
        positionRequested = new Hashtable<Integer, String>();
        queue = new Stack<ItemPair>();
        resolver = r;
    }

    public void setListener(BaseAdapter adapter) {
        listener = adapter;
        reset();
    }

    public void reset() {

        positionRequested.clear();
        runningCount = 0;
        queue.clear();
    }

    public Bitmap getImage(int uid, String path) {
        Bitmap image = loadImages.get(uid);
        if( image != null )
            return image;
        if( !positionRequested.containsKey(uid) ) {
            positionRequested.put(uid, path);
            if( runningCount >= 15 ) {
                queue.push(new ItemPair(uid, path));
            }
            else {
                runningCount++;
                new LoadImageAsyncTask().execute(uid, path);
            }
        }
        return null;
    }

    public void getNextImage() {
        if( !queue.isEmpty() ) {
            ItemPair item = queue.pop();
            new LoadImageAsyncTask().execute(item.uid, item.path);
        }
    }

    public class LoadImageAsyncTask extends AsyncTask<Object, Void, Bitmap> {

        Integer uid;

        @Override
        protected Bitmap doInBackground(Object... params) {
            this.uid = (Integer) params[0];
            String path = (String) params[1];
            String[] proj = { MediaStore.Images.Thumbnails.DATA };
            Bitmap micro = MediaStore.Images.Thumbnails.getThumbnail(resolver, uid, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            if( micro != null ) {
                return micro;
            }
            else {
                Cursor mini = MediaStore.Images.Thumbnails.queryMiniThumbnail(resolver, uid, MediaStore.Images.Thumbnails.MINI_KIND, proj);
                if( mini != null && mini.moveToFirst() ) {

                    path = mini.getString(mini.getColumnIndex(proj[0]));
                }
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            if( options.outWidth > 96 ) {
                int ws = options.outWidth / 96 + 1;
                if( ws > options.inSampleSize )
                    options.inSampleSize = ws;
            }
            if( options.outHeight > 96 ) {

                int hs = options.outHeight / 96 + 1;
                if( hs > options.inSampleSize )
                    options.inSampleSize = hs;
            }
            return BitmapFactory.decodeFile(path, options);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            runningCount--;
            if( result != null ) {
                loadImages.put(uid, result);
                listener.notifyDataSetChanged();
                getNextImage();
            }
        }
    }
    public static class ItemPair {
        Integer uid;
        String path;
        public ItemPair(Integer uid, String path) {
            this.uid = uid;
            this.path = path;
        }
    }
}