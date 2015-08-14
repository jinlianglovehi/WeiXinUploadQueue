package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.cache.memory;


import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.cache.KImageCache;

/**
 * Created by diudiustudio on 2015/2/5.
 */
public interface MemoryImageCache extends KImageCache {

    String generateHashKey(String url);
}
