# AppCache
Android app DiskLruCache缓存封装

# 介绍
Android 应用缓存很常见，大多数应用都在使用DiskLruCache缓存技术，也有很多人介绍过DiskLruCache，一次我在这里就不介绍了。

DiskLruCache用起来不难，但是如果不加以封装的话，你会遇到各种open呀各种方法的组合，总之，不加以封装，还是比较麻烦的，于是就有了这篇博客，一行代码即可搞定缓存。

# 效果演示

![这里写图片描述](https://ws4.sinaimg.cn/large/006tKfTcgy1fm9eyreh0cg30960g8q3u.gif)


以上就是保存缓存数据以及读取缓存数据的效果

# 支持

## 能缓存些什么数据？

 1. 任何java对象，包括List集合。
 2. 图片
 
 该缓存工具主要缓存java对象，当然你如果要缓存json数据也可以，你可以把他当做String对象缓存到本地，读取的时候读取String数据就好，图片主要是将流缓存到本地，然后读取的时候读取本地保存的流就好。

## 使用DiskLruCache缓存技术

使用DiskLruCache缓存技术的好处在于，你不用关心缓存的过期时间，以及缓存大小的问题，也不用关心版本变化后数据格式改变的问题，他会自动判断软件版本，也会自动删除过期的旧数据，保证取到的数据没有问题，也不用关心SD卡的异常问题

# 如何使用


实际上，该工具的使用远远要比你想象的简单

## 1.保存缓存


1. 保存java对象

		String cachePath = getCacheDir(this);

		User user = new User();
		 user.name = "fussen";
		 user.age = "100";

		Cache.with(this)
		     .setPath(cachePath)
		     .saveCache("key", user);


2. 保存List集合数据

		List<String> mData = new ArrayList<>();

		String cachePath = getCacheDir(this);

		Cache.with(this)
		     .setPath(cachePath))
		     .saveCache("key", mData);

3. 保存图片

		String imageUrl = "http://img.my.csdn.net/uploads/201407/26/1406383059_2237.jpg";
		tring cachePath = getCacheDir(this);
		Cache.with(this)
		     .setPath(cachePath)
		     .saveImage(imageUrl);


## 2.读取缓存

1. 读取java对象缓存

		String cachePath = getCacheDir(this);
		User user = Cache.with(this)
		                 .setPath(cachePath)
		                 .getCache("key", User.class);

2. 读取List集合数据

		String cachePath = getCacheDir(this);
		List<String> cacheList = Cache.with(this)
					      .setPath(cachePath)
					      .getCacheList("key", String.class);

3. 读取图片缓存

		String cachePath = getCacheDir(this);
		
		Bitmap cacheBitmap = Cache.with(this)
				          .setPath(cachePath)
				          .getImageCache(imageUrl);
    		imageView.setImageBitmap(cacheBitmap);
		


经过以上步骤，你的缓存将会保存到本地，如图：

![](https://ws2.sinaimg.cn/large/006tKfTcgy1fm9lc63adhj30l00jetbj.jpg)

journal为DiskLruCache缓存经典标识文件。


## 3.说明：

1. 该工具可以设置缓存路径，也可以不用设置，默认的缓存路径是：
/sdcard/Android/data/（应用包名）/cache
2. 参数key为缓存文件的唯一标识，图片缓存以图片的url为唯一标识
3. 缓存文件名为md5编码后的名称


# 依赖


	 dependencies  {
		 compile 'cc.fussen:cachelibrary:1.0.0'
	   }


# 最后说明

1. 该工具封装相对简单，后面会继续优化，该工具的封装思想来自于Glide
2. 如果遇到什么问题，可以直接联系我
3. 扫描下面二维码即可关注AppCode

![AppCode](http://upload-images.jianshu.io/upload_images/3267943-35cf55f437d712a9.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
