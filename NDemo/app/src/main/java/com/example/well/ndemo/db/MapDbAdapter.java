package com.example.well.ndemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.well.ndemo.bean.PathRecord;
import com.example.well.ndemo.utils.MapUtils;
import com.example.well.ndemo.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ${LuoChen} on 2017/4/24 12:54.
 * email:luochen0519@foxmail.com
 * <p>
 * 保存足迹路线的数据库操作类
 */

public class MapDbAdapter {

    private static final java.lang.String RECORD_DB_NAME = "record";//数据库名称
    private static final java.lang.String RECORD_TABLE_NAME = "record";//表名称
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ SettingsUtils.RECORDPATH;//存放数据库的文件夹路径
    private static final String DATABASE_NAME = DATABASE_PATH + "/" + RECORD_DB_NAME + ".db";//存放数据库的路径

    private static final String KEY_ROWID = "id";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_SPEED = "averagespeed";
    private static final String KEY_LINE = "pathline";
    private static final String KEY_STRAT = "stratpoint";
    private static final String KEY_END = "endpoint";
    private static final String KEY_DATE = "date";

    /**
     * //创建数据库的sql语句
     */
    private static final java.lang.String RECORD_CREATE = "create table if not exists " + RECORD_TABLE_NAME + "("
            + KEY_ROWID + " integer primary key autoincrement,"
            + KEY_STRAT + " STRING,"
            + KEY_END + " STRING,"
            + KEY_LINE + " STRING,"
            + KEY_DISTANCE + " STRING,"
            + KEY_DURATION + " STRING,"
            + KEY_SPEED + " STRING,"
            + KEY_DATE + " STRING"
            + ");";
    private Context context;
    private final DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;

    private class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);//创建数据库
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RECORD_CREATE);//建表

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public MapDbAdapter(Context context) {
        this.context = context;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    /**
     * 打开数据库
     *
     * @return
     */
    public MapDbAdapter open() {
        mDb = mDatabaseHelper.getWritableDatabase();
        return this;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mDb.close();
    }

    /**
     * 添加一条轨迹
     *
     * @return
     */
    public long addRecord(String distance, String duration,
                          String averagespeed, String pathline, String stratpoint,
                          String endpoint, String date) {
        ContentValues values = new ContentValues();
        values.put(KEY_DISTANCE, distance);
        values.put(KEY_DURATION, duration);
        values.put(KEY_SPEED, averagespeed);
        values.put(KEY_LINE, pathline);
        values.put(KEY_STRAT, stratpoint);
        values.put(KEY_END, endpoint);
        values.put(KEY_DATE, date);
        long insert = mDb.insert(RECORD_TABLE_NAME, null, values);
        return insert;
    }

    public List<PathRecord> queryRecordAll() {

        List<PathRecord> records = new ArrayList<>();
        Cursor query = mDb.query(RECORD_TABLE_NAME, getColumns(), null, null, null, null, null);
        while (query.moveToNext()) {
            PathRecord record = new PathRecord();
            record.setId(query.getInt(query.getColumnIndex(MapDbAdapter.KEY_ROWID)));
            record.setDistance(query.getString(query.getColumnIndex(MapDbAdapter.KEY_DISTANCE)));
            record.setDuration(query.getString(query
                    .getColumnIndex(MapDbAdapter.KEY_DURATION)));
            record.setDate(query.getString(query
                    .getColumnIndex(MapDbAdapter.KEY_DATE)));
            String lines = query.getString(query
                    .getColumnIndex(MapDbAdapter.KEY_LINE));
            record.setPathline(MapUtils.parseLocations(lines));
            record.setStartPoint(MapUtils.parseLocation(query
                    .getString(query
                            .getColumnIndex(MapDbAdapter.KEY_STRAT))));
            record.setEndPoint(MapUtils.parseLocation(query
                    .getString(query
                            .getColumnIndex(MapDbAdapter.KEY_END))));
            record.setAveragespeed(query.getString(query.getColumnIndex(MapDbAdapter.KEY_SPEED)));
            records.add(record);
        }

        Collections.reverse(records);

        return records;
    }

    /**
     * 按照id查询
     *
     * @param mRecordItemId
     * @return
     */
    public PathRecord queryRecordById(int mRecordItemId) {
        String where = KEY_ROWID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mRecordItemId)};
        Cursor cursor = mDb.query(RECORD_TABLE_NAME, getColumns(), where,
                selectionArgs, null, null, null);
        PathRecord record = new PathRecord();
        if (cursor.moveToNext()) {
            record.setId(cursor.getInt(cursor
                    .getColumnIndex(MapDbAdapter.KEY_ROWID)));
            record.setDistance(cursor.getString(cursor
                    .getColumnIndex(MapDbAdapter.KEY_DISTANCE)));
            record.setDuration(cursor.getString(cursor
                    .getColumnIndex(MapDbAdapter.KEY_DURATION)));
            record.setDate(cursor.getString(cursor
                    .getColumnIndex(MapDbAdapter.KEY_DATE)));
            String lines = cursor.getString(cursor
                    .getColumnIndex(MapDbAdapter.KEY_LINE));
            record.setPathline(MapUtils.parseLocations(lines));
            record.setStartPoint(MapUtils.parseLocation(cursor.getString(cursor
                    .getColumnIndex(MapDbAdapter.KEY_STRAT))));
            record.setEndPoint(MapUtils.parseLocation(cursor.getString(cursor
                    .getColumnIndex(MapDbAdapter.KEY_END))));
        }
        return record;
    }

    private String[] getColumns() {
        return new String[]{KEY_ROWID, KEY_DISTANCE, KEY_DURATION, KEY_SPEED,
                KEY_LINE, KEY_STRAT, KEY_END, KEY_DATE};
    }
}


