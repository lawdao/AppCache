package cc.fussen.cache;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import cc.fussen.cache.model.LoaderFactory;
import cc.fussen.cache.model.ModelLoader;
import cc.fussen.cache.model.Type;

import static cc.fussen.cache.disklrucache.Util.requireNonNull;

/**
 * Created by Fussen on 2017/12/4.
 * <p>
 * 缓存工具类
 */

public class Cache {


    private static volatile Cache cache;
    private final LoaderFactory loaderFactory;
    private final Handler mainHandler;

    public Cache(Context context) {
        loaderFactory = new LoaderFactory(context);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Get the singleton.
     *
     * @return the singleton
     */
    public static Cache get(Context context) {
        if (cache == null) {
            synchronized (Cache.class) {
                if (cache == null) {
                    cache = new Cache(context.getApplicationContext());
                }
            }
        }
        return cache;
    }


    /**
     * @param context Any context, will not be retained and trans application
     * @return CacheManager
     */
    public static CacheManager with(Context context) {
        ManagerRetriever retriever = ManagerRetriever.get();
        return retriever.get(context);
    }


    /**
     * @param activity The activity to use.
     * @return CacheManager
     */
    public static CacheManager with(Activity activity) {
        ManagerRetriever retriever = ManagerRetriever.get();
        return retriever.get(activity);
    }


    /**
     * @param activity The FragmentActivity to use.
     * @return CacheManager
     */
    public static CacheManager with(FragmentActivity activity) {
        ManagerRetriever retriever = ManagerRetriever.get();
        return retriever.get(activity);
    }


    /**
     * @param fragment The fragment to use.
     * @return CacheManager
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static CacheManager with(android.app.Fragment fragment) {
        ManagerRetriever retriever = ManagerRetriever.get();
        return retriever.get(fragment);
    }


    /**
     * @param fragment The fragment to use.
     * @return CacheManager
     */
    public static CacheManager with(Fragment fragment) {
        ManagerRetriever retriever = ManagerRetriever.get();
        return retriever.get(fragment);
    }


    public static <D> ModelLoader<D> buildObjModelLoader(String path, Context context) {

        return buildModelLoader(path, context,Type.NORMAL);
    }

    public static <D> ModelLoader<D> buildImageModelLoader(String path, Context context) {

        return buildModelLoader(path, context,Type.IMAGE);
    }

    public static <D> ModelLoader<D> buildModelLoader(String path, Context context, Type type) {

        requireNonNull(path, "path can't be null");

        return Cache.get(context).getLoaderFactory().buildModelLoader(path, type);
    }


    public LoaderFactory getLoaderFactory() {
        return loaderFactory;
    }


    /**
     * close cache
     */
    public static void closeCache() {
        ManagerRetriever retriever = ManagerRetriever.get();

        CacheManager manager = retriever.getManager();
        if (manager == null) {
            return;
        }
        manager.closeCache();
    }

}
