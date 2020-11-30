package com.example.notebook3;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class Research extends AppCompatActivity implements OnClickListener{
    Button ButtonDelete,ButtonSave,ButtonCancel;
    EditText EditTextContent,EditTextTitle,EditTextREAuthor;
    String tranTitle;
    String Author;
    ImageView EditPicture;
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,9);

    private void InitNote() {
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,9);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Note",new String[]{"id","title","author_d","content","photo"},"title=?",new String[]{tranTitle+""},null,null,null);

        if(cursor.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String author = cursor.getString(cursor.getColumnIndex("author_d"));
                String photopath = cursor.getString(cursor.getColumnIndex("photo"));
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
                EditTextREAuthor.setText(author);
                if (photopath != null)
                    displayImage(photopath);
            } while (cursor.moveToNext());
        }

//        SharedPreferences pref = getSharedPreferences("data_1",MODE_PRIVATE);     //向sharedpreferences文件读取作者信息
//        String name = pref.getString("author","lulu");
//        Log.d("MainActivity","name is " + name);
//        EditTextREAuthor.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);
        EditTextContent = (EditText)findViewById(R.id.EditTextEditContent);
        EditTextTitle = (EditText)findViewById(R.id.EditTextREEditTitle) ;
        ButtonCancel = (Button)findViewById(R.id.ButtonRECancel);
        ButtonSave = (Button)findViewById(R.id.ButtonRESave);
        ButtonDelete = (Button)findViewById(R.id.ButtonREDelete);
        EditTextREAuthor = findViewById(R.id.EditTextREAuthor);
        EditPicture = findViewById(R.id.EditPicture);

        ButtonCancel.setOnClickListener(this);
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        tranTitle = extras.getString("tranTitletoRE");      //接受主界面传来的title值

        InitNote();


    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.ButtonREDelete:       //删除该title的日志
                Log.d("title is ",tranTitle);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Note","title=?",new String[]{tranTitle+""});     //进行字符串匹配
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;
            case R.id.ButtonRESave:         //将文界面内容保存
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();        //获取可写文件
                Date date = new Date();
                ContentValues values = new ContentValues();         //获取信息
                String Title = String.valueOf(EditTextTitle.getText());
                String Content = String.valueOf(EditTextContent.getText());
                String author = String.valueOf(EditTextREAuthor.getText());
                if(Title.length()==0){
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);         //填装信息
                    values.put("content", Content);
                    values.put("author_d", author);
                    db1.update("Note", values, "title=?", new String[]{tranTitle + ""});        //字符串匹配
                    Research.this.setResult(RESULT_OK, getIntent());        //返回主界面
                    Research.this.finish();
                }

                Author = String.valueOf(EditTextREAuthor.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data_1",MODE_PRIVATE).edit();
                editor.putString("author",Author);
                editor.apply();     //写入作者信息

                break;


            case R.id.ButtonRECancel:
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;

        }

    }
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            EditPicture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}