package com.giraffe.imapp.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditIfmActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PHOTO = 11;//定义拍照返回请求码常量
    private static final int REQUEST_CODE_CHOOSE = 12;//定义相册返回请求码常量
    private static final int REQUEST_CODE_PERMISSION_WRITE = 21;//定义访问相机存储权限请求码
    private static final int REQUEST_CODE_PERMISSION_READ = 22;//定义访问相册读取权限请求码

    private Toolbar toolbar;
    private RelativeLayout rl_avatar;
    private Uri imageUri;//拍照的图片保存uri:包括名称
    private CircleImageView civ_avatar;
    private List<Uri> mSelected;//相册选择的图像路径，其中mSelected.get(0)为单选图片的路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }


    private void initView() {
        setContentView(R.layout.activity_edit_ifm);
        setSupportActionBar(toolbar);

        toolbar = findViewById(R.id.AE_tb_toolbar);//导航栏
        rl_avatar = findViewById(R.id.AE_rl_avatar);//头像行
        civ_avatar = findViewById(R.id.AE_civ_avatar);//头像组件

        toolbar.inflateMenu(R.menu.edit_ifm_menu);//加载菜单按钮
    }



    private void initListener() {
        rl_avatar.setOnClickListener(this);//头像行点击

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {//点击保存修改信息
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.AE_it_alter:
                        Toast.makeText(EditIfmActivity.this,"alter",
                                Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
    }



    /* ******** */
    /* 点击事件 */
    /* ******** */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AE_rl_avatar:
                openDialog();//打开弹窗选择:照相/打开相册
                break;
        }
    }



    /* ************** */
    /* 拍照、相册弹窗 */
    /* ************** */
    private void openDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final String[] items = {"拍照","打开相册"};
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //检查存储权限，如果没有授权，发起授权请求并打开相机
                    if (ContextCompat.checkSelfPermission
                            (EditIfmActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(EditIfmActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_WRITE);
                    }else {
                        takePhote();
                    }
                }else if (which == 1){
                    //检查读取权限，如果没有授权，发起授权请求并打开相册
                    if (ContextCompat.checkSelfPermission
                         (EditIfmActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(EditIfmActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_READ);
                    }else {
                        openAlbum();//打开图片选择器
                    }
                }
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }



    /* ********************************************************** */
     /* 系统自带拍照，此处不用，因为图片选择器中已经集成了拍照功能 */
     /* ********************************************************** */
    private void takePhote(){
        File outputImage = new File(getExternalCacheDir(),"avatar.jpg");//文件存储在Cache中
        try{//保证不重复存储图片
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){//按版本获得路径的方式
            imageUri = FileProvider.getUriForFile(EditIfmActivity.this,
                    "com.giraffe.imapp.fileprovider",outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }

        Intent intent = new Intent();//打开相机
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,REQUEST_CODE_PHOTO);
    }




    /* ********************* */
    /* 打开Matisse图片选择器 */
    /* ********************* */
    private void openAlbum(){
        Matisse.from(EditIfmActivity.this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Zhihu)//主题，夜间模式R.style.Matisse_Dracula
                .countable(false)//是否显示选中数字
                .capture(false)//是否提供拍照功能
                .maxSelectable(1)//最大选择数
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//屏幕方向
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(new GlideEngine())//图片加载方式
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }



    /* ************************************************* */
    /* 重写活动结果，主要是获得图片选择器传回的图片uri */
    /* ************************************************* */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_CODE_PHOTO://自写的系统自带拍照，此处不用，因为使用的图片选择器集成有拍照功能
                if (resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver().openInputStream(imageUri));
                        civ_avatar.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_CHOOSE:
                    if(data!=null){
                        mSelected = Matisse.obtainResult(data);
                        if (mSelected.get(0) != null){
                            try{
                                Bitmap bitmap = BitmapFactory.decodeStream
                                        (getContentResolver().openInputStream(mSelected.get(0)));
                                civ_avatar.setImageBitmap(bitmap);
                                Log.d("tag",bitmap.getWidth()+"//"+bitmap.getHeight());
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        }


    /* ************ */
    /* 权限获取请求 */
    /* ************ */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSION_READ:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_PERMISSION_WRITE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",
                            Toast.LENGTH_LONG).show();
                }
        }
    }

}
