package com.example.iceman.project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iceman on 18/10/2016.
 */

public class SQLiteDatabase extends SQLiteOpenHelper {

    public static SQLiteDatabase mInstance;

    public static SQLiteDatabase getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new SQLiteDatabase(mContext);
        }
        return mInstance;
    }

    public static final String DB_NAME = "MoneyManagement.db";
    public static final int DB_VERSION = 1;

    public static final String TBL_CURRENT_BALANCE = "CurrentBalance";
    public static final String TBL_CB_COLUMN_ID = "IdCB";
    public static final String TBL_CB_COLUMN_NAME = "Name";
    public static final String TBL_CB_COLUMN_MONEY = "Money";

    public static final String TBL_TRANSACTION = "TBL_Transaction";
    public static final String TBL_TRANS_COLUMN_ID = "idTrans";
    public static final String TBL_TRANS_COLUMN_DATE = "DateAdd";
    public static final String TBL_TRANS_COLUMN_CONTENT = "Content";
    public static final String TBL_TRANS_COLUMN_MONEY = "Money";
    public static final String TBL_TRANS_COLUMN_ID_TBL_CB = "Id_tbl_cb";
    public static final String TBL_TRANS_COLUMN_TRANS_TYPE = "TransactionType";

    public static final String CREATE_TABLE_CB = "create table " + TBL_CURRENT_BALANCE + "("
            + TBL_CB_COLUMN_ID + " integer primary key autoincrement,"
            + TBL_CB_COLUMN_NAME + " text not null,"
            + TBL_CB_COLUMN_MONEY + " real not null);";

    public static final String CREATE_TABLE_TRANS = "create table " + TBL_TRANSACTION + "("
            + TBL_TRANS_COLUMN_ID + " integer primary key autoincrement,"
            + TBL_TRANS_COLUMN_DATE + " text not null,"
            + TBL_TRANS_COLUMN_CONTENT + " text not null,"
            + TBL_TRANS_COLUMN_MONEY + " real not null,"
            + TBL_TRANS_COLUMN_ID_TBL_CB + " integer not null,"
            + TBL_TRANS_COLUMN_TRANS_TYPE + " integer not null, "
            + "foreign key" +
            "(" + TBL_TRANS_COLUMN_ID_TBL_CB + ") references "+TBL_CURRENT_BALANCE+"(" + TBL_CB_COLUMN_ID + ") " +
            "on delete cascade on update cascade);";

    public SQLiteDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onOpen(android.database.sqlite.SQLiteDatabase db) {
        super.onOpen(db);
        if(!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CB);
        db.execSQL(CREATE_TABLE_TRANS);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertRecord(String tableName, ContentValues values) {
        return getWritableDatabase().insert(tableName, null, values);
    }

    public Cursor rawQuery(String sql) {
        return getReadableDatabase().rawQuery(sql, null);
    }

    public long updateRecord(String tableName, ContentValues values, String columnID, String[] id) {
        return getWritableDatabase().update(tableName, values, columnID + " =? ", id);
    }

    public long deleteRecord(String tableName, String columnID, String[] id) {
        return getWritableDatabase().delete(tableName, columnID + " =? ", id);
    }
}
