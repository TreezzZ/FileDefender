package com.github.treezzz.filedefender.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.treezzz.filedefender.R;

/**
 * Created by tree on 6/13/17.
 * 登录界面
 */

public class LoginActivity extends AppCompatActivity
{
    // 密码输入控件
    private EditText etPassword;

    // 登录按钮控件
    private Button btnLogin;

    // 用户预设密码，暂且直接写入文件
    private static final String DefaultPassword = "336735";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 获取界面控件
        getWidget();

        // 登录按钮绑定事件
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 获取用户输入密码
                String Password = etPassword.getText().toString();

                if (Password.equals(DefaultPassword))
                {
                    // 跳转到功能界面
                    Intent intent = new Intent(LoginActivity.this, FileDefenderActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("错误")
                            .setMessage("密码输入错误")
                            .setPositiveButton("确定", null)
                            .create().show();
                }
            }
        });
    }

    /**
     * 获取界面控件
     */
    private void getWidget()
    {
        etPassword = (EditText)findViewById(R.id.activity_login_password);
        btnLogin = (Button)findViewById(R.id.activity_login_login);
    }
}
