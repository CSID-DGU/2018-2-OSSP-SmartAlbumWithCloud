package com.zjianhao.album;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;


//https://developer.android.com/guide/components/services#Basics
public class FileUploaderService extends IntentService {
    private DriveId myDriveId;
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public FileUploaderService(){
       super("FileUploaderService");
        myDriveId = settingActivity.myDriveId;
    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent){

        String folderId = myDriveId.asDriveFolder().toString();






    }
}
