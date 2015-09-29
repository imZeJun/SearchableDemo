package scs.android.example.com.searchableclientsample;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lizejun on 2015/09/25.
 */
public class SampleProvider extends ContentProvider {

    private static final String TAG = "SampleProvider";
    public static String AUTHORITY = "com.example.android.searchableclientsample.SampleProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/dictionary");
    public static final String SEARCH_WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.example.android.searchabledict";
    public static final String SEARCH_WORD_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.example.android.searchabledict";
    private SampleDatabase mDictionary;
    private static final int SEARCH_SUGGEST = 0;
    private static final int SEARCH_WORDS = 1;
    private static final int SEARCH_WORD = 2;
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "dictionary", SEARCH_WORDS);
        matcher.addURI(AUTHORITY, "dictionary/#", SEARCH_WORD);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDictionary = new SampleDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                String query = uri.getLastPathSegment();
                Cursor cursor =  mDictionary.getWordMatchQuery(query);
                return changeCursorSearchable(cursor);
            case SEARCH_WORDS:
                return null;
            case SEARCH_WORD:
                String id = uri.getLastPathSegment();
                return mDictionary.getWordMatchId(id);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private Cursor changeCursorSearchable(Cursor cursor) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[] {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA});
        while (cursor != null && cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(SampleDatabase.KEY_ID));
            String data = CONTENT_URI + "/" + id;
            String word = cursor.getString(cursor.getColumnIndex(SampleDatabase.KEY_WORD));
            String def = cursor.getString(cursor.getColumnIndex(SampleDatabase.KEY_DEFINITION));
            matrixCursor.addRow(new Object[] {id, null, word, def, data});
        }
        return matrixCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case SEARCH_WORDS:
                return SEARCH_WORDS_MIME_TYPE;
            case SEARCH_WORD:
                return SEARCH_WORD_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
