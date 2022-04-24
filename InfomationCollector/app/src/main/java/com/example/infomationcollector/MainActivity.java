package com.example.infomationcollector;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainActivity extends AppCompatActivity{

    // resources
    private MyReceiver myReceiver;
    private Button bt1, bt2,通话记录按钮,短信记录按钮,拍照按钮,应用信息按钮,社交软件按钮;
    public static final int TAKE_PHOTO = 1;//声明一个请求码，用于识别返回的结果
    private ImageView picture;
    private Uri imageUri;
    private  String filePath = "/sdcard/Hack_TakePicture/output_image.jpg";;




    // permissions to acquire
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS};
    // permission acquiring code
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int REQUEST_PHONE_STATE = 1001;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    //此处是跳转的result回调方法
                    try {
                        System.out.println("进入回转");
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        //将图片解析成Bitmap对象，并把它显现出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        通话记录按钮 = findViewById(R.id.通话按钮);
        短信记录按钮 = findViewById(R.id.短信按钮);
        拍照按钮=findViewById(R.id.拍照);
        picture = findViewById(R.id.图片显示);
        应用信息按钮=findViewById(R.id.获取应用);
        社交软件按钮=findViewById(R.id.社交软件);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GetPhoneInfo.class);
                startActivity(intent);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GetContactors.class);
                startActivity(intent);
            }
        });

        通话记录按钮.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Get_history.class);
                intent.putExtra("type", "通话记录");
                startActivity(intent);
            }
        });

        短信记录按钮.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Get_history.class);
                intent.putExtra("type", "短信记录");
                startActivity(intent);
            }
        });

        拍照按钮.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera();
            }
        });

        应用信息按钮.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Get_history.class);
                intent.putExtra("type", "获取包名");
                startActivity(intent);
            }
        });
        社交软件按钮.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Get_history.class);
                intent.putExtra("type", "社交软件");
                startActivity(intent);
            }
        });

        if (!checkPermissions()) {  //未获取权限，申请权限
            requestPermissions();
        } else {
            //已经获取权限
        }
        setDefualtImage();

    }

    private boolean checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                PERMISSIONS, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //判断请求码，确定当前申请的权限
        if (requestCode == REQUEST_PHONE_STATE) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ok
            } else {
                Toast.makeText(this, "请手动获取权限后继续操作", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestCamera() {
       // filePath="/sdcard/Hack_TakePicture/output_image.jpg";
        File outputImage = new File(filePath);
      //  File file = new File("/sdcard/UFH_document/盘点结果.csv");

                /*
                创建一个File文件对象，用于存放摄像头拍下的图片，我们把这个图片命名为output_image.jpg
                并把它存放在应用关联缓存目录下，调用getExternalCacheDir()可以得到这个目录，为什么要
                用关联缓存目录呢？由于android6.0开始，读写sd卡列为了危险权限，使用的时候必须要有权限，
                应用关联目录则可以跳过这一步
                 */
        System.out.println("AAAAAAAA");

            if (!outputImage.getParentFile().exists()) {
                outputImage.getParentFile().mkdirs();
                System.out.println("BBBBBBBB");
            }
            if (outputImage.exists()) {
                outputImage.delete();
                System.out.println("CCCCCC");
            }
            System.out.println("DDDDDDD");
          //  outputImage.createNewFile();
        if (!checkPermissions()) {  //未获取权限，申请权限
            requestPermissions();
        }
            try {
                if(!outputImage.createNewFile()) {
                    System.out.println("File already exists");
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
            System.out.println("EEEEEEEEE");
            if (Build.VERSION.SDK_INT >= 24) {
                System.out.println("FFFFFFFFFF");
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.infomationcollector.fileprovider", outputImage);
            } else {
                System.out.println("GGGGGGGGG");
                imageUri = Uri.fromFile(outputImage);
            }
            System.out.println("HHHHHHHHH");

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivity(intent);
       // intentActivityResultLauncher.launch(intent);
       // String out =imageUri;
      //  mGetContent.launch("image/*");


            System.out.println("IIIIIIIII");
            //调用会返回结果的开启方式，返回成功的话，则把它显示出来


    }



    //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解
  /*  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        //将图片解析成Bitmap对象，并把它显现出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }*/

    //设置保存拍照图片——>再次关闭app重新打开显示为上次拍照照片
    private void setDefualtImage() {
        File outputImage = new File(filePath);
        if (!outputImage.exists()) {
            return;
        }
        picture.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }
}





