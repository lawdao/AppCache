package cc.fussen.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cc.fussen.cache.StreamUtil.readListStream;
import static cc.fussen.cache.StreamUtil.readStream;
import static cc.fussen.cache.StreamUtil.writeToStream;
import static cc.fussen.cache.Util.getAppVersion;
import static cc.fussen.cache.Util.getCacheDir;
import static cc.fussen.cache.Util.requireNonNull;


/**
 * Created by Fussen on 2017/12/4.
 */

public class CacheManager {


    private DiskLruCache mDiskLruCache;

    private String path;

    private Context context;

    private int maxSize = 20;//unit :m

    private Set<SaveImageTask> taskCollection;

    private static final Map<String, DiskLruCache> cacheDiskLru = new HashMap<>();


    public CacheManager(Context context) {
        this.context = context;

        if (path==null){
            path = getCacheDir(context);
        }

        if (taskCollection == null) {
            taskCollection = new HashSet<>();
        }

        initCache(context);
    }


    private void initCache(Context context) {

        try {
            File cacheDir = new File(path);

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            if (mDiskLruCache == null || (mDiskLruCache != null && !mDiskLruCache.getDirectory().getPath().toString().equals(path))) {

                if (cacheDiskLru.containsKey(path)) {
                    mDiskLruCache = cacheDiskLru.get(path);
                    return;
                }

                mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, maxSize * 1024 * 1024);
                cacheDiskLru.put(path, mDiskLruCache);
                System.out.println("......create DiskLruCache......");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * set cache path
     *
     * @param path path
     * @return CacheManager
     */
    public CacheManager setPath(String path) {

        this.path = path;
        initCache(context);
        return this;
    }


    /**
     * save cache
     *
     * @param key    key
     * @param object object
     * @return is
     */
    public boolean saveCache(String key, Object object) {


        requireNonNull(object, "cache data can't be null");

        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(getKey(key));

            OutputStream fos = edit.newOutputStream(0);

            if (writeToStream(fos, object)) {
                edit.commit();
            } else {
                edit.abort();
            }
            mDiskLruCache.flush();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * save image cache
     *
     * @param imageUrl imageUrl
     */
    public void saveImage(String imageUrl) {

        try {
            SaveImageTask saveImageTask = new SaveImageTask();
            taskCollection.add(saveImageTask);
            saveImageTask.execute(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * read cache
     *
     * @param key key
     * @param cls cls
     * @param <T> object
     * @return object
     */
    public <T> T getCache(String key, Class<T> cls) {


        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getKey(key));

            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return readStream(inputStream, cls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * get image cache by image url
     *
     * @param imageUrl imageUrl
     * @return Bitmap
     */
    public Bitmap getImageCache(String imageUrl) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getKey(imageUrl));
            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return BitmapFactory.decodeStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * read list cache
     *
     * @param key key
     * @param cls cls
     * @param <T> t
     * @return object
     */
    public <T> List<T> getCacheList(String key, Class<T> cls) {


        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getKey(key));

            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return readListStream(inputStream, cls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    /**
     * close cache
     */
    public void closeCache() {

        try {
            if (taskCollection != null) {
                for (SaveImageTask task : taskCollection) {
                    task.cancel(false);
                }
                taskCollection.clear();
            }

            for (DiskLruCache disLru : cacheDiskLru.values()) {
                disLru.close();
            }

            cacheDiskLru.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * delete cache by key
     *
     * @param key key
     * @return is
     */
    public boolean remove(String key) {
        if (mDiskLruCache == null) {
            return false;
        }

        try {
            return mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }



    /**
     * save image task
     */
    class SaveImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String imageUrl = params[0];

            try {

                DiskLruCache.Editor edit = mDiskLruCache.edit(getKey(imageUrl));

                OutputStream fos = edit.newOutputStream(0);

                if (StreamUtil.writeUrlToStream(imageUrl, fos)) {
                    edit.commit();
                } else {
                    edit.abort();
                }
                mDiskLruCache.flush();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isFinish) {
            super.onPostExecute(isFinish);
            taskCollection.remove(this);
        }
    }

    /**
     * get key
     *
     * @param key :key key
     * @return String
     */
    private String getKey(String key) {

        requireNonNull(key, "key can't be null");

        String fileName = "cache_" + key;
        return MD5.encodeKey(fileName);
    }
}
