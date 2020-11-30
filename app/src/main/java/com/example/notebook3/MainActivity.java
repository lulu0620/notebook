package com.example.notebook3;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Integer> IDList = new ArrayList<>();
    private List<String> TADList = new ArrayList<>();
    ArrayAdapter simpleAdapter;
    Button ButtonSeek;
    EditText EditTextSeek;
    EditText EditTextDefaultName;
    String EditTextSeekString ;
    Button ButtonSaveAuthorName;
/*    private void InitNote() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //通过dbhelper获得可写文件
        Cursor cursor  = db.rawQuery("select * from Note",null);
        IDList.clear();
        TADList.clear();        //清空两个list
        while(cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            IDList.add(id);
            TADList.add(title+"\n"+ date);      //对两个list填充数据
        }
    }*/

    public void RefreshTADList(){       //返回该界面时刷新的方法
        int size = TADList.size();
        //if(size>0){
        TADList.removeAll(TADList);
        IDList.removeAll(IDList);
        simpleAdapter.notifyDataSetChanged();       //清空两个list中的值
        //}
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,9);
        SQLiteDatabase db = dbHelper.getWritableDatabase();         //实例化SQLitedatabase
        Cursor cursor  = db.rawQuery("select * from Note",null);
        while(cursor.moveToNext()){         //对两个list重新赋予值
            int id=cursor.getInt(cursor.getColumnIndex("id"));

            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            IDList.add(id);
            TADList.add(title+"\n"+ date);      //将title和时间分开显示
        }
        /*SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("author", " ");
        editor.apply();*/
        SharedPreferences sharedPreferences=getSharedPreferences("data_1",MODE_PRIVATE);
        String name=sharedPreferences.getString("author","lulu");
        EditTextDefaultName.setText(name);

    }



    @Override
    protected void onStart() {
        super.onStart();
        RefreshTADList();       //调用刷新方法
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,9);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //通过dbhelper获得可写文件
        Cursor cursor  = db.rawQuery("select * from Note",null);
        IDList.clear();
        TADList.clear();        //清空两个list
        while(cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            IDList.add(id);
            TADList.add(title+"\n"+ date);      //对两个list填充数据
        }

        simpleAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,TADList);       //配置适配器
        ListView ListView = (ListView)findViewById(R.id.ListView);
        ListView.setAdapter(simpleAdapter);                 //将两个list中的值通过ArrayList显示出来

        Button ButtonAdd;
        ButtonAdd = (Button)findViewById(R.id.ButtonAdd);
        ButtonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, com.example.notebook3.Add.class);
                startActivity(intent);
            }
        });

        ButtonSeek = findViewById(R.id.ButtonSeek);
        EditTextSeek = findViewById(R.id.EditTextSeek);
        ButtonSeek.setOnClickListener(new View.OnClickListener(){       //点击跳转查询界面
            @Override
            public void onClick(View v){
                EditTextSeekString="";
                EditTextSeekString = String.valueOf(EditTextSeek.getText());
                //Log.d("title is ",EditTextSeekString);
                if(EditTextSeekString.length()==0){             //查询为空，给出提示信息
                    RefreshTADList();
                    Toast.makeText(MainActivity.this,"查询值不能为空",Toast.LENGTH_LONG).show();
                }
                else{           //否则通过intent给查询界面传入查询的title
                    Intent intent = new Intent(MainActivity.this, com.example.notebook3.Research.class);
                    //intent.putExtra("tranTitle",EditTextSeekString);
                    intent.putExtra("tranTitletoRE",EditTextSeekString);
                    startActivity(intent);

                }
            }
        });

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                int tran = IDList.get(position);        //点击不同的行，返回不同的id
                Intent intent = new Intent(MainActivity.this, com.example.notebook3.Edit.class);
                intent.putExtra("tran",tran);
                startActivity(intent);      //通过intent传输
            }
        });

        EditTextDefaultName=findViewById(R.id.default_author_name);
        ButtonSaveAuthorName=findViewById(R.id.ButtonSaveDefaultAuthor);
        ButtonSaveAuthorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=EditTextDefaultName.getText().toString();
                SharedPreferences.Editor editor=getSharedPreferences("data_1",MODE_PRIVATE).edit();
                if("".equals(EditTextDefaultName.getText().toString())){
                    editor.putString("author","lulu");
                    Toast.makeText(MainActivity.this, "默认值为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    editor.putString("author", name);
                    Toast.makeText(MainActivity.this, "默认值不为空", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
            }
        });
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
           /*case R.id.ButtonSeek:
                *//*Intent intent = new Intent(MainActivity.this,com.example.notebook2.Research.class);
                startActivity(intent);*//*
                Toast.makeText(MainActivity.this, "按钮被点击", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ButtonAdd:
                *//*Intent intent1 = new Intent(MainActivity.this, com.example.notebook2.Add.class);
                startActivity(intent1);*//*
                Toast.makeText(MainActivity.this, "按钮被点击", Toast.LENGTH_SHORT).show();
                break;*/
        }

    }
    
}