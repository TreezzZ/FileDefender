package com.github.treezzz.filedefender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.github.treezzz.filedefender.model.EncryptedFile;

import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.FILE_NAME;
import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.PATH;
import static com.github.treezzz.filedefender.database.EncryptedFileDbSchema.EncryptedFileTable.Cols.THUMB_NAME;

/**
 * Created by tree on 6/13/17.
 * 负责数据库查询
 */

public class EncryptedFileCursorWrapper extends CursorWrapper
{
    public EncryptedFileCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    /**
     * 从数据库中获取加密文件信息
     * @return
     */
    public EncryptedFile getEncryptedFile()
    {
        String name = getString(getColumnIndex(FILE_NAME));
        String thumb = getString(getColumnIndex(THUMB_NAME));
        String path = getString(getColumnIndex(PATH));

        return new EncryptedFile(name, thumb, path);
    }
}
