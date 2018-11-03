package com.SmartAlbumWithCloud.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 张建浩（Clarence) on 2016-7-2 16:57.
 * the author's website:http://www.zjianhao.cn
 * the author's github: https://github.com/zhangjianhao
 */
public class ImgUtil {
    public static File compressImage(String filePath, String targetPath, int quality)  {
        Bitmap bm = getSmallBitmap(filePath);
        // Rotate the photo angle, omit
        /*int degree = readPictureDegree(filePath);
        if(degree!=0){
            bm=rotateBitmap(bm,degree);
        }*/
        File outputFile=new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            }else{
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
        }catch (Exception e){}
        return outputFile;
    }

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// Only parse the edge of the image to get the width and height
        BitmapFactory.decodeFile(filePath, options);
        // Calculating the zoom ratio
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Full parsing image returns bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int i, int i1) {
        int w = options.outWidth;
        int h = options.outHeight;
        float hh = 800f;//Set the height here is 800f
        float ww = 480f;//Set the width here is 480f
        // Zoom Ratio. Since it is a fixed scale scaling, only one of the data of height or width can be used for calculation.
        int be = 1;//be=1 does not scale
        if (w > h && w > ww) {// if the width is large, scale according to the fixed width
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {// If the height is high, scale according to the fixed width
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        return be;
    }

}
