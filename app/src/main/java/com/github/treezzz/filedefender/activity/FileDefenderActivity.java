package com.github.treezzz.filedefender.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.treezzz.filedefender.R;
import com.github.treezzz.filedefender.database.EncryptedFileList;
import com.github.treezzz.filedefender.model.EncryptedFile;
import com.github.treezzz.filedefender.utils.FileProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by tree on 6/13/17.
 * 功能界面
 */

public class FileDefenderActivity extends AppCompatActivity
{
    // RecyclerView负责显示加密文件列表
    private RecyclerView rvRecyclerView;

    // RecyclerView适配器
    private FileListAdapter fileListAdapter;

    // 加密文件信息
    private List<EncryptedFile> encryptedFileList = new ArrayList<>();

    // 请求文件管理器
    private static final int LOAD_FILE_MANAGER = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_defender);

        // 获取权限
        getPermission();

        // 获取界面控件
        getWidget();

        // 填充数据
        updateUI();
    }

    /**
     * 获取界面控件
     */
    private void getWidget()
    {
        rvRecyclerView = (RecyclerView)findViewById(R.id.activity_file_defender_recycler_view);
        rvRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    /**
     * 调用文件管理器选择文件进行加密
     */
    private void encryptFile()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, LOAD_FILE_MANAGER);
    }

    /**
     * 获取系统权限
     */
    private void getPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {

                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
            }
        }
    }

    // 配置RecyclerView
    private class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView ivImage;
        private TextView tvName;

        public FileHolder(View view)
        {
            super(view);

            ivImage = (ImageView) view.findViewById(R.id.item_grid_view_thumb);
            tvName= (TextView)view.findViewById(R.id.item_grid_view_name);

            ivImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            //获取正在处理的加密文件
            int i = (int)v.getTag();

            // 解密
            FileProcess.decrypt(encryptedFileList.get(i), FileDefenderActivity.this);

            // 恢复文件名
            EncryptedFile file = encryptedFileList.get(i);
            String newPath = new File(file.getPath()).getParentFile().getAbsolutePath() + "/" +
                    file.getName();
            File oldFile = new File(file.getPath());
            File newFile = new File(newPath);
            oldFile.renameTo(newFile);

            // 刷新列表
            updateUI();
        }
    }

    private class FileListAdapter extends RecyclerView.Adapter<FileHolder>
    {
        @Override
        public FileHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(FileDefenderActivity.this);
            View view = layoutInflater.inflate(R.layout.item_recycler_view, parent, false);

            return new FileHolder(view);
        }

        @Override
        public int getItemCount()
        {
            return encryptedFileList.size();
        }

        @Override
        public void onBindViewHolder(FileHolder holder, int position)
        {
            EncryptedFile encryptedFile = encryptedFileList.get(position);

            //根据不同的文件类型，设置不同的缩略图
            String fileType = FileProcess.getFileType(encryptedFile);
            if (fileType.equals("video") || fileType.equals("picture"))
            {
                try
                {
                    File file = new File(FileDefenderActivity.this.getFilesDir() + "/" + encryptedFile.getName());
                    Picasso.with(FileDefenderActivity.this).load(file).placeholder(R.drawable.file_picture).into(holder.ivImage);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (fileType.equals("audio"))
            {
                Picasso.with(FileDefenderActivity.this).load(R.drawable.audio_picture).into(holder.ivImage);
            }
            else if (fileType.equals("other"))
            {
                Picasso.with(FileDefenderActivity.this).load(R.drawable.file_picture).into(holder.ivImage);
            }

            holder.tvName.setText(encryptedFile.getName());
            holder.ivImage.setTag(position);
        }
    }

    /**
     * 更新RecyclerView
     */
    private void updateUI()
    {
        encryptedFileList.clear();
        encryptedFileList.addAll(new EncryptedFileList(this).getEncryptedFileList());

        if (fileListAdapter == null)
        {
            fileListAdapter = new FileListAdapter();
            rvRecyclerView.setAdapter(fileListAdapter);
        }
        else
        {
            fileListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // 获取从文件管理器中选择的文件路径
        if (requestCode == LOAD_FILE_MANAGER && resultCode == Activity.RESULT_OK && data != null)
        {
            Uri uri = data.getData();
            String path;
            String[] filePath = {MediaStore.Images.Media.DATA};

            Cursor cursor = this.getContentResolver().query(uri, filePath, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                path = cursor.getString(columnIndex);
                cursor.close();
            }
            else path = uri.getPath();

            // 设置待加密文件信息
            String name = new File(path).getName();

            // hash一下文件名
            String fileType[] = name.split("\\.");
            String newPath = new File(path).getParentFile().getAbsoluteFile() + "/" + name.hashCode() +
                    "." + fileType[fileType.length-1];
            String thumbPath = this.getFilesDir() + "/" + name;
            new File(path).renameTo(new File(newPath));
            EncryptedFile encryptedFile = new EncryptedFile(name, thumbPath, newPath);

            // 加密文件
            FileProcess.encrypt(encryptedFile, this);
            updateUI();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // 加密文件
            case R.id.menu_add:
                encryptFile();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
