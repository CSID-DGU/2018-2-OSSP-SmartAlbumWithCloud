package com.zjianhao.album;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DirFileManager
{
    public static GoogleSignInAccount mSignInAccount;
    public static DriveResourceClient mDriveResourceClient;
    public static DriveClient mDriveClient;
    public static DriveId myDriveId; // Main Folder
    public void makeDir(String path, Set<String> strSet)
    {
        //String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //_path=sdpath+path;
        String _path = path;
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
    public void uploadFIleByMap(String path, Set<String> strSet, Map<String, ArrayList<PhotoDatabase>> map){
        myDriveId = settingActivity.myDriveId;
        mSignInAccount = MainActivity.mGoogleSignInAccount;
        mDriveClient = BaseDemoActivity.mDriveClient;
        mDriveResourceClient = BaseDemoActivity.mDriveResourceClient;
        myDriveId = settingActivity.myDriveId;
        String folderId = myDriveId.asDriveFolder().toString();

        for(String str : strSet)
        {
            DriveFolder df= createFolder(str);
            int size = map.get(str).size();
            for(int i = 0; i<size; i++)
            {
                String inPath = map.get(str).get(i).path;
                uploadFile(df, new File(inPath));
            }
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
                String outPath=path+"/"+str+"/"+map.get(str).get(i).title;
                copyFile(inPath,outPath);
            }
        }
    }

    /**
     * Create Folder
     * @param folderName
     */
    public DriveFolder createFolder(String folderName){
        DriveFolder df = null;
        DriveFolder parentFolder = myDriveId.asDriveFolder();
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(folderName)
                .setMimeType(DriveFolder.MIME_TYPE)
                .setStarred(false)
                .build();

        Task<DriveFolder> mTask = mDriveResourceClient.createFolder(parentFolder, changeSet);
        while(!mTask.isComplete()){
            try{
                wait(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        df = mTask.getResult();
        return df;
    }

    public void uploadFile(DriveFolder df , java.io.File fileContent){
        String dir = fileContent.getPath();

        final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveContents contents = createContentsTask.getResult();
                    DriveFolder parent = df;

                    InputStream is = new FileInputStream(new File(dir));
                    OutputStream os = contents.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    is.close();
                    os.close();

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(fileContent.getName())
                            //.setMimeType("jpg/plain")
                            .build();
                    Task<DriveFile> mtask = mDriveResourceClient.createFile(parent,changeSet,contents);
                    while(!mtask.isComplete()){
                        try{
                            wait(1000);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    return mtask;
                }).addOnSuccessListener(DriveFile->{
            Log.d("File Upload Success : ", dir);
        }).addOnFailureListener(DriveFile->{
            Log.e("File Upload Success : ", dir);
        });
    }
}
