package com.github.treezzz.filedefender.model;

/**
 * Created by tree on 6/13/17.
 * 加密文件信息类
 */

public class EncryptedFile
{
    // 文件名
    private String name;

    // 缩略图
    private String thumb;

    // 文件路径
    private String path;

    public EncryptedFile(String name, String thumb, String path)
    {
        this.name = name;
        this.thumb = thumb;
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public String getThumb()
    {
        return thumb;
    }

    public String getPath()
    {
        return path;
    }
}
