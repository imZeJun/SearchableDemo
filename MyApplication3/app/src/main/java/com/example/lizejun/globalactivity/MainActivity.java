package com.example.lizejun.globalactivity;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "TAG.MainActivity";
    private EditText mEditText;
    private ListView mListView;
    private SearchManager mSearchManager;
    private List<SearchableInfo> mGlobalSearchableInfoList;
    private GlobalQueryHandler mGlobalQueryHandler;
    private HashMap<Integer, List<LocalBaseBean>> mDateList = new HashMap<>();
    private SuggestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mGlobalSearchableInfoList = mSearchManager.getSearchablesInGlobalSearch();
        mAdapter = new SuggestionAdapter(this);
        for (int i = 0; i < mGlobalSearchableInfoList.size(); i++) {
            List<LocalBaseBean> list = new ArrayList<>();
            mDateList.put(i, list);
            mAdapter.addSource(list);
        }
        mGlobalQueryHandler = new GlobalQueryHandler(getContentResolver());
        mEditText = (EditText) findViewById(R.id.edit_text_main);
        mListView = (ListView) findViewById(R.id.list_view_main);
        mListView.setAdapter(mAdapter);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                asyncGlobalQuery(s.toString());
            }
        });
    }

    private void asyncGlobalQuery(String query) {
        if (query != null) {
            int token = 0;
            for (SearchableInfo info : mGlobalSearchableInfoList) {
                String authority = info.getSuggestAuthority();
                String path = info.getSuggestPath();
                Uri.Builder uriBuilder = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_CONTENT)
                        .authority(authority);
                if (path != null) {
                    uriBuilder.appendEncodedPath(path);
                }
                uriBuilder.appendEncodedPath(SearchManager.SUGGEST_URI_PATH_QUERY).appendEncodedPath(query);
                Uri uri = uriBuilder.build();
                mGlobalQueryHandler.startQuery(token, info, uri, null, null, null, null);
                token++;
                Log.d(TAG, "authority:" + authority + ",path:" + path);
            }
        }
    }

    private class GlobalQueryHandler extends AsyncQueryHandler {

        public GlobalQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            List<LocalBaseBean> list = mDateList.get(token);
            list.clear();
            if (cursor != null) {
                Log.d(TAG, "token:" + token + ",cursor size:" + cursor.getColumnCount());
                while (cursor.moveToNext()) {
                    LocalBaseBean baseBean = new LocalBaseBean();
                    baseBean.setTitle(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)));
                    baseBean.setSub_title(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2)));
                    baseBean.setUri(Uri.parse(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA))));
                    if (cookie != null && cookie instanceof SearchableInfo) {
                        baseBean.setComponentName(((SearchableInfo) cookie).getSearchActivity());
                        baseBean.setAction(((SearchableInfo) cookie).getSuggestIntentAction());
                    }
                    Log.d(TAG, "title:" + baseBean.getTitle());
                    list.add(baseBean);
                }
                cursor.close();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
