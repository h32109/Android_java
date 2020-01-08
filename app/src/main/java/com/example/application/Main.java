package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Context _this = Main.this;
        findViewById(R.id.next_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SQLiteHelper(_this);
                        startActivity(new Intent(_this, Login.class));
                    }
                });
    }
    static class Member{int seq; String name, passwd, email, phone, addr, photo;}
    static String DBNAME = "contacts.db";
    static String MEMBERS = "MEMBERS";
    static String SEQ = "SEQ";
    static String NAME = "NAME";
    static String PASSWD = "PASSWD";
    static String EMAIL = "EMAIL";
    static String PHONE = "PHONE";
    static String ADDR = "ADDR";
    static String PHOTO = "PHOTO";
    static abstract class QueryFactory{
        Context _this;
        public QueryFactory(Context _this){this._this = _this;}
        public abstract SQLiteDatabase getDatabase();
    }
    static class SQLiteHelper extends SQLiteOpenHelper{

        public SQLiteHelper(Context context){
            super(context, DBNAME, null, 1);
            this.getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) { 
            String sql = String.format(
                    " CREATE TABLE IF NOT EXISTS %s " +
                            " ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "   %s TEXT, " +
                            "   %s TEXT, " +
                            "   %s TEXT, " +
                            "   %s TEXT, " +
                            "   %s TEXT, " +
                            "   %s TEXT " +
                            ")",MEMBERS, SEQ,NAME, PASSWD, EMAIL,
                    PHONE, ADDR, PHOTO );
            Log.d("실행할 쿼리 : ", sql);
            db.execSQL(sql);
            String[] names = {"강동원","윤아","임수정","박보검","송중기"};
            String[] emails = {"kdw", "yoona","lsj","pbk","sjk"};
            for(int i = 0; i< names.length;i++){
                db.execSQL(String.format(" INSERT INTO %s " +
                                "( %s, " +
                                " %s ," +
                                " %s ," +
                                " %s ," +
                                " %s ," +
                                " %s )" +
                                " VALUES (" +
                                " '%s' ," +
                                " '%s' ," +
                                " '%s' ," +
                                " '%s' ," +
                                " '%s' ," +
                                " '%s' )" , MEMBERS, NAME, PASSWD, EMAIL,
                        PHONE, ADDR, PHOTO, names[i], "1", emails[i]+"@test.com",
                        "010-1234-567"+(i+1), "백범로 "+(i+1)+"길", "photo_"+(i+1)));
            }
            Log.d("INSERT 쿼리 ", " SUCCESS !! ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}