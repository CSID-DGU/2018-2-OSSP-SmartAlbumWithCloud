/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zjianhao.album;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.widget.DataBufferAdapter;
import com.google.android.gms.tasks.Task;
import com.zjianhao.R;


/**
 * An activity that illustrates how to query files in a folder.
 */
public class QueryFilesInFolderActivity extends BaseDemoActivity  {
    private static final String TAG = "QueryFilesInFolder";

    private DataBufferAdapter<Metadata> mResultsAdapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_listfiles);
        ListView mListView = (ListView)findViewById(R.id.listViewResults);
        mResultsAdapter = new ResultsAdapter(this);
        mListView.setAdapter(mResultsAdapter);
    }

    @Override
    protected void onDriveClientReady() {
        pickFolder()
                .addOnSuccessListener(this,
                        driveId -> {
                            listFilesInFolder(driveId.asDriveFolder());
                            settingActivity.myDriveId=driveId;
                            finish(); // Return to Previous Activity - JuhunChoi
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No folder selected", e);
                    showMessage(getString(R.string.folder_not_selected));
                    finish();
                });
    }

    /**
     * Clears the result buffer to avoid memory leaks as soon
     * as the activity is no longer visible by the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        mResultsAdapter.clear();
    }

    /**
     * Retrieves results for the next page. For the first run,
     * it retrieves results for the first page.
     */
    private void listFilesInFolder(DriveFolder folder) {
        Log.d("Show",folder.toString());
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .build();
        // [START drive_android_query_children]
        Task<MetadataBuffer> queryTask = getDriveResourceClient().queryChildren(folder, query);
        // END drive_android_query_children]
        queryTask
                .addOnSuccessListener(this,
                        metadataBuffer -> mResultsAdapter.append(metadataBuffer))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error retrieving files", e);
                    finish();
                });

    }
}
