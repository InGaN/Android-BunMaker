package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kevinmatsubara.japanesesentencemaker.MainActivity;

/**
 * Created by kevin on 2016/07/23.
 */
public class DatabaseInitializer extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BunMaker.db";
    public static final String DATABASE_PATH = "/data/"+ MainActivity.PACKAGE_NAME +"/databases/";

    public static final String TABLE_NAME_MAIN = "sentences";
    public static final String TABLE_NAME_CATEGORIES = "categories";

    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_KANJI = "kanji";
    public static final String COLUMN_NAME_FURIGANA = "furigana";
    public static final String COLUMN_NAME_MEANING = "meaning";
    public static final String COLUMN_NAME_CATEGORY = "category";

    public static final String CATEGORY_PRE = "category_";

    public static final String SQL_CREATE_ENTRIES_MAIN =
            "CREATE TABLE " + TABLE_NAME_MAIN + " (" +
                    COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_KANJI + " varchar(255), " +
                    COLUMN_NAME_FURIGANA + " varchar(255), " +
                    COLUMN_NAME_MEANING + " varchar(255) ) ";

    public static final String SQL_CREATE_ENTRIES_CATEGORIES =
            "CREATE TABLE " + TABLE_NAME_CATEGORIES + " (" +
                    COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_CATEGORY + " varchar(255) )";

    public static final String SQL_DELETE_ENTRIES_MAIN =
            "DROP TABLE IF EXISTS " + TABLE_NAME_MAIN;
    public static final String SQL_DELETE_ENTRIES_CATEGORIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORIES;

    public DatabaseInitializer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DB", "Init");

    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_MAIN);
        db.execSQL(SQL_CREATE_ENTRIES_CATEGORIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_MAIN);
        db.execSQL(SQL_DELETE_ENTRIES_CATEGORIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
