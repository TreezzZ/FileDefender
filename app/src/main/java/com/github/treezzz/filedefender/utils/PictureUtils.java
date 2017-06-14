package com.github.treezzz.filedefender.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * 对图片进行处理
 * Created by tree on 2/16/17.
 */

public class PictureUtils
{
    /**
     * 对图片进行缩放处理
     * @param path 图片路径
     * @param destWidth 宽度
     * @param destHeight 高度
     * @return 缩放后的图片
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        boolean needScale = srcHeight > destHeight || srcWidth > destWidth;
        if (needScale)
        {
            if(srcWidth > srcHeight)
            {
                inSampleSize = Math.round(srcHeight / destHeight);
            }
            else
            {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 对图片进行缩放处理
     * @param path 图片路径
     * @param activity 用于获取窗口尺寸
     * @return 缩放后的图片
     */
    public static Bitmap getscaledBitmap(String path, Activity activity)
    {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
