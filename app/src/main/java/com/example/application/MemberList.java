package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.function.Supplier;

public class MemberList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list);
        final Context _this = MemberList.this;
        final ItemList query = new ItemList(_this);
        final ListView memberList = findViewById(R.id.memberList);
        memberList.setAdapter(
                new MemberAdapter(_this, new Supplier<ArrayList<Main.Member>>() {
                    @Override
                    public ArrayList<Main.Member> get() {
                        return query.get();
                    }
                }.get()));
        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int i, long l) {
                Intent intent = new Intent(_this, MemberDetail.class);
                intent.putExtra("seq", (memberList.getItemIdAtPosition(i)+1)+"");
                startActivity(intent);
            }
        });
        memberList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p, View v, int i, long l) {
                final String seq = (memberList.getItemIdAtPosition(i)+1)+"";
                new AlertDialog.Builder(_this)
                        .setTitle("삭제")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MemberDelete memberDelete = new MemberDelete(_this);
                                memberDelete.seq = seq;
                                memberDelete.run();
                                startActivity(new Intent(_this, MemberList.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                startActivity(new Intent(_this, MemberList.class));
                            }
                        })
                        .show();
                return false;
            }
        });

    }
    private class MemberListQuery extends Main.QueryFactory{
        SQLiteOpenHelper helper;

        public MemberListQuery(Context _this) {
            super(_this);
            helper = new Main.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class MemberDeleteQuery extends Main.QueryFactory{
        SQLiteOpenHelper helper;

        public MemberDeleteQuery(Context _this) {
            super(_this);
            helper = new Main.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getWritableDatabase();
        }
    }
    private class MemberDelete extends MemberDeleteQuery{
        String seq="";

        public MemberDelete(Context _this) {
            super(_this);
        }

        public void run(){
            this.getDatabase().execSQL(String.format("" +
                    "DELETE FROM %s " +
                    "WHERE %s LIKE '%s'",Main.MEMBERS, Main.SEQ, seq));
        }
    }

    private class ItemList extends MemberListQuery{

        public ItemList(Context _this) {
            super(_this);
        }
        public ArrayList<Main.Member> get(){
            ArrayList<Main.Member> list = new ArrayList<>();
            Cursor cursor = this.getDatabase().rawQuery(
                    " SELECT * FROM MEMBERS ", null);
            Main.Member member = null;
            if(cursor != null){
                while(cursor.moveToNext()){
                    member = new Main.Member();
                    member.seq = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Main.SEQ)));
                    member.name = cursor.getString(cursor.getColumnIndex(Main.NAME));
                    member.passwd = cursor.getString(cursor.getColumnIndex(Main.PASSWD));
                    member.email = cursor.getString(cursor.getColumnIndex(Main.EMAIL));
                    member.phone = cursor.getString(cursor.getColumnIndex(Main.PHONE));
                    member.addr = cursor.getString(cursor.getColumnIndex(Main.ADDR));
                    member.photo = cursor.getString(cursor.getColumnIndex(Main.PHOTO));
                    Log.d("Member : ", member.name);
                    list.add(member);
                }
            }else{
                Toast.makeText(_this, "등록된 친구가 없음", Toast.LENGTH_LONG).show();
            }
            return list;
        }

    }
    private class PhotoQuery extends Main.QueryFactory{

        SQLiteOpenHelper helper;

        public PhotoQuery(Context _this) {
            super(_this);
            helper = new Main.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class ItemPhoto extends PhotoQuery{
        String seq;
        public ItemPhoto(Context _this) {
            super(_this);
        }
        public String get(){
            String result = "";
            Cursor cursor = getDatabase()
                    .rawQuery(String.format(" SELECT %s FROM %s " +
                                    " WHERE %s LIKE '%s'", Main.PHOTO, Main.MEMBERS,
                            Main.SEQ, seq), null);
            if(cursor != null){
                if(cursor.moveToNext()){
                    result = cursor.getString(cursor.getColumnIndex(Main.PHOTO));
                }
            }
            Log.d("프로필명 : ", result);
            return result;
        }
    }

    private class MemberAdapter extends BaseAdapter{

        ArrayList<Main.Member> list;
        LayoutInflater inflater;
        Context _this;

        public MemberAdapter(Context _this, ArrayList<Main.Member> list) {
            this.list = list;
            this._this = _this;
            this.inflater = LayoutInflater.from(_this);

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View v, ViewGroup g) {
            ViewHolder holder;
            if(v == null){
                v = inflater.inflate(R.layout.member_item, null);
                holder = new ViewHolder();
                holder.photo = v.findViewById(R.id.profile);
                holder.name = v.findViewById(R.id.name);
                holder.phone = v.findViewById(R.id.phone);
                v.setTag(holder);
            }else{
                holder = (ViewHolder)v.getTag();
            }
            final ItemPhoto query = new ItemPhoto(_this);
            query.seq = list.get(i).seq+"";
            holder.photo
                    .setImageDrawable(
                            getResources()
                                    .getDrawable(
                                            getResources()
                                                    .getIdentifier(
                                                            _this.getPackageName()+":drawable/"
                                                                    +new Supplier<String>() {
                                                                @Override
                                                                public String get() {
                                                                    return query.get();
                                                                }
                                                            }.get(), null, null
                                                    )
                                    )
                    );
            holder.name.setText(list.get(i).name);
            holder.phone.setText(list.get(i).phone);
            return v;
        }
    }
    static class ViewHolder{
        ImageView photo;
        TextView name, phone;
    }
}










