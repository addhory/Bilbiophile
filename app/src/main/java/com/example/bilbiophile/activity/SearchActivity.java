package com.example.bilbiophile.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bilbiophile.R;
import com.example.bilbiophile.fragment.ProgressBarFragment;
import com.example.bilbiophile.fragment.RetryFragment;
import com.example.bilbiophile.helper.Helper;
import com.example.bilbiophile.model.data.Book;
import com.example.bilbiophile.model.data.RSSBook;
import com.example.bilbiophile.provider.CoverProvider;
import com.example.bilbiophile.provider.IdProvider;
import com.example.bilbiophile.task.SearchParseTask;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements
        SearchParseTask.OnSearchListener, AdapterView.OnItemClickListener {
    private static final String TAG = "___SearchActivity";

    private ListView mBookList;
    private BookListAdapter mAdapter;
    private ProgressBarFragment mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupContent();
        Log.d(TAG, "onCreate: " + getIntent());
        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(getIntent());
    }
    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d(TAG, "handleIntent: "+Intent.ACTION_SEARCH.equals(intent.getAction()));

            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query == null || query.isEmpty()) { return; }

            SearchParseTask task = new SearchParseTask(this, IdProvider.generateTaskId());
            task.addListener(this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
        }
    }

    private void setupContent() {
        mBookList = findViewById(R.id.lsvBooks);
        mAdapter = new BookListAdapter(this, R.id.lsvBooks);
        mBookList.setAdapter(mAdapter);
        mBookList.setOnItemClickListener(this);
    }

    @Override
    public void onPreSearch(int taskId) {
        mProgressBar = new ProgressBarFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_search_progress, mProgressBar).commitAllowingStateLoss();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPostSearch(int taskId, List<Book> result) {
        if (mProgressBar != null) {
            getSupportFragmentManager().beginTransaction().remove(mProgressBar).commitAllowingStateLoss();
        }

        if (result == null) {
        } else if (result.size() == 0) {
            RetryFragment retryFragment = new RetryFragment();
            retryFragment.setContent(R.drawable.ic_search_black_24dp, getString(R.string.msg_search_result_empty), false);
            getSupportFragmentManager().beginTransaction().add(R.id.activity_search_progress, retryFragment).commitAllowingStateLoss();
        } else {
            mAdapter.setList(result);
        }
    }


    @Override
    public void onSearchError(int taskId, String message) {
        if (mProgressBar != null) {
            getSupportFragmentManager().beginTransaction().remove(mProgressBar).commitAllowingStateLoss();
        }

        RetryFragment retryFragment = new RetryFragment();
        retryFragment.setContent(R.drawable.spacebook, message, false);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_search_progress, retryFragment).commitAllowingStateLoss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = mAdapter.getItem(position);

        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    class BookListAdapter extends ArrayAdapter<Book> {
        private Context mContext;
        private LayoutInflater inflater;

        private CoverProvider mCoverProvider = new CoverProvider(getContext());
        private List<Book> mBooks = new ArrayList<>();

        public BookListAdapter(@NonNull Context context, int resource) {
            super(context, resource);

            this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return this.mBooks.size();
        }

        @Override
        public Book getItem(int position) {
            return this.mBooks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if (view == null) {
                view = inflater.inflate(R.layout.search_item, viewGroup, false);

                holder = new ViewHolder();
                holder.ivCover = view.findViewById(R.id.ivsCover);
                holder.tvTitle = view.findViewById(R.id.tvsTitle);
                holder.tvPubdate = view.findViewById(R.id.tvsPubdate);
                holder.tvDescription = view.findViewById(R.id.tvsDescription);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Book book = this.mBooks.get(position);

            Bitmap bitmap = mCoverProvider.getCoverBitmap(book, holder.ivCover);
            holder.ivCover.setImageBitmap(bitmap);

            holder.tvTitle.setText(book.title);
            holder.tvPubdate.setText(String.format("%s", Helper.milliToString(book.pubDate, RSSBook.TIME_FORMAT_PUBDATE_COMPACT)));
            holder.tvDescription.setText(book.description);

            return view;
        }

        public void setList(List<Book> list) {
            this.mBooks = list;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvPubdate;
        TextView tvDescription;
    }

}
