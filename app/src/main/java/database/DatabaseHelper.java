package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.util.Log;


import com.kevinmatsubara.japanesesentencemaker.Sentence;

import java.util.ArrayList;

/**
 * Created by kevin on 2016/07/10.
 */

public class DatabaseHelper implements BaseColumns{
    private Context context;
    private DatabaseInitializer db_init;

    public DatabaseHelper(Context context) {
        this.context = context;
        db_init = new DatabaseInitializer(context);
    }

    public boolean createMainTables() {
        try {
            SQLiteDatabase db = db_init.getWritableDatabase();
            db.execSQL(DatabaseInitializer.SQL_CREATE_ENTRIES_MAIN);
            db.execSQL(DatabaseInitializer.SQL_CREATE_ENTRIES_CATEGORIES);
        }
        catch (SQLiteException ex) {
            return false;
        }
        return true;
    }

    public boolean createCategoryTable(String name) {
        try {
            SQLiteDatabase db = db_init.getWritableDatabase();
            String sql =    "CREATE TABLE " + DatabaseInitializer.CATEGORY_PRE + name + " (" +
                            DatabaseInitializer.COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                            DatabaseInitializer.COLUMN_NAME_KANJI + " varchar(255), " +
                            DatabaseInitializer.COLUMN_NAME_FURIGANA + " varchar(255), " +
                            DatabaseInitializer.COLUMN_NAME_MEANING + " varchar(255) ) ";
            db.execSQL(sql);
        }
        catch (SQLiteException ex) {
            return false;
        }
        return true;
    }

    public boolean deleteDatabase() {
        try {
            context.deleteDatabase(DatabaseInitializer.DATABASE_NAME);
            return !checkIfTableExists();
        }
        catch (SQLiteException ex) {
            return false;
        }
    }

    public boolean checkIfTableExists() {
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

    public int getTableSize(String table) {
        Cursor cursor = querySelect("SELECT * FROM " + table, null);
        if(cursor == null)
            return 0;
        return cursor.getCount();
    }

    private Cursor querySelect(String query, String[] values) {
        Cursor cursor;
        SQLiteDatabase dbr = db_init.getReadableDatabase();
        cursor = dbr.rawQuery(query, values);
        return cursor;
    }

    private void queryInsert(String query, String[] values) {
        SQLiteDatabase dbw= db_init.getWritableDatabase();
        dbw.execSQL(query, values);
    }

    public Sentence getRandomSentence() {
        Cursor cursor = querySelect(
                "SELECT * FROM " + DatabaseInitializer.TABLE_NAME_MAIN + " ORDER BY RANDOM() LIMIT ?",
                new String[]{"1"});
        cursor.moveToFirst();
        return new Sentence(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }

    public String getRandomCategoryItem(String category) {
        Cursor cursor = querySelect(
                "SELECT * FROM " + DatabaseInitializer.CATEGORY_PRE + category + " ORDER BY RANDOM() LIMIT ?",
                new String[]{"1"});
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    public ArrayList<Sentence> getSentences() {
        Cursor cursor = querySelect(
                "SELECT * FROM " + DatabaseInitializer.TABLE_NAME_MAIN + "",
                new String[]{});


        ArrayList<Sentence> sentences = new ArrayList<>();
        cursor.moveToFirst();
        for (int x = 0; x < cursor.getCount(); x++) {
            sentences.add(new Sentence(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));
            cursor.moveToNext();
        }
        return sentences;
    }

    public String[] getCategoryTypes() {
        Cursor cursor = querySelect(
                "SELECT * FROM " + DatabaseInitializer.TABLE_NAME_CATEGORIES + "",
                null);

        String[] arr = new String[cursor.getCount()];
        cursor.moveToFirst();
        for(int x = 0; x < cursor.getCount(); x++) {
            arr[x] = cursor.getString(1);
            cursor.moveToNext();
        }
        Log.d("LOG", ""+cursor.getCount());
        return arr;
    }

    public ArrayList<Sentence> getCategoryItems(String type) {
        Cursor cursor = querySelect(
                "SELECT * FROM " + DatabaseInitializer.CATEGORY_PRE + type + "",
                new String[]{});

        ArrayList<Sentence> sentences = new ArrayList<>();
        cursor.moveToFirst();
        for (int x = 0; x < cursor.getCount(); x++) {
            sentences.add(new Sentence(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));
            cursor.moveToNext();
        }
        return sentences;
    }

    public void insertSentence(String furigana, String kanji, String meaning) {
        queryInsert(
                "INSERT INTO " + DatabaseInitializer.TABLE_NAME_MAIN + " VALUES (null, ?, ?, ?)",
                new String[]{furigana, kanji, meaning});
    }

    public void insertCategoryType(String name) {
        queryInsert(
                "INSERT INTO " + DatabaseInitializer.TABLE_NAME_CATEGORIES + " VALUES (null, ?)",
                new String[]{name});
        createCategoryTable(name);
    }

    public void insertCategoryItem(String category, String furigana, String kanji, String meaning) {
        queryInsert(
                "INSERT INTO " + DatabaseInitializer.CATEGORY_PRE + category + " VALUES (null, ?, ?, ?)",
                new String[]{furigana, kanji, meaning});
    }
}
