package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.cache.memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;



/**
 * @author Hongjian.Liu
 */

//TODO
public class LruMemoryImageCache implements MemoryImageCache {
    protected static final String TAG = "LruMemoryImageCache";
    private int DEFAULT_CACHE_CAPACITY;
    private final LruCache<String, Bitmap> mLruCache;

    /**
     * @param context  上下文
     * @param capacity 容量，单位b
     */
    public LruMemoryImageCache(Context context, int capacity) {
        mLruCache = new LruCache<String, Bitmap>(capacity) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            /**
             * 更新: 此处的问题多数是因为缓存满了之后,将oldvalue推出缓存,此时图片仍在使用<br/>
             * 因为缓存的逻辑只能判断出改图不再被缓存,但是不能保证该图是否被使用,这是两个不相关的行为<br/>
             * 所以不可以调用recycle<br/>
             * recycle的操作是清除底层二进制数组<br/>
             * 避免在此处使用以下代码：<br/>
             *      if (!oldValue.isRecycled()) {<br/>
             *          oldValue.recycle();<br/>
             *      }<br/>
             * <br/>
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }


    /**
     * 从内存缓存中获取Bitmap，如果获取失败，从二级缓存中获取
     */
    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = mLruCache.get(url);
        if (bitmap != null) {
//            LogUtil.log(TAG, "从内存缓存中取出" + url + bitmap.toString());
        }
        return bitmap;
    }

    /**
     * 将Bitmap存入缓存
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mLruCache.put(generateHashKey(url), bitmap);
//        LogUtil.log(TAG, "保存到内存缓存" + url + "bitmap:" + bitmap.toString());
    }

    @Override
    public void clearCache() {
        mLruCache.evictAll();
    }

    @Override
    public boolean remove(String url) {
        mLruCache.remove(generateHashKey(url));
        return true;
    }

    @Override
    public String generateHashKey(String url) {
//TODO MD5
//        return MD5.getMessageDigest(url.getBytes()  );
        return null;
    }
}

