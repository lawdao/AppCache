package cc.fussen.cache.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import cc.fussen.cache.disklrucache.DiskLruCache;
import cc.fussen.cache.disklrucache.StreamUtil;

/**
 * Created by Fussen on 2017/12/19.
 */

public class ImageLoader<D> extends BaseLoader<Bitmap> {

    private Set<SaveImageTask> taskCollection;

    public ImageLoader(String cachePath, Context context) {
        super(cachePath, context);
        if (taskCollection == null) {
            taskCollection = new HashSet<>();
        }
    }


    @Override
    public void saveImage(String imageUrl) {

        try {
            SaveImageTask saveImageTask = new SaveImageTask();
            taskCollection.add(saveImageTask);
            saveImageTask.execute(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Bitmap getImage(String imageUrl) {
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


    @Override
    public void clear() {
        super.clear();

        if (taskCollection != null) {
            for (SaveImageTask task : taskCollection) {
                task.cancel(false);
            }
            taskCollection.clear();
        }
    }


    /**
     * save image task
     */
    class SaveImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String imageUrl = params[0];

            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                return false;
            }

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
}
