package cc.fussen.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.fussen.cache.Cache;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button save;
    private Button read;
    private Button delete;
    private TextView name;
    private TextView age;
    private ListView listView;


    private List<String> mData = new ArrayList<>();
    private List<String> cacheData = new ArrayList<>();
    private User user;
    private ArrayAdapter<String> adapter;
    private ImageView imageView;
    private String imageUrl;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save = (Button) findViewById(R.id.btn_save);
        read = (Button) findViewById(R.id.btn_read);
        delete = (Button) findViewById(R.id.delete);

        name = (TextView) findViewById(R.id.tv_name);
        age = (TextView) findViewById(R.id.tv_age);

        listView = (ListView) findViewById(R.id.listview);

        imageView = (ImageView) findViewById(R.id.image);

        save.setOnClickListener(this);
        read.setOnClickListener(this);
        delete.setOnClickListener(this);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cacheData);

        listView.setAdapter(adapter);

        initUser();
    }

    private void initUser() {

        for (int i = 0; i < 20; i++) {
            mData.add("user: " + i);
        }

        user = new User();
        user.name = "fussen";
        user.age = "100";


        imageUrl = "http://img.my.csdn.net/uploads/201407/26/1406383059_2237.jpg";

        imagePath = getCacheDir(this) + File.separator + "image";
    }

    @Override
    public void onClick(View view) {

        if (view == save) {


            Cache.with(this)
                    .path(getCacheDir(this))
                    .saveCache("user1", user);

            Cache.with(this)
                    .path(getCacheDir(this))
                    .saveCache("user2", mData);

            Cache.with(MainActivity.this)
                    .path(imagePath)
                    .saveImage(imageUrl);

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

        } else if (view == read) {

            getCacheData();
        } else if (view == delete) {
            deleteCache();
        }

    }


    private void getCacheData() {


        //////////////////////////////
        User user = Cache.with(this)
                .path(getCacheDir(this))
                .getCache("user1", User.class);

        if (user == null) {
            Toast.makeText(this, "暂无缓存", Toast.LENGTH_SHORT).show();
            return;
        }

        name.setText(user.name);
        age.setText(user.age);


        ////////////////////////////////

        List<String> cacheList = Cache.with(this)
                .path(getCacheDir(this))
                .getCacheList("user2", String.class);

        cacheData.clear();
        cacheData.addAll(cacheList);

        adapter.notifyDataSetChanged();

        //////////////////////////////


        Bitmap cacheBitmap = Cache.with(this)
                .path(imagePath)
                .getImageCache(imageUrl);

        imageView.setImageBitmap(cacheBitmap);


    }


    private void deleteCache() {

        Cache.with(this)
                .path(getCacheDir(this))
                .remove("user1");


        Cache.with(this)
                .path(getCacheDir(this))
                .remove("user2");

        Cache.with(this)
                .path(imagePath)
                .remove(imageUrl);
    }


    public String getCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

}
