package Khack.Q.Kkakkumi.JustClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBcharacterHelper extends SQLiteOpenHelper {
    // 싱클턴 방식
    // 하나의 인스턴스만 가져도 됨
    private static DBcharacterHelper slnstance;

    public static final int DB_VER = 1;
    public static final String DB_NAME = "Kcharacter.db";
    public static final String SQL_CREATE_ENTERS =
            String.format(
                    "CREATE TABLE %s (%S INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER)",
                    DBcharacterINFO.DBinfo.TABLE_NAME,
                    DBcharacterINFO.DBinfo._ID,
                    DBcharacterINFO.DBinfo.COLUMN_NAME,
                    DBcharacterINFO.DBinfo.COLUMN_VISIBLE
            );

    // 테이블이 존재하면 (IF EXISTS) 삭제
    public static final String SQL_DELETE_ENTERS =
            "DROP TABLE IF EXISTS " + DBcharacterINFO.DBinfo.TABLE_NAME;

    public static DBcharacterHelper getInstance(Context cont){
        if(slnstance == null) slnstance = new DBcharacterHelper(cont);
        return slnstance;
    }

    public DBcharacterHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    // 최초로 DB 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTERS);
    }

    // DB 변경 > 버전 올리기 & 변경점 대응
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTERS);
        onCreate(db);
    }
}
