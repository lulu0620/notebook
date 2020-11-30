package com.example.notebook3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper{     //对数据库进行创建
    public static  final String CREATE_NOTE = "create table Note(" //创建一个名为“Note”的数据库
            + "id integer primary key autoincrement,"   //数据库的列的名字及约束
            + "title text not null,"
            + "content text,"
            + "date datetime not null default current_time,"
            + "photo text,"
            + "author_d text)";
    private Context mContext;
    public MyDataBaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }
    @Override
    public void  onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_NOTE);
    } //数据库的创建方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ //数据库的更新方法
        db.execSQL("drop table if exists Note");  //数据库进行的更新操作
        onCreate(db);
    }
}
