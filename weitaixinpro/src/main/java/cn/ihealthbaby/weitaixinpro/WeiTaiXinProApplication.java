package cn.ihealthbaby.weitaixinpro;

import android.app.Application;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.AbstractHttpClientAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;

/**
 * @author by kang on 2015/9/9.
 */
public class WeiTaiXinProApplication extends Application {
	private AbstractHttpClientAdapter adapter;

	@Override
	public void onCreate() {
		super.onCreate();
//		initUniversalImageLoader();
		initApiManager();
	}

	public void initApiManager() {
		RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
		adapter = new VolleyAdapter(getApplicationContext(), Constants.SERVER_URL, requestQueue);
		ApiManager.init(adapter);
	}

	public AbstractHttpClientAdapter getAdapter() {
		return adapter;
	}

//	public void initUniversalImageLoader() {
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//				                                  .memoryCacheExtraOptions(480, 800)
//				                                  .discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
//				                                  .threadPoolSize(3)
//				                                  .threadPriority(Thread.NORM_PRIORITY - 2)
//				                                  .discCacheSize(5 * 1024 * 1024)
//				                                  .discCacheFileNameGenerator(new Md5FileNameGenerator())
//				                                  .tasksProcessingOrder(QueueProcessingType.FIFO)
////				                                  .discCacheFileCount(100)
//				                                  .writeDebugLogs()
//				                                  .build();
//		ImageLoader.getInstance().init(config);
//	}
//
//	public DisplayImageOptions setDisplayImageOptions() {
//		DisplayImageOptions options = null;
//		options = new DisplayImageOptions.Builder()
//				          .showImageOnLoading(R.drawable.button_monitor_helper)
//				          .showImageForEmptyUri(R.drawable.button_monitor_helper)
//				          .showImageOnFail(R.drawable.button_monitor_helper)
//				          .cacheInMemory(true)
//				          .cacheOnDisc(true)
//				          .considerExifParams(true)
//				          .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//				          .bitmapConfig(Bitmap.Config.RGB_565)
//				          .displayer(new SimpleBitmapDisplayer())
////				.displayer(new RoundedBitmapDisplayer(20))
////				.displayer(new FadeInBitmapDisplayer(100))
//				          .build();
//		return options;
//	}
}

