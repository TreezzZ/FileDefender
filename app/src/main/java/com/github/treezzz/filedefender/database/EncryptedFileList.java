package com.github.treezzz.filedefender.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.github.treezzz.filedefender.model.EncryptedFile;

import java.util.ArrayList;
import java.util.List;

import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.FILE_NAME;
import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.PATH;
import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.THUMB_NAME;

/**
 * Created by tree on 6/13/17.
 * 负责加密文件信息的插入删除
 */

public class EncryptedFileList
{
    // 数据库
    private SQLiteDatabase database;

    public EncryptedFileList(Context context)
    {
        database = new EncryptedFileDbHelper(context).getWritableDatabase();
    }

    /**
     * 添加加密文件信息到数据库
     * @param encryptedFile 加密文件信息
     */
    public void addEcryptedFile(EncryptedFile encryptedFile)
    {
        ContentValues contentValues = getContentValues(encryptedFile);
        database.insert(EncryptedFileDbSchema.EncryptedFileTable.NAME, null, contentValues);
    }

    /**
     * 删除加密文件信息
     * @param encryptedFile 要删除的加密文件信息
     */
    public void removeEncryptedFile(EncryptedFile encryptedFile)
    {
        database.delete(EncryptedFileDbSchema.EncryptedFileTable.NAME,
                        PATH + " = ?",
                        new String[] {encryptedFile.getPath()});
    }

    /**
     * 获取加密文件信息列表
     * @return 加密文件信息列表
     */
    public List<EncryptedFile> getEncryptedFileList()
    {
        List<EncryptedFile> encryptedFileList = new ArrayList<>();
        EncryptedFileCursorWrapper cursor = queryEncryptedFileList(null, null);

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                encryptedFileList.add(cursor.getEncryptedFile());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return encryptedFileList;
    }


    /**
     * 查询所有加密文件信息，返回一个游标
     * @param whereClause 查询子句
     * @param whereArgs 查询参数
     * @return 加密文件信息查询游标
     */
    private EncryptedFileCursorWrapper queryEncryptedFileList(String whereClause, String[] whereArgs)
    {
        Cursor cursor = database.query(
                EncryptedFileDbSchema.EncryptedFileTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new EncryptedFileCursorWrapper(cursor);
    }

    /**
     * 获取ContentValues
     * @param encryptedFile 加密文件信息
     * @return ContentValues
     */
    @NonNull
    private ContentValues getContentValues(EncryptedFile encryptedFile)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FILE_NAME, encryptedFile.getName());
        contentValues.put(THUMB_NAME, encryptedFile.getThumb());
        contentValues.put(PATH, encryptedFile.getPath());

        return contentValues;
    }


}
