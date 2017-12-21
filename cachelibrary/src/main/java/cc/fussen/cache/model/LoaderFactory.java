package cc.fussen.cache.model;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fussen on 2017/12/20.
 */

public class LoaderFactory {

    private final Context context;
    private final Map<String, ModelLoader/*D*/> objCacheLoaders = new HashMap<>();
    private final Map<String, ModelLoader/*D*/> imageCacheLoaders = new HashMap<>();

    public LoaderFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized <D> ModelLoader<D> buildModelLoader(String path, Type type) {

        return createNormalLoader(path, type);
    }


    private synchronized <D> ModelLoader<D> createNormalLoader(String path, Type type) {

        ModelLoader<D> cacheLoader = getCacheLoader(path, type);

        if (cacheLoader != null) {
            return cacheLoader;
        }

        ModelLoader<D> modelLoader = null;
        switch (type) {
            case NORMAL:
                modelLoader = new ObjectLoader(path, context);
                objCacheLoaders.put(path, modelLoader);
                break;
            case IMAGE:
                modelLoader = new ImageLoader(path, context);
                imageCacheLoaders.put(path, modelLoader);
                break;
        }


        return modelLoader;
    }


    private <D> ModelLoader<D> getCacheLoader(String path, Type type) {

        ModelLoader/*D*/ modelLoader = null;
        switch (type) {
            case NORMAL:
                modelLoader = objCacheLoaders.get(path);
                break;
            case IMAGE:
                modelLoader = imageCacheLoaders.get(path);
                break;
        }
        return modelLoader;
    }


    public void clearLoader() {

        for (ModelLoader/*D*/ loader : objCacheLoaders.values()) {
            loader.clear();
        }
        objCacheLoaders.clear();
        for (ModelLoader/*D*/ loader : imageCacheLoaders.values()) {
            loader.clear();
        }
        imageCacheLoaders.clear();
    }
}
