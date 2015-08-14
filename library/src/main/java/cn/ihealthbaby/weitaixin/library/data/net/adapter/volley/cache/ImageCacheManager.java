package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.cache;

import android.graphics.Bitmap;

/**
 * Created by diudiustudio on 2015/2/5.
 */
public class ImageCacheManager implements KImageCache {

    private final KImageCache[] caches;

    public ImageCacheManager(KImageCache... caches) {
        this.caches = caches;
    }

    @Override
    public Bitmap getBitmap(String url) {
        for (int i = 0; i < caches.length; i++) {
            Bitmap bitmap = caches[i].getBitmap(url);
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
//        if (getBitmap(url) != null) {
//            return;
//        }
            for (KImageCache cache : caches) {
                cache.putBitmap(url, bitmap);
            }

    }

    @Override
    public void clearCache() {
        for (KImageCache cache : caches) {
            cache.clearCache();
        }
    }

    @Override
    public boolean remove(String url) {
        for (KImageCache cache : caches) {
            boolean remove = cache.remove(url);
            if (!remove) {
                return false;
            }
        }
        return true;
    }
}
