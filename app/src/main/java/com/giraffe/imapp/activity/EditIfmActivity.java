package com.giraffe.imapp.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.pojo.User;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditIfmActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PHOTO = 11;//定义拍照返回请求码常量
    private static final int REQUEST_CODE_CHOOSE = 12;//定义相册返回请求码常量
    private static final int REQUEST_CODE_PERMISSION_WRITE = 21;//定义访问相机存储权限请求码
    private static final int REQUEST_CODE_PERMISSION_READ = 22;//定义访问相册读取权限请求码

    private Toolbar toolbar;
    private RelativeLayout rl_avatar;
    private String avatarPath;//上传图片的filepath
    private String s_avatarPath;//缩略图的filepath
    private Uri imageUri;//拍照的图片保存uri:包括名称
    private CircleImageView civ_avatar;
    private TextView tv_username,tv_sex;
    private EditText et_nickname,et_space,et_sign,et_mood;

    boolean isAvatar,isNickName,isSex,isSpace,isSign,isMood;

    private int int_sex;

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
        tv_username = findViewById(R.id.AE_tv_username);//用户名（账号）
        tv_sex = findViewById(R.id.AE_tv_sex);//性别
        et_nickname = findViewById(R.id.AE_et_nickname);//昵称
        et_space = findViewById(R.id.AE_et_space);//地址
        et_sign = findViewById(R.id.AE_et_sign);//个性签名
        et_mood = findViewById(R.id.AE_et_mood);//心情

        if (BmobUser.getCurrentUser(User.class).getAvatar() != null){
            Glide.with(this).load(BmobUser.getCurrentUser(User.class).getAvatar().getUrl()).
                error(R.mipmap.ic_launcher).thumbnail(0.1f).placeholder(R.mipmap.ic_launcher).
                    dontAnimate().into(civ_avatar);
        }
        tv_username.setText(BmobUser.getCurrentUser(User.class).getUsername());
        et_nickname.setText(BmobUser.getCurrentUser(User.class).getNickname());
        tv_sex.setText(BmobUser.getCurrentUser(User.class).getSex());
        et_space.setText(BmobUser.getCurrentUser(User.class).getSpace());
        et_sign.setText(BmobUser.getCurrentUser(User.class).getSign());
        et_mood.setText(BmobUser.getCurrentUser(User.class).getMood());

        toolbar.inflateMenu(R.menu.edit_ifm_menu);//加载菜单按钮
    }



    private void initListener() {
        rl_avatar.setOnClickListener(this);//头像行点击
        tv_sex.setOnClickListener(this);
        isEditTextChangeListener();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {//点击保存修改信息
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.AE_it_alter:
                        final Intent intent = new Intent(EditIfmActivity.this,MainActivity.class);
                        final User user = BmobUser.getCurrentUser(User.class);

                        if (isNickName) user.setNickname(et_nickname.getText().toString());
                        if (isSex) user.setSex(int_sex);
                        if (isSpace) user.setSpace(et_space.getText().toString());
                        if (isSign) user.setSign(et_sign.getText().toString());
                        if (isMood) user.setMood(et_mood.getText().toString());

                        if(isAvatar){
                            //上传图片
//                            final BmobFile bmobFile = new BmobFile(new File(avatarPath));//创建上传的文件
                            getFile(getBytes(avatarPath));
                            final BmobFile bmobFile = new BmobFile(new File(s_avatarPath));//创建上传的文件
                            bmobFile.uploadblock(new UploadFileListener() {//上传图片

                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                        Log.d("上传文件成功" ,bmobFile.getFileUrl());

                                        user.setAvatar(bmobFile);
                                        user.update(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e==null){
                                                    Log.d("图更新","更新图片以及修改信息到表格");
                                                    Toast.makeText(EditIfmActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                                    goBackMain();

                                                }else {
                                                    Log.d("图更新","错误："+e.getMessage());
                                                    Toast.makeText(EditIfmActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                                    goBackMain();

                                                }
                                            }
                                        });
                                        isAvatar = false;
                                    }else{
                                        Log.d("上传文件失败：" , e.getMessage());
                                        Toast.makeText(EditIfmActivity.this,"上传图片失败",Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onProgress(Integer value) {
                                    // 返回的上传进度（百分比）
                                }
                            });
                        }else if(isNickName||isSex||isSpace||isSign||isMood) {
                                user.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            Log.d("任意ET","更新修改的editText成功");
                                            Toast.makeText(EditIfmActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                            goBackMain();


                                        }else {
                                            Log.d("任意ET","更新修改的editText失败："+e.getMessage());
                                            Toast.makeText(EditIfmActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                            goBackMain();


                                        }
                                    }
                                });
                                isNickName = false;
                                isSex = false;
                                isSpace = false;
                                isSign = false;
                                isMood = false;
                        }else {
                            Toast.makeText(EditIfmActivity.this,"无更改",Toast.LENGTH_SHORT).show();
                            goBackMain();

                        }
                        BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {//更新用户缓存信息
                            @Override
                            public void done(BmobUser user, BmobException e) {
                                if (e == null) {
                                    final User myUser = BmobUser.getCurrentUser(User.class);
                                    Log.d("BmobUser", "更新用户本地缓存信息成功："+myUser.getUsername()+"-"+myUser.getNickname());
                                } else {
                                    Log.e("error",e.getMessage());
                                    Log.d("BmobUser", "更新用户本地缓存信息失败：" + e.getMessage());
                                }
                            }
                        });
                        break;
                }
                return true;
            }
        });
    }



    /* ******************** */
    /* 检查EditText是否修改 */
    /* ******************** */
    private void isEditTextChangeListener() {
        et_nickname.addTextChangedListener(new TextWatcher() {//昵称是否修改
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                isNickName = true;
            }
        });
        et_space.addTextChangedListener(new TextWatcher() {//地址是否修改
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                isSpace = true;
            }
        });
        et_sign.addTextChangedListener(new TextWatcher() {//个性签名是否修改
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                isSign = true;
            }
        });
        et_mood.addTextChangedListener(new TextWatcher() {//心情表是否修改
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                isMood = true;
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
                openAvatarDialog();//打开弹窗选择:照相/打开相册
                break;
            case R.id.AE_tv_sex:
                openSexDialog();

        }
    }



    /* ************** */
    /* 拍照、相册弹窗 */
    /* ************** */
    private void openAvatarDialog() {
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



    /* ************ */
    /* 选择性别弹窗 */
    /* ************ */
    private void openSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        final String[] it_sex = {"未知","男","女"};
        builder.setItems(it_sex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_sex.setText(it_sex[which]);
                isSex = true;//修改了性别
                int_sex = which;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


     /* ********************************************************** */
     /* 系统自带拍照，因为图片选择器中集成的拍照功能不能自定义路径 */
     /* ********************************************************** */
    private void takePhote(){
        File outputImage = new File(getExternalCacheDir(),"avatar.jpg");//文件存储在Cache中
        try{//保证不重复存储图片
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
            avatarPath = outputImage.getPath();//获得该路径下的图片
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
            case REQUEST_CODE_PHOTO://自写的系统自带拍照，因为使用的图片选择器集成的拍照功能不能自定义路径
                if (resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver().openInputStream(imageUri));
                        civ_avatar.setImageBitmap(bitmap);
                        isAvatar = true;//修改了头像

                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_CHOOSE:
                    if(data!=null){
                        //相册选择的图像路径，其中mSelected.get(0)为单选图片的路径
                        List<String> strings = Matisse.obtainPathResult(data);
                        //相册选择的图像Uri，其中mSelected.get(0)为单选图片的Uri
                        List<Uri> mSelected = Matisse.obtainResult(data);

                        if (mSelected.get(0) != null){
                            try{
                                Bitmap bitmap = BitmapFactory.decodeStream
                                        (getContentResolver().openInputStream(mSelected.get(0)));
                                civ_avatar.setImageBitmap(bitmap);
                                isAvatar = true;//修改了头像
                                avatarPath = strings.get(0);

                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        }


    /* ********** */
    /* 返回主界面 */
    /* ********** */
    private void goBackMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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



    private byte[] getBytes(String path) {
        //File file = new File(path);
        //读取图片 只读边,不读内容
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, newOpts);
        //开始按比例缩放图片
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        float maxSize = 1200;
        int be = 1;
        if (width >= height && width > maxSize) {//缩放比,用高或者宽其中较大的一个数据进行计算
            be = (int) (newOpts.outWidth / maxSize);
            be++;
        } else if (width < height && height > maxSize) {
            be = (int) (newOpts.outHeight / maxSize);
            be++;
        }
        newOpts.inSampleSize = be;//设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        //下面可是图片压缩
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        while (baos.toByteArray().length > 100 * 1024) {//循环判断如果压缩后图片是否大于指定大小,大于继续压缩
            baos.reset();//重置baos即让下一次的写入覆盖之前的内容
            options -= 5;//图片质量每次减少5
            if (options <= 5) options = 5;//如果图片质量小于5，为保证压缩后的图片质量，图片最底压缩质量为5
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
            if (options == 5) break;//如果图片的质量已降到最低则，不再进行压缩
        }
        return baos.toByteArray();
    }


    /**
     * 根据byte数组，生成文件
     */
    public void getFile(byte[] bfile) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        File file = null;
        try {
            file = new File(getExternalCacheDir(),"s_avatar.JPEG");
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            s_avatarPath = file.getPath();
            Log.d("s_avatar:",s_avatarPath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
