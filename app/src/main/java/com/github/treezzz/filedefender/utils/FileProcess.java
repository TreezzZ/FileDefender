package com.github.treezzz.filedefender.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;

import com.github.treezzz.filedefender.database.EncryptedFileList;
import com.github.treezzz.filedefender.model.EncryptedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;

/**
 * Created by tree on 6/13/17.
 * 负责文件加密解密
 */

public class FileProcess
{
    // 加密前100个字节
    private static final int LEN = 100;

    /**
     * 加密文件
     * @param encryptedFile 加密文件信息
     * @param context       上下文
     */
    public static void encrypt(EncryptedFile encryptedFile, Context context)
    {
        // 保存缩略图
        saveThumbnail(encryptedFile, context);

        // 加密文件
        process(encryptedFile);

        // 添加到数据库
        new EncryptedFileList(context).addEcryptedFile(encryptedFile);

        // 删除图库缓存
        scanFileAsync(context, new File(encryptedFile.getPath()).getParentFile().getAbsolutePath() + "/" + encryptedFile.getName());
    }

    public static void decrypt(EncryptedFile encryptedFile, Context context)
    {
        // 删除缩略图
        context.deleteFile(new File(encryptedFile.getThumb()).getName());

        // 解密文件
        process(encryptedFile);

        // 从数据库删除
        new EncryptedFileList(context).removeEncryptedFile(encryptedFile);
    }

    /**
     * 处理文件
     * @param encryptedFile 加密文件信息
     */
    private static void process(EncryptedFile encryptedFile)
    {
        File file = new File(encryptedFile.getPath());
        int len = LEN;
        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() < LEN)
                len = (int) randomAccessFile.length();
            FileChannel fileChannel = randomAccessFile.getChannel();
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, len);

            for (int i = 0; i < len; ++i)
                buffer.put(i, (byte) (buffer.get(i) ^ i));
            buffer.force();
            buffer.clear();
            fileChannel.close();
            randomAccessFile.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 保存文件缩略图
     * @param encryptedFile 加密文件信息
     * @param context       上下文
     */
    private static void saveThumbnail(EncryptedFile encryptedFile, Context context)
    {
        String fileType = getFileType(encryptedFile);
        if (fileType.equals("picture"))
        {
            Bitmap Thumbnail = PictureUtils.getscaledBitmap(encryptedFile.getPath(), (Activity) context);
            saveThumb(encryptedFile, context, Thumbnail);
        } else if (fileType.equals("video"))
        {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(encryptedFile.getPath());
            String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int videoTime = Integer.valueOf(time) / 1000;

            //取视频中间为缩略图
            videoTime /= 2;
            Bitmap thumbnail = mediaMetadataRetriever.getFrameAtTime(videoTime * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            saveThumb(encryptedFile, context, thumbnail);

            mediaMetadataRetriever.release();
        } else if (fileType.equals("audio"))
        {

        } else if (fileType.equals("other"))
        {

        }
    }

    /**
     * 缩略图压缩保存
     * @param encryptedFile 文件信息
     * @param context 上下文
     * @param thumbnail 缩略图
     */
    private static void saveThumb(EncryptedFile encryptedFile, Context context, Bitmap thumbnail)
    {
        try
        {
            //缩略图压缩保存
            String fileName = new File(encryptedFile.getThumb()).getName();
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 判断文件类型
     * @param encryptedFile 文件信息
     * @return 文件类型
     */
    public static String getFileType(EncryptedFile encryptedFile)
    {
        //视频类型
        HashSet<String> videoFileType = new HashSet<>();
        videoFileType.add("avi");
        videoFileType.add("mov");
        videoFileType.add("mpeg");
        videoFileType.add("mpg");
        videoFileType.add("wmv");
        videoFileType.add("flv");
        videoFileType.add("mp4");
        videoFileType.add("mkv");
        videoFileType.add("rmvb");

        //音频类型
        HashSet<String> audioFileType = new HashSet<>();
        audioFileType.add("mp3");
        audioFileType.add("wma");
        audioFileType.add("wav");
        audioFileType.add("mid");
        audioFileType.add("m4a");

        //图片类型
        HashSet<String> pictureFileType = new HashSet<>();
        pictureFileType.add("bmp");
        pictureFileType.add("gif");
        pictureFileType.add("jpeg");
        pictureFileType.add("jpg");
        pictureFileType.add("tiff");
        pictureFileType.add("png");

        String[] postfix = encryptedFile.getName().split("\\.");
        String fileType = postfix[postfix.length - 1];

        if (videoFileType.contains(fileType))
        {
            return "video";
        } else if (audioFileType.contains(fileType))
        {
            return "audio";
        } else if (pictureFileType.contains(fileType))
        {
            return "picture";
        } else
        {
            return "other";
        }
    }

    /**
     * 删除图库缓存
     * @param context 上下文
     * @param path 文件路径
     */
    private static void scanFileAsync(Context context, String path)
    {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(path)));
        context.sendBroadcast(scanIntent);

        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/cache/latest");
        String[] fileLists = file.list();
        if (fileLists != null)
        {
            for (String f : fileLists)
            {
                new File(file.getAbsolutePath(), f).delete();
            }
        }

    }

}
