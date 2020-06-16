package com.snowdango.numac.DBControl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.snowdango.numac.ListFormat.AppListFormat;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    //database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "AppList.db";
    private static final String TABLE_NAME = "app_list";
    private static final String TABLE_NAME2 = "theme_base";

    // column name for table1
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_TITLE1 = "app_name";
    private static final String COLUMN_NAME_TITLE2 = "package_name";
    private static final String COLUMN_NAME_TITLE3 = "class_name";
    private static final String COLUMN_NAME_TITLE4 = "command";

    //column name for table2
    private static final String COLUMN_NAME_TITLE5 = "theme_color";

    // entries of create sql table1
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE1 + " TEXT," +
                    COLUMN_NAME_TITLE2 + " TEXT," +
                    COLUMN_NAME_TITLE3 + " TEXT," +
                    COLUMN_NAME_TITLE4 + " TEXT)";

    //entries of create sql table2
    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + TABLE_NAME2 + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE5 + " TEXT) ";

    //entries of delete sql
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;

    //init
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create db
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES2);

        Log.d("debug", "onCreate(SQLiteDatabase db)");
    }

    // upgrade db 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);

        db.execSQL(
                SQL_DELETE_ENTRIES2
        );
        onCreate(db);
    }

    public ArrayList<AppListFormat> getList(DataBaseHelper dataBaseHelper){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        ArrayList<AppListFormat> appList = new ArrayList<>();


        Cursor cursor = db.query(
                TABLE_NAME,
                new String[] { COLUMN_NAME_TITLE1, COLUMN_NAME_TITLE2,
                COLUMN_NAME_TITLE3 , COLUMN_NAME_TITLE4},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();


        for (int i = 0; i < cursor.getCount(); i++) {
            AppListFormat appListFormat = new AppListFormat();
            appListFormat.setAppName(cursor.getString(0));
            appListFormat.setAppPackageName(cursor.getString(1));
            appListFormat.setAppClassName(cursor.getString(2));
            appListFormat.setAppCommand(cursor.getString(3));
            appList.add(appListFormat);
            cursor.moveToNext();
        }

        cursor.close();
        return appList;
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onUpgrade(db, oldVersion, newVersion);

    }

    public String[] getPackageAndClass(DataBaseHelper dataBaseHelper, String command){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select "+COLUMN_NAME_TITLE2+" , "+COLUMN_NAME_TITLE3+" from "+TABLE_NAME+" where "+COLUMN_NAME_TITLE4+" = ?",new String[]{command});
        cursor.moveToFirst();

        return new String[]{ cursor.getString(0),cursor.getString(1) };
    }

    public ArrayList<String> getAppNameList(DataBaseHelper dataBaseHelper){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select "+COLUMN_NAME_TITLE1+" from "+TABLE_NAME,new String[]{});
        cursor.moveToFirst();

        ArrayList<String> appDataList = new ArrayList<>();
        for(int i = 0; i < cursor.getCount(); i++){
            appDataList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return appDataList;
    }

    public ArrayList<String> getAppCommandList(DataBaseHelper dataBaseHelper){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select "+COLUMN_NAME_TITLE4+" from "+TABLE_NAME,new String[]{});
        cursor.moveToFirst();

        ArrayList<String> appDataList = new ArrayList<>();
        for(int i = 0; i < cursor.getCount(); i++){
            appDataList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return appDataList;
    }

    public void insertData(DataBaseHelper dataBaseHelper, String appName, String packageName,
                           String className, String num){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE1, appName);
        values.put(COLUMN_NAME_TITLE2, packageName);
        values.put(COLUMN_NAME_TITLE3, className);
        values.put(COLUMN_NAME_TITLE4, num);
        db.insert(TABLE_NAME, null, values);
    }

    public void insertColor(DataBaseHelper dataBaseHelper, String color){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE5,color);
        db.insert(TABLE_NAME2,null,values);
    }

    public void updateCommandWhereName(DataBaseHelper dataBaseHelper,String appName,String newCommand){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        db.execSQL("update "+TABLE_NAME+" set "+COLUMN_NAME_TITLE4+" = ? where "+COLUMN_NAME_TITLE1+" = ?",new String[]{ newCommand, appName});
    }

    public void updateColor(DataBaseHelper dataBaseHelper,String color){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        db.execSQL("update "+TABLE_NAME2+" set "+COLUMN_NAME_TITLE5+" = ? where "+_ID+" = ?",new String[]{ color, String.valueOf(1)});
    }

    public String getThemeColor(DataBaseHelper dataBaseHelper){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select "+COLUMN_NAME_TITLE5+" from "+TABLE_NAME2,new String[]{});

        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public void clearTable(DataBaseHelper dataBaseHelper){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        db.rawQuery("delete from "+TABLE_NAME,new String[]{});
    }

    public void deleteApp(DataBaseHelper dataBaseHelper,String appName){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        db.execSQL("delete from "+TABLE_NAME+" where "+COLUMN_NAME_TITLE1+" = ?",new String[]{appName});
    }
}