package com.example.xwn.calendarmanager;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.cloud.SpeechUtility;

import java.util.List;

import cn.bmob.v3.Bmob;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Bmob.initialize(this,"4c962dbe9488fcdea84b8e7b46c96ae3");
        String[] perms = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR,};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this,"权限有了",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        } else {
            EasyPermissions.requestPermissions(this, "Android6.0以上的APP需要手动赋予权限，请赋予",0, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this,"权限申请成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this,"权限申请失败",Toast.LENGTH_SHORT).show();
    }
}
