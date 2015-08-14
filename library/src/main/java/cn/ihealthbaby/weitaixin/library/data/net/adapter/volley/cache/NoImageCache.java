package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.cache;

import android.graphics.Bitmap;

/**
 * Created by diudiustudio on 2015/2/5.
 */
public class NoImageCache implements KImageCache {
    @Override
    public void clearCache() {

    }

    @Override
    public boolean remove(String url) {
        return false;
    }


    @Override
    public Bitmap getBitmap(String url) {
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {

    }
}
