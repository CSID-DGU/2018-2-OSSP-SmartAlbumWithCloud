package com.zjianhao.album;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjianhao.bean.User;
import com.zjianhao.fragments.AlbumFragment;
import com.zjianhao.fragments.CloudFragment;
import com.zjianhao.fragments.PhotoFragment;
import com.zjianhao.service.UploadService;
import com.zjianhao.ui.AdvancedSearchAty;
import com.zjianhao.ui.AmbigiousSearchAty;
import com.zjianhao.ui.LoginAty;
import com.zjianhao.adapter.MainTabAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.zjianhao.R;
import com.zjianhao.utils.LogUtil;
import com.zjianhao.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener{

    @InjectView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @InjectView(R.id.main_tab_layout)
    TabLayout mainTabLayout;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.main_content)
    CoordinatorLayout mainContent;
    @InjectView(R.id.navigation_view)
    NavigationView navigationView;

    private ImageView logOutIv;
    private ImageView imgProfilePic;
    private TextView headUsername;
    private View googleLoginView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.take_photo_fb)
    FloatingActionButton takePhotoFb;
    private MainTabAdapter adapter;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private PhotoFragment photoFragment;
    private AlbumFragment albumFragment;
    private CloudFragment cloudFragment;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MediaScannerConnection msc;
    private ArrayList<String> selectPath = new ArrayList<>();

    private static boolean title_bool =false;
    /** Google Sign In **/
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient = null;
    private static GoogleSignInAccount mGoogleSignInAccount= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.inject(this);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainToolbar.setTitle(getString(R.string.album));
        mainToolbar.setTitleTextColor(Color.WHITE);
        mainToolbar.setNavigationIcon(R.drawable.ic_list_white);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBarDrawerToggle.syncState();
            }
        });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout.setDrawerListener(mActionBarDrawerToggle);

        init();
        callPermission();

        adapter = new MainTabAdapter(getSupportFragmentManager(), fragments, titles);
        viewpager.setAdapter(adapter);
        mainTabLayout.setupWithViewPager(viewpager);
        mainTabLayout.setTabMode(TabLayout.MODE_FIXED);

        View view = navigationView.inflateHeaderView(R.layout.drawer_head_view);
        navigationView.inflateMenu(R.menu.drawable_home_main);
        navigationView.setNavigationItemSelectedListener(this);
        final AppContext application = (AppContext) getApplication();
        final User user = application.getUser();

        /** Google Login Button Settings -by Ju Hun Choi **/
        googleLoginView = view.findViewById(R.id.google_sign_in_button);
        googleLoginView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signIn();
            }
        });

        /** Logout Button Settings - Ju Hun Choi */
        logOutIv = (ImageView)view.findViewById(R.id.log_out);
        logOutIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                updateUI();
                ToastUtil.show(MainActivity.this,"Logged Out");

            }
        });
        /****** End of Google Login *****/

        headUsername = (TextView)view.findViewById(R.id.head_username);
        imgProfilePic = (ImageView)view.findViewById(R.id.head_img);
        if(mGoogleSignInAccount == null){ // Not Logged In yet
            googleLoginView.setVisibility(View.VISIBLE);
            headUsername.setVisibility(View.INVISIBLE);
        }else { // Logged In
            headUsername.setText(mGoogleSignInAccount.getEmail());
            googleLoginView.setVisibility(View.INVISIBLE);
            headUsername.setVisibility(View.VISIBLE);
            /** Google Profile Image Settings - Ju Hun Choi*/
            Uri myuri = mGoogleSignInAccount.getPhotoUrl();
            if (myuri != null) {
                String personPhotoUrl = myuri.toString();
                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfilePic);

            }
        }
    }


    public void init() {
        /** Initial Title combine (only one time) */
        titles.add(getString(R.string.photo));
        titles.add(getString(R.string.album));
        titles.add(getString(R.string.cloud));
        photoFragment = new PhotoFragment();
        fragments.add(photoFragment);
        albumFragment = new AlbumFragment();
        fragments.add(albumFragment);
        cloudFragment = new CloudFragment();
        fragments.add(cloudFragment);


    }

    // Ask for Permission if not granted
    public void callPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int checkCallCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int checkCallLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(checkCallStoragePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0x01);
            }
            if(checkCallCameraPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},0x01);
            }
            if(checkCallLocationPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0x01);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtil.v(this,"request code:"+requestCode);
        switch (requestCode) {
            case 0x01:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this,"Failed to get storage permission, data cannot be read.",Toast.LENGTH_SHORT).show();
                int resultCoarsePermission = ContextCompat.checkSelfPermission(this ,Manifest.permission.CAMERA);
                if (resultCoarsePermission != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},0x02);

                break;
            case 0x02:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this,"Failed to take photo permission, photos cannot be taken.",Toast.LENGTH_SHORT).show();

                break;
            case 0x03:
                if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this, "Failed to get Location permission, Map view cannot be used.",Toast.LENGTH_SHORT).show();
                resultCoarsePermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
                if(resultCoarsePermission != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x03);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search_menu:
                Intent intent = new Intent(this, AmbigiousSearchAty.class);
                startActivity(intent);
                break;
            case R.id.share_menu:
               share();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.take_photo_fb)
    public void onClick() {
        //takePicture();
        Intent intent = new Intent(getApplicationContext(),AlbumMapActivity.class);
        startActivity(intent);
    }//take_photo 버튼을 그대로 가져다가 구글맵 액티비티 연결로 씀. 이후 사진이랑 이름 바꾸면댈듯?

    public void takePicture(){
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 0x01);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0x01:
                if (resultCode == Activity.RESULT_OK){
                   savePicture(data);
                }

                break;
            case 0x02:
                if(resultCode == RESULT_OK && data != null){

                    if(resultCode == RESULT_OK){
                        ArrayList<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        if (path.size()>0){
                            Intent intent = new Intent(this, UploadService.class);
                            intent.putStringArrayListExtra("upload_photos",path);
                            startService(intent);
                            ToastUtil.show(this,"Start Uploading...");
                        }
                        for (String s : path) {
                            LogUtil.v(MainActivity.this,s);
                        }
                    }

                }
                break;
            case RC_SIGN_IN : // Google Sign In
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePicture(Intent data) {
        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        final File filepath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/album/");
        if (!filepath.exists())
            filepath.mkdirs();
        final File file = new File(filepath,getDateImgName());
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        msc = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                msc.scanFile(filepath.getAbsolutePath(),"image/jpeg");

            }

            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });
        msc.connect();

    }


    public String getDateImgName(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
        return "IMG_"+format.format(new Date(System.currentTimeMillis()))+".jpg";

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (msc != null && msc.isConnected()){
            msc.disconnect();
            msc = null;

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
//        mActionBarDrawerToggle.syncState();
        drawerLayout.closeDrawers();
        switch (item.getItemId()){

            case R.id.nav_upload_photo:
                AppContext application = (AppContext) getApplication();
                if(mGoogleSignInClient != null){
                    /*


                    Google Sync 기능 넣기
                     */
                }else {
                    ToastUtil.show(this,"Please login first");
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    //Intent intent1 = new Intent(this,.class);
                    //startActivity(intent1);
                }


                break;
            case R.id.nav_search_photo:
                intent = new Intent(this, AdvancedSearchAty.class);
                startActivity(intent);

                break;
            case R.id.nav_share:
                share();
                break;
            case R.id.nav_take_picture:
                takePicture();
                break;
        }

        return false;
    }

    public void share() {

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share It");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_information));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "Share It"));

    }



    /** Google Sign In Actions - by Ju Hun Choi **/
    private void signIn(){
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }catch(Exception e){
            e.printStackTrace();
            Log.e("SignIn", "MainActivity-signin : Google Sign In Failed!!");
            return;
        }
        //goto handleSignInResult();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mGoogleSignInAccount = completedTask.getResult(ApiException.class);
            Log.w("GoogleLogIn", "signInResult:GoogleLoginSuccess");
            headUsername.setText(mGoogleSignInAccount.getEmail());
            googleLoginView.setVisibility(View.INVISIBLE);
            headUsername.setVisibility(View.VISIBLE);
            /** Google Profile Image Settings - Ju Hun Choi*/
            Uri myuri = mGoogleSignInAccount.getPhotoUrl();
            if (myuri != null) {
                String personPhotoUrl = myuri.toString();
                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfilePic);

            }
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleLogIn", "signInResult:failed code=" + e.getStatusCode());
        }finally{
            updateUI();
        }
    }
    // [END handleSignInResult]
    // [START signOut]
    private void signOut() {
        mGoogleSignInAccount = null;
        googleLoginView.setVisibility(View.VISIBLE);
        headUsername.setVisibility(View.INVISIBLE);
        Glide.with(getApplicationContext()).load(R.drawable.head_icon)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfilePic);
        updateUI();
    }
    // [END signOut]
    public void updateUI(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("GoogleSignInAccount", mGoogleSignInAccount);
        startService(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
