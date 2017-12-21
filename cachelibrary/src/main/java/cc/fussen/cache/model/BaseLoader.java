package cc.fussen.cache.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cc.fussen.cache.MD5;
import cc.fussen.cache.disklrucache.DiskLruCache;

import static cc.fussen.cache.disklrucache.Util.getAppVersion;
import static cc.fussen.cache.disklrucache.Util.requireNonNull;

/**
 * Created by Fussen on 2017/12/20.
 */

public abstract class BaseLoader<D> implements ModelLoader<D> {

    private static final String TAG = "modelloader";

    private final int maxSize = 20;//unit :m

    protected String cachePath;
    protected DiskLruCache mDiskLruCache;

    public BaseLoader(String cachePath, Context context) {
        this.cachePath = cachePath;
        createDiskLruCache(context);
    }

    private void createDiskLruCache(Context context) {

        File cacheDir = new File(cachePath);

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, maxSize * 1024 * 1024);
            System.out.println("......create DiskLruCache......");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveCache(String key, D data) {
        return false;
    }

    @Override
    public void saveImage(String imageUrl) {
    }

    @Override
    public <D> D getImage(String imageUrl) {
        return null;
    }

    @Override
    public <D> D getObjCache(String key, Class<D> cls) {
        return null;
    }

    @Override
    public <D> List<D> getCacheList(String key, Class<D> cls) {
        return null;
    }


    @Override
    public boolean remove(String key) {

        if (mDiskLruCache != null) {
            try {
                return mDiskLruCache.remove(getKey(key));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "===========remove failed ===========");
            }
        }
        return false;
    }

    @Override
    public void clear() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "===========clear failed ===========");
            }
        }
    }

    /**
     * get key
     *
     * @param key :key key
     * @return String
     */
    protected String getKey(String key) {

        requireNonNull(key, "key can't be null");

        String fileName = "cache_" + key;
        return MD5.encodeKey(fileName);
    }

}
