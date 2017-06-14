package com.github.treezzz.filedefender.database;

/**
 * Created by tree on 6/13/17.
 * 加密文件数据库模式
 */

public class EncryptedFileDbSchema
{
    public static final class EncryptedFileTable
    {
        // 数据库名
        public static final String NAME = "EncryptedFile";

        // 列名
        public static final class Cols
        {
            // 加密文件名称
            public static final String FILE_NAME = "name";

            // 缩略图名称
            public static final String THUMB_NAME = "thumb";

            // 加密文件路径
            public static final String PATH = "path";
        }
    }
}
