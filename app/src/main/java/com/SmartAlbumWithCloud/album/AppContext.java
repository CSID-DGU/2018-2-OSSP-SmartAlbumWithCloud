package com.SmartAlbumWithCloud.album;

import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import com.SmartAlbumWithCloud.bean.Album;
import com.SmartAlbumWithCloud.bean.User;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.SmartAlbumWithCloud.utils.LogUtil;

import java.io.File;
import java.util.List;


public class AppContext extends Application {
    private static final String TAG = AppContext.class.getSimpleName();
    private static final String APP_CACAHE_DIRNAME = "/webcache";

    //singleton
    private static AppContext appContext = null;
    private Display display;

    private List<Album> albums;

    private Album cameraAlbum;

    public Album getCameraAlbum() {
        return cameraAlbum;
    }

    public void setCameraAlbum(Album cameraAlbum) {
        this.cameraAlbum = cameraAlbum;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.isDebug(true);
        appContext = this;
        init();
    }


    public User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static AppContext getInstance() {
        return appContext;
    }

    /**
     * Initialization
     */
    private void init() {
        initImageLoader(getApplicationContext());
        //Local Image Helper Class Initialization
        if (display == null) {
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }
    }


    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCacheSize((int) Runtime.getRuntime().maxMemory() / 4);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //Modify the connection timeout time of 5 seconds，Download timeout 5 seconds
        config.imageDownloader(new BaseImageDownloader(appContext, 5 * 1000, 5 * 1000));
        //		config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }
    /**
     * @return
     * @Description： Get the width of the current screen
     */
    public int getWindowWidth() {
        return display.getWidth();
    }

    /**
     * @return
     * @Description： Get the height of the current screen
     */
    public int getWindowHeight() {
        return display.getHeight();
    }

    /**
     * @return
     * @Description： Get the current screen half width
     */
    public int getHalfWidth() {
        return display.getWidth() / 2;
    }

    /**
     * @return
     * @Description： Get the current screen 1/4 width
     */
    public int getQuarterWidth() {
        return display.getWidth() / 4;
    }
}
