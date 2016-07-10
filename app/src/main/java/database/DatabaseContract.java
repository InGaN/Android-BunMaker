package database;

import android.provider.BaseColumns;

/**
 * Created by kevin on 2016/07/10.
 */
public final class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "kanji";

        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_KANJI = "kanji";
        public static final String COLUMN_NAME_FURIGANA = "furigana";
        public static final String COLUMN_NAME_MEANING = "meaning";
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty";

    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    FeedEntry.COLUMN_NAME_KANJI + " varchar(255), " +
                    FeedEntry.COLUMN_NAME_FURIGANA + " varchar(255), " +
                    FeedEntry.COLUMN_NAME_MEANING + " varchar(255), " +
                    FeedEntry.COLUMN_NAME_DIFFICULTY + " INTEGER NOT NULL )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static final String SQL_TABLE_EXISTS =
            "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name='" + FeedEntry.TABLE_NAME +"'";
}