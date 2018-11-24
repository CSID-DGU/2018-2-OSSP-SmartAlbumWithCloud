package com.zjianhao.LJHdata;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DirFileManager
{






    public void makeDir(String path, Set<String> strSet)
    {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String _path = sdPath+path;
        for(String str : strSet)
        {

            String localPath=_path+"/"+str; //장소값을 리턴받을 때 끝에 /가 있을 지 없을 지 몰라서 넣었어. 끝에 있으면 지우면됨
            File file = new File(localPath);
            file.mkdirs();
        }
    }//string set을 받아와서 path에(기본적으로 내부스토리지는 잡혀있음. 그니까 path에 /DCIM 이라고 넣으면 됨)
     // string set의 이름을 가진 폴더들 생성

        public void copyFile(String _from,String _to)
        {
            try {
                FileInputStream inputStream = new FileInputStream(_from);
                FileOutputStream outputStream = new FileOutputStream(_to);
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void copyFileByMap(String path, Set<String> strSet, Map<String,ArrayList<PhotoDatabase>> map)//path는 makeDir에 넣었던 그대로
        {
            makeDir(path,strSet);

            for(String str : strSet)
            {
                int size = map.get(str).size();
                for(int i = 0; i<size; i++)
                {
                    String inPath = map.get(str).get(i).path;
                    String outPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    outPath+=path+"/"+str+"/"+map.get(str).get(i).title;
                    copyFile(inPath,outPath);
                }
            }
        }


}
