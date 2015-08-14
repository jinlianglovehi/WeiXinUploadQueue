package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.cache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by diudiustudio on 2015/2/5.
 */
public interface KImageCache extends ImageLoader.ImageCache {

    void clearCache();

    boolean remove(String url);

}