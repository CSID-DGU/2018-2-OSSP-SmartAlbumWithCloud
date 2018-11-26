package com.zjianhao.album;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.zjianhao.R;

import java.util.List;


//https://developer.android.com/guide/components/services#Basics
public class FileUploaderService extends IntentService {
    private GoogleSignInAccount mSignInAccount;
    private DriveResourceClient mDriveResourceClient;
    private DriveClient mDriveClient;
    private DriveId myDriveId; // Main Folder
    private List<DriveId> childDriveList;
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public FileUploaderService(){

        super("FileUploaderService");
        myDriveId = settingActivity.myDriveId;
        mSignInAccount = MainActivity.mGoogleSignInAccount;
        mDriveClient = BaseDemoActivity.mDriveClient;
        mDriveResourceClient = BaseDemoActivity.mDriveResourceClient;
    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent){
        myDriveId = settingActivity.myDriveId;
        String folderId = myDriveId.asDriveFolder().toString();

        createFolder("아아아아아ㅏ아아아아");
        Log.d("FileUploaderService", "Normal Execution");





    }


    /**
     * Create Folder
     * @param folderName
     */
    protected void createFolder(String folderName){

        mDriveResourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = myDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(folderName)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();
                    return mDriveResourceClient.createFolder(parentFolder, changeSet);
                });

    }

    protected void uploadFile(){

    }
}
