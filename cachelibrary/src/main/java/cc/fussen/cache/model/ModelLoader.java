package cc.fussen.cache.model;

import java.util.List;

/**
 * Created by Fussen on 2017/12/19.
 */

public interface ModelLoader<D> {

    boolean saveCache(String key, D data);

    void saveImage(String imageUrl);

    <D> D getObjCache(String key, Class<D> cls);

    <D> List<D> getCacheList(String key, Class<D> cls);

    <D> D getImage(String imageUrl);

    void clear();

    boolean remove(String key);
}
