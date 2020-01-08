package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

public class MemberUpdate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_update);
        final Context _this = MemberUpdate.this;
        ImageView update_profile = findViewById(R.id.update_profile);
        final EditText update_name = findViewById(R.id.update_name);
        final EditText changePhone = findViewById(R.id.changePhone);
        final EditText changeEmail = findViewById(R.id.changeEmail);
        final EditText changeAddress = findViewById(R.id.changeAddress);
        Button confirmBtn = findViewById(R.id.confirmBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        Intent intent = this.getIntent();
        String spec = intent.getExtras().getString("spec");
        final String[] specList = spec.split(",");
        update_name.setText(specList[1]);
        changePhone.setText(specList[3]);
        changeEmail.setText(specList[2]);
        changeAddress.setText(specList[4]);
        update_profile.setImageDrawable(
                getResources().getDrawable(
                        getResources().getIdentifier(
                                _this.getPackageName()+":drawable/"+specList[5], null, null
                        )));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UpdateMember updateMember = new UpdateMember(_this);
                    updateMember.update_name = update_name.getText().toString();
                    updateMember.changePhone = changePhone.getText().toString();
                    updateMember.changeEmail = changeEmail.getText().toString();
                    updateMember.changeAddress = changeAddress.getText().toString();
                    updateMember.seq = specList[0];
                    updateMember.run();
                    Intent intent = new Intent(_this, MemberDetail.class);
                    intent.putExtra("seq", specList[0]);
                    startActivity(intent);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, MemberDetail.class);
                intent.putExtra("seq", specList[0]);
                startActivity(intent);
            }
        });
    }
    private class UpdateQuery extends Main.QueryFactory{
        SQLiteOpenHelper helper;

        public UpdateQuery(Context _this) {
            super(_this);
            helper = new Main.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getWritableDatabase();
        }
    }
    private class UpdateMember extends UpdateQuery{
        String update_name = "", changePhone = "", changeEmail = "", changeAddress = "", seq = "";

        public UpdateMember(Context _this) {
            super(_this);
        }

        public void run(){ // setter는 execSQL로 한다.
            String sql = String.format(
                    " UPDATE %s\n" +
                            "     SET %s = '%s',\n" +
                            "         %s = '%s',\n" +
                            "         %s = '%s',\n" +
                            "         %s = '%s'\n" +
                            "     WHERE %s LIKE '%s'",
                    Main.MEMBERS, Main.NAME, update_name,
                    Main.EMAIL, changeEmail,
                    Main.PHONE, changePhone,
                    Main.ADDR, changeAddress, Main.SEQ, seq);
            getDatabase().execSQL(sql);
        }
    }
}
