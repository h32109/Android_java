package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.function.Consumer;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final Context _this = Login.this;
        findViewById(R.id.login_btn)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        EditText et1 = findViewById(R.id.userid);
                        EditText et2 = findViewById(R.id.passwd);
                        String userid = et1.getText().toString();
                        String passwd = et2.getText().toString();
                        final MemberExist query = new MemberExist(_this);
                        query.userid = userid;
                        query.passwd = passwd;
                        new Consumer<Main.Member>(){

                            @Override
                            public void accept(Main.Member member) {
                                if(query.test()){
                                    startActivity(new Intent(_this, MemberList.class));
                                }else{
                                    startActivity(new Intent(_this, Login.class));
                                }
                            }
                        }.accept(null);

                    }
                });
    }
    private class LoginQuery extends Main.QueryFactory{
        SQLiteOpenHelper helper;

        public LoginQuery(Context _this) {
            super(_this);
            helper = new Main.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class MemberExist extends LoginQuery{
        String userid, passwd;
        public MemberExist(Context _this) {
            super(_this);
        }
        public boolean test(){
            return super.getDatabase()
                    .rawQuery(String.format("" +
                                    " SELECT * FROM %s " +
                                    " WHERE %s LIKE '%s' AND %s LIKE '%s' ",
                            Main.MEMBERS, Main.SEQ, userid, Main.PASSWD, passwd), null)
                    .moveToNext();
        }
    }
}