package com.example.notebook3;


import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Add extends AppCompatActivity implements OnClickListener{
    String Title,Content,simpleDate,Photo;
    Button ButtonAddCancel,ButtonAddSave,ButtonAddPhoto;
    EditText EditTextAddTitle,EditTextAddContent,EditTextAddAuthor;
    String Author;
    String AT;
    public final static int REQUEST_CODE_SELECT_PHOTO = 1;
    public static final int TAKE_PHOTO = 1;//拍照片
    private static final int CHOOSE_PHOTO = 2;//选择相册中的图片
    private ImageView picture;//放置图片的view
    private Uri imageUri;//图片在手机中存放位置的view


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        SharedPreferences sharedPreferences=getSharedPreferences("data_1",MODE_PRIVATE);

        ButtonAddCancel = (Button)findViewById(R.id.ButtonAddCancel);
        ButtonAddSave = (Button)findViewById(R.id.ButtonAddSave);
        Button takePhoto = (Button) findViewById(R.id.take_photo);//拍照按钮
        Button choosePhoto = (Button) findViewById(R.id.choose_from_album);//获取手机中的图片按钮
        picture = (ImageView) findViewById(R.id.picture);
        EditTextAddContent = findViewById(R.id.EditTextAddContent);
        EditTextAddTitle = findViewById(R.id.EditTextAddTitle);
        EditTextAddAuthor = findViewById(R.id.EditTextAddAuthor);
        ButtonAddCancel.setOnClickListener(this);
        ButtonAddSave.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        EditTextAddAuthor.setText(sharedPreferences.getString("author","lulu"));
    }
    @Override
    public void onClick(View v){
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,9);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()) {
            case R.id.ButtonAddSave:
                Date date = new Date();
                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        //配置时间格式
                simpleDate = simpleDateFormat.format(date);
                Title = String.valueOf(EditTextAddTitle.getText());         //获取需要储存的值
                Content = String.valueOf(EditTextAddContent.getText());
                AT = String.valueOf(EditTextAddAuthor.getText());
                ContentValues values = new ContentValues();
//                Photo = getImagePath(imageUri, null);
                Log.d("Title",Title);
                if(Title.length()==0){               //标题为空给出提示
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);
                    values.put("content", Content);
                    values.put("date", simpleDate);
                    values.put("photo", Photo);
                    values.put("author_d", AT);
                    db.insert("Note", null, values);                 //将值传入数据库中
                    Add.this.setResult(RESULT_OK, getIntent());
                    Add.this.finish();
                }


              //  Author = String.valueOf(EditTextAddAuthor.getText());
                //SharedPreferences.Editor editor = getSharedPreferences("data_1",MODE_PRIVATE).edit();
              //  editor.putString("author",Author);      //使用sharedperferences设置默认作者
               // editor.apply();
                break;

            case R.id.ButtonAddCancel:
                Add.this.setResult(RESULT_OK,getIntent());
                Add.this.finish();
                break;

            case R.id.take_photo:
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");//创建文件用于保存即将拍摄出的照片
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();//如果这个名字的图片存在，那么删除，避免重复照
                    }
                    outputImage.createNewFile();//创建这个file
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    //如果SDK版本大于24需要使用provider
                    imageUri = FileProvider.getUriForFile(Add.this,
                            "com.example.cameralbumm.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//使用Intent打开相机
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);//为了获取拍摄所得到的照片需要重写返回方法
//                Photo=imageUri.getPath();
                Photo=outputImage.getPath();
                break;

            case R.id.choose_from_album:
                //首先检查访问权限，如果没有权限，向用户进行询问
                if (ContextCompat.checkSelfPermission(Add.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Add.this, new
                            String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //权限允许，打开相册
                    openAlbum();
                }

                break;

            default:
                break;

        }


    }
    /**因为是用startActivityForResult启动的，
     所以当拍照这个活动结束销毁之后会调用这个方法*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO://声明全局变量，区分拍照还是从相册中直接选取
                if (resultCode == RESULT_OK) {//表示拍照成功
                    try {
                        //将图片内容转换为Bitmap格式，将拍照的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);//将图片显示到ImageView上
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){//已经选择图片成功
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片，需要对uri进行处理
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片，直接获取uri
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //请求用户访问权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }

    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");//使用Intent打开相册
        intent.setType("image/*");//图片
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册

    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //如果是document类型的URI，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1]; //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Photo=imagePath;
        displayImage(imagePath); //根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
