package com.zjianhao.album;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.CreateFileActivityOptions;
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
import com.zjianhao.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirFileManager
{
    public static Context context;
    public static GoogleSignInAccount mSignInAccount;
    public static DriveResourceClient mDriveResourceClient;
    public static DriveClient mDriveClient;
    public static DriveId myDriveId; // Main Folder
    private static List<String> Name;
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
            File from = new File(_from);
            File to = new File(_to);
            from.renameTo(to);

            String oldFilePath = to.getAbsolutePath().replace(_from, _to);
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, to.getAbsolutePath());
            int rows = context.getContentResolver().update(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values,
                    MediaStore.MediaColumns.DATA + "='" + oldFilePath + "'", null
            );
            MediaScannerConnection.scanFile(context,
                    new String[] { _from }, null, null);
            MediaScannerConnection.scanFile(context,
                    new String[]{_to}, null, null);

            wait(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void copyFileByMap(String path, Set<String> strSet, Map<String,ArrayList<PhotoDatabase>> map)//path는 makeDir에 넣었던 그대로
    {
        myDriveId = settingActivity.myDriveId;
        mSignInAccount = MainActivity.mGoogleSignInAccount;
        mDriveClient = BaseDemoActivity.mDriveClient;
        mDriveResourceClient = BaseDemoActivity.mDriveResourceClient;
        myDriveId = settingActivity.myDriveId;

        makeDir(path,strSet);

        for(String str : strSet)
        {
            ArrayList<String> list = new ArrayList<String>();
            DriveFolder df= createFolder(str);
            int size = map.get(str).size();
            for(int i = 0; i<size; i++)
            {

                String inPath = map.get(str).get(i).path;
                String outPath=path+"/"+str+"/"+map.get(str).get(i).title;
                if(list.contains(map.get(str).get(i).title) == false) {
                    if (inPath.equals(outPath) == false) { // Filter out same file
                        list.add(map.get(str).get(i).title);
                        copyFile(inPath, outPath);
                        uploadFile(df, new File(outPath));
                        Log.d("DIRDIR:", inPath + "\t\t" + outPath);
                    }
                }
            }
        }
        deleteEmptyFolder(new File(path));
    }

    /**
     * Create Folder
     * @param folderName
     */
    /**
     * Create Folder
     * @param folderName
     */
    protected DriveFolder createFolder(String folderName){
        DriveFolder newId = null;
        Task<DriveFolder> mtask = mDriveResourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = myDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();

                    return mDriveResourceClient.createFolder(parentFolder, changeSet);
                }).addOnSuccessListener(DriveResourceClient->{

        });
        while(!mtask.isComplete());
        newId = mtask.getResult();
        return newId;
    }

    /**
     * Upload one file to Google Drive within DriveFolder - Juhun Choi
     * @param df
     * @param fileContent
     */
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
                    return null;
                }).addOnSuccessListener(DriveFile->{
            Log.d("File Upload Success : ", dir);
        }).addOnFailureListener(DriveFile->{
            Log.e("File Upload Success : ", dir);
        });
    }

    /**
     * Delete Empty Folders within root Folder - Juhun Choi
     * @param rootFolder
     */
    public static void deleteEmptyFolder(File rootFolder){
        if (!rootFolder.isDirectory()) return;

        File[] childFiles = rootFolder.listFiles();
        if (childFiles==null) return;
        if (childFiles.length == 0){
            rootFolder.delete();
        } else {
            for (File childFile : childFiles){
                deleteEmptyFolder(childFile);
            }
        }
    }
}
