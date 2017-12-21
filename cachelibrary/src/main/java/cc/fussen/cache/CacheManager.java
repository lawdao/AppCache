package cc.fussen.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.URLUtil;

import java.util.List;

import cc.fussen.cache.model.LoaderFactory;
import cc.fussen.cache.model.ModelLoader;

import static cc.fussen.cache.disklrucache.Util.getCacheDir;
import static cc.fussen.cache.disklrucache.Util.requireNonNull;


/**
 * Created by Fussen on 2017/12/4.
 */

public class CacheManager {


    private String path;

    private final Context context;

    private final Cache cache;

    public CacheManager(Context context) {
        this.context = context;
        this.cache = Cache.get(context);

        if (TextUtils.isEmpty(path)) {
            path = getCacheDir(context);
        }
    }


    /**
     * set cache path
     *
     * @param cachePath cachePath
     * @return CacheManager
     */
    public CacheManager path(String cachePath) {
        this.path = cachePath;
        return this;
    }


    /**
     * save cache
     *
     * @param key    key
     * @param object object
     * @return is
     */
    public synchronized <D> boolean saveCache(String key, D object) {

        requireNonNull(object, "cache data can't be null");

        return fromObj().saveCache(key, object);
    }

    /**
     * save image cache
     *
     * @param imageUrl imageUrl
     */
    public synchronized void saveImage(String imageUrl) {

        requireNonNull(imageUrl, "imageUrl can't be null");

        fromImage().saveImage(imageUrl);
    }


    /**
     * read cache
     *
     * @param key key
     * @param cls cls
     * @param <D> object
     * @return object
     */
    public synchronized <D> D getCache(String key, Class<D> cls) {

        return fromObj().getObjCache(key, cls);

    }

    /**
     * read list cache
     *
     * @param key key
     * @param cls cls
     * @param <D> t
     * @return object
     */
    public synchronized <D> List<D> getCacheList(String key, Class<D> cls) {

        return fromObj().getCacheList(key, cls);
    }


    /**
     * get image cache by image url
     *
     * @param imageUrl imageUrl
     * @return Bitmap
     */
    public synchronized Bitmap getImageCache(String imageUrl) {

        requireNonNull(imageUrl, "imageUrl can't be null");

        return fromImage().getImage(imageUrl);
    }


    /**
     * delete cache by key
     *
     * @param key key
     * @return is
     */
    public synchronized boolean remove(String key) {

        if (URLUtil.isHttpUrl(key) || URLUtil.isHttpsUrl(key)) {
            return fromImage().remove(key);
        } else {
            return fromObj().remove(key);
        }
    }


    /**
     * close cache
     */
    public void closeCache() {

        LoaderFactory loaderFactory = cache.getLoaderFactory();

        if (loaderFactory == null) return;

        loaderFactory.clearLoader();

    }


    private ModelLoader<Object> fromObj() {
        ModelLoader<Object> objectModelLoader = Cache.buildObjModelLoader(path, context);

        if (objectModelLoader == null) {
            throw new IllegalArgumentException("Unknown type obj . can't be save or nonsupport this type cache!");
        }

        return objectModelLoader;
    }


    private ModelLoader<Bitmap> fromImage() {
        ModelLoader<Bitmap> imageModelLoader = Cache.buildImageModelLoader(path, context);

        if (imageModelLoader == null) {
            throw new IllegalArgumentException("Unknown type image . can't be save or nonsupport this type cache!");
        }
        return imageModelLoader;
    }

}
