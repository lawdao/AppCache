package cc.fussen.cache;

import android.content.Context;

/**
 * Created by Fussen on 2017/12/6.
 */

public class Bulder {
    private static final Bulder INSTANCE = new Bulder();
    private CacheManager manager;


    public static Bulder get() {
        return INSTANCE;
    }


    public CacheManager get(Context context) {

        if (manager == null) {
            synchronized (this) {
                if (manager == null) {
                    manager = new CacheManager(context);
                }
            }
        }

        return manager;
    }


    public CacheManager getManager() {
        return manager;
    }
}
