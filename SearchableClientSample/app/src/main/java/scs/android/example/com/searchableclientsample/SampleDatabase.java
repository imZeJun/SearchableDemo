package scs.android.example.com.searchableclientsample;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SampleDatabase {

    private static final String TAG = "SampleDatabase";
    public static final String KEY_ID = BaseColumns._ID;
    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    private static final String DATABASE_NAME = "dictionary.db";
    private static final String TABLE_NAME = "dictionary";
    private static final int DATABASE_VERSION = 2;
    private final SampleOpenHelper mDatabaseOpenHelper;

    public SampleDatabase(Context context) {
        mDatabaseOpenHelper = new SampleOpenHelper(context);
    }

    public Cursor getWordMatchQuery(String query) {
        String selection = "select * from " + TABLE_NAME + " where " + KEY_WORD + " like ?";
        String[] selectionArgs = new String[] {"%" + query + "%"};
        SQLiteDatabase database = mDatabaseOpenHelper.getReadableDatabase();
        return database.rawQuery(selection, selectionArgs);
    }

    public Cursor getWordMatchId(String id) {
        String selection = "select * from " + TABLE_NAME + " where " + KEY_ID + " = ?";
        String[] selectionArgs = new String[]{id};
        SQLiteDatabase database = mDatabaseOpenHelper.getReadableDatabase();
        return database.rawQuery(selection, selectionArgs);
    }

    private static class SampleOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String CREATE_TABLE =
                "create table " + TABLE_NAME + " (" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_WORD + " varchar(20), " +
                        KEY_DEFINITION + " varchar(20));";

        SampleOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(CREATE_TABLE);
            loadDictionary();
        }

        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadWords();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadWords() throws IOException {
            Log.d(TAG, "Loading words...");
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.definitions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, "-");
                    if (strings.length < 2) continue;
                    long id = addWord(strings[0].trim(), strings[1].trim());
                    if (id < 0) {
                        Log.e(TAG, "unable to add word: " + strings[0].trim());
                    }
                }
            } finally {
                reader.close();
            }
            Log.d(TAG, "DONE loading words.");
        }

        public long addWord(String word, String definition) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_WORD, word);
            initialValues.put(KEY_DEFINITION, definition);
            return mDatabase.insert(TABLE_NAME, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
