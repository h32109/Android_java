package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MemberDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_detail);
        final Context _this = MemberDetail.this;
        Intent intent = this.getIntent();
        TextView detail_name = findViewById(R.id.detail_name);
        TextView detail_email = findViewById(R.id.detail_email);
        TextView detail_phone = findViewById(R.id.detail_phone);
        TextView detail_addr = findViewById(R.id.detail_addr);
        ImageView detail_profile = findViewById(R.id.detail_profile);
        Button updateBtn = findViewById(R.id.updateBtn);
        Button listBtn = findViewById(R.id.listBtn);
        String seq = intent.getExtras().getString("seq"); // 화면을 넘기는데 추가로 같이 넘기기
        Toast.makeText(_this, "넘어온 값"+seq, Toast.LENGTH_LONG).show();
        Memberspec memberspec = new Memberspec(_this);
        memberspec.seq = seq;
        final Main.Member member = memberspec.get();
        detail_name.setText(member.name);
        detail_email.setText(member.email);
        detail_phone.setText(member.phone);
        detail_addr.setText(member.addr);
        detail_profile.setImageDrawable(
                getResources().getDrawable(
                        getResources().getIdentifier(
                                _this.getPackageName()+":drawable/"+member.photo, null, null
                        )));
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, MemberUpdate.class);
                intent.putExtra("spec",String.format("%s,%s,%s,%s,%s,%s",member.seq,member.name,member.email,member.phone,member.addr,member.photo));
                startActivity(intent);
            }
        });
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_this, MemberList.class));
            }
        });

    }
    private class DetailQuery extends Main.QueryFactory{
        SQLiteOpenHelper helper;

        public DetailQuery(Context _this) {
            super(_this);
            helper = new Main.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }

    private class Memberspec extends DetailQuery{
        String seq;

        public Memberspec(Context _this) {
            super(_this);
        }
        public Main.Member get(){
            Main.Member member = new Main.Member();
            Cursor cursor = this.getDatabase()
                    .rawQuery(String.format("" +
                            "SELECT %s,%s,%s,%s,%s,%s FROM %s " +
                            "WHERE %s LIKE '%s' ", Main.SEQ,Main.NAME,Main.ADDR,Main.PHONE,Main.EMAIL,Main.PHOTO,Main.MEMBERS, Main.SEQ, seq), null);
            cursor.moveToFirst();
            member.seq = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Main.SEQ)));
            member.name = cursor.getString(cursor.getColumnIndex(Main.NAME));
            member.addr = cursor.getString(cursor.getColumnIndex(Main.ADDR));
            member.phone = cursor.getString(cursor.getColumnIndex(Main.PHONE));
            member.email = cursor.getString(cursor.getColumnIndex(Main.EMAIL));
            member.photo = cursor.getString(cursor.getColumnIndex(Main.PHOTO));
            return member;
        }
    }

}