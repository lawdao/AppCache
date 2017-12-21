package cc.fussen.cache;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Fussen on 2017/12/6.
 */

public class ManagerRetriever {
    private static final ManagerRetriever INSTANCE = new ManagerRetriever();
    private volatile CacheManager manager;


    public static ManagerRetriever get() {
        return INSTANCE;
    }


    private CacheManager getApplicationManager(Context context) {

        if (manager == null) {
            synchronized (this) {
                if (manager == null) {
                    manager = new CacheManager(context.getApplicationContext());
                }
            }
        }

        return manager;
    }

    public CacheManager getManager() {
        return manager;
    }


    public CacheManager get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a save or read on a null Context");
        } else if (!(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context);
            } else if (context instanceof Activity) {
                return get((Activity) context);
            } else if (context instanceof ContextWrapper) {
                return get(((ContextWrapper) context).getBaseContext());
            }
        }

        return getApplicationManager(context);
    }


    public CacheManager get(Activity activity) {
        return get(activity.getApplicationContext());
    }


    public CacheManager get(Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a read or save on a fragment before it is attached");
        }
        return get(fragment.getActivity().getApplicationContext());
    }


    public CacheManager get(FragmentActivity activity) {
        return get(activity.getApplicationContext());
    }


    public CacheManager get(android.app.Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a read or save on a fragment before it is attached");
        }
        return get(fragment.getActivity().getApplicationContext());
    }

}
