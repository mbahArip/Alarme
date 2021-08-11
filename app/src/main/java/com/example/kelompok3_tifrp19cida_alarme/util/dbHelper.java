package com.example.kelompok3_tifrp19cida_alarme.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {

    private static final String _db_name = "Test";
    private static final String _db_table = "reminderList";

    //Column
    private static final String _col_id = "id";
    private static final String _col_title = "title";
    private static final String _col_desc = "description";
    private static final String _col_time = "timestamp";
    private static final String _col_status = "status";

    public dbHelper(Context context){
        super(context, _db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String query = "CREATE TABLE " + _db_table + "("
                + _col_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + _col_title + " TEXT,"
                + _col_desc + " TEXT,"
                + _col_time + " TEXT,"
                + _col_status + " INTEGER"
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + _db_table;
        db.execSQL(query);

        onCreate(db);
    }

    public void insertData(String title, String description, String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(_col_title, title);
        cv.put(_col_desc, description);
        cv.put(_col_time, timestamp);
        cv.put(_col_status, 1);

        db.insert(_db_table, null, cv);
    }

    public Cursor viewData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + _db_table + " WHERE " + _col_status + " = 1";
        Cursor c = db.rawQuery(query, null);

        return c;
    }

    public void deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(_db_table, _col_id + " = ?",
                new String[]{String.valueOf(id)});
    }
}
