package com.github.treezzz.filedefender.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.FILE_NAME;
import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.THUMB_NAME;
import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.PATH;

/**
 * Created by tree on 6/13/17.
 * 负责数据库创建升级
 */

public class EncryptedFileDbHelper extends SQLiteOpenHelper
{
    // 版本号
    private static final int VERSION = 1;

    // 数据库文件名
    private static final String DATABASE_NAME = "EncryptedFileDb.db";

    public EncryptedFileDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        // 创建表
        sqLiteDatabase.execSQL("create table " + EncryptedFileDbSchema.EncryptedFileTable.NAME + "(" +
                                "_id integer primary key autoincrement, " +
                                FILE_NAME + ", " +
                                THUMB_NAME + ", " +
                                PATH +
        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}
