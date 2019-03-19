package com.giraffe.imapp.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.giraffe.imapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditIfmActivity extends AppCompatActivity implements View.OnClickListener {

    CollapsingToolbarLayout collapsingToolbarLayout;
    RelativeLayout rl_avatar;
    private Uri imageUri;
    CircleImageView civ_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ifm);

        rl_avatar = findViewById(R.id.AE_rl_avatar);
        civ_avatar = findViewById(R.id.AE_civ_avatar);

        rl_avatar.setOnClickListener(this);
    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.AE_rl_avatar:
               takePhote();
               break;
        }
    }


    /**
     * 拍照
     */
    private void takePhote(){
        File outputImage = new File(getExternalCacheDir(),"avatar.jpg");
        try{
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(EditIfmActivity.this,
                    "com.giraffe.imapp.fileprovider",outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }


        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        civ_avatar.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 刷新当前页面
     **/
    public static void restartActivity(Activity activity){
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
        activity.finish();
    }

}
