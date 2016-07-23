package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;


import com.kevinmatsubara.japanesesentencemaker.Bun;

import java.util.ArrayList;

/**
 * Created by kevin on 2016/07/10.
 */

public class DatabaseHelper implements BaseColumns{
    private Context context;
    private DatabaseInitializer db_init;

    private enum Query {
        SELECT(0), INSERT(1);
        private final int number;
        Query(int number) {
            this.number = number;
        }
        public int getNumber() {
            return number;
        }
    }

    public DatabaseHelper(Context context) {
        this.context = context;
        db_init = new DatabaseInitializer(context);
    }

    public boolean create_database() {
        try {
            SQLiteDatabase db = db_init.getWritableDatabase();
            db.execSQL(DatabaseInitializer.SQL_CREATE_ENTRIES);
        }
        catch (SQLiteException ex) {
            return false;
        }
        return true;
    }

    public boolean delete_database() {
        try {
            SQLiteDatabase db = db_init.getWritableDatabase();
            db.execSQL(DatabaseInitializer.SQL_DELETE_ENTRIES);
            return !check_if_table_exists();
        }
        catch (SQLiteException ex) {
            return false;
        }
    }

    public boolean check_if_table_exists() {
        try {
            DatabaseInitializer helper = new DatabaseInitializer(context);
            SQLiteDatabase db = helper.getReadableDatabase();

            Cursor cursor = db.query(
                    true,                // Distinct
                    DatabaseInitializer.TABLE_NAME_MAIN,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            return (cursor != null);
        }
        catch(SQLiteException ex) {
            return false;
        }
    }

    private Cursor query(String query, String[] values, Query type) {
        if(!check_if_table_exists()) {
            //throw Exception;
            return null;
        }
        Cursor cursor;

        switch(type) {
            case SELECT:
                SQLiteDatabase db = db_init.getReadableDatabase();
                cursor = db.rawQuery(query, values);
                break;
            case INSERT:
                cursor = null;
                break;
            default:
                cursor = null;
                break;
        }

        // if null, throw exception
        return cursor;
    }

    public Bun get_random_sentence() {
        Cursor cursor = query(
                "SELECT * FROM " + DatabaseInitializer.TABLE_NAME_MAIN + " ORDER BY RANDOM() LIMIT ?",
                new String[]{"1"},
                Query.SELECT);
        cursor.moveToFirst();
        return new Bun(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }

    public ArrayList<Bun> get_sentences() {
        Cursor cursor = query(
                "SELECT * FROM " + DatabaseInitializer.TABLE_NAME_MAIN + "",
                new String[]{},
                Query.SELECT);


        ArrayList<Bun> sentences = new ArrayList<>();
        cursor.moveToFirst();
        for (int x = 0; x < cursor.getCount(); x++) {
            sentences.add(new Bun(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));
            cursor.moveToNext();
        }


        return sentences;
    }

    public boolean insert_sentence(String kanji, String furigana, String meaning) {
        SQLiteDatabase db = db_init.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseInitializer.COLUMN_NAME_KANJI, kanji);
        values.put(DatabaseInitializer.COLUMN_NAME_FURIGANA, furigana);
        values.put(DatabaseInitializer.COLUMN_NAME_MEANING, meaning);

        long newRowId;
        newRowId = db.insert(
                DatabaseInitializer.TABLE_NAME_MAIN,
                DatabaseInitializer.COLUMN_NAME_KANJI,
                values
        );

        return (newRowId != -1);
    }

    public boolean create_new_category(String tableName) {
        String sql = "CREATE TABLE " + tableName + " (" +
                DatabaseInitializer.COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                DatabaseInitializer.COLUMN_NAME_KANJI + " varchar(255), " +
                DatabaseInitializer.COLUMN_NAME_FURIGANA + " varchar(255), " +
                DatabaseInitializer.COLUMN_NAME_MEANING + " varchar(255) )";

        DatabaseInitializer helper = new DatabaseInitializer(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql);
        return false;
    }

    public boolean delete_category(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        DatabaseInitializer helper = new DatabaseInitializer(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql);
        return false;
    }
}
