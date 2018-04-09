package com.example.cpu10152_local.testrecyclerview;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by cpu10152-local on 04/04/2018.
 */

public class ImageCache extends LruCache<Integer, Bitmap> {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public ImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return value.getByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        oldValue.recycle();
    }
}
