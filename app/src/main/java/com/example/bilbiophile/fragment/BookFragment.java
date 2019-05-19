package com.example.bilbiophile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bilbiophile.R;
import com.example.bilbiophile.activity.BookDetailActivity;
import com.example.bilbiophile.helper.Helper;
import com.example.bilbiophile.model.data.Book;
import com.example.bilbiophile.model.data.RSSBook;
import com.example.bilbiophile.provider.BookProvider;
import com.example.bilbiophile.provider.CoverProvider;
import com.example.bilbiophile.task.BookParseTask;
import com.example.bilbiophile.task.DownloadTask;

import java.util.List;

public class BookFragment extends BaseFragment implements
        DownloadTask.OnDownloadListener,
        BookParseTask.OnBooksParseListener {

    private RecyclerView mRecyclerView;
    private BookListAdapter mAdapter;
    private ProgressBarFragment mProgressBar;
    private RetryFragment mRetryFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_books, container, false);

            mRecyclerView = mView.findViewById(R.id.book_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

            BookProvider provider = new BookProvider(getContext());
            provider.setDownloadListener(this);
            provider.setParseListener(this);

            mAdapter = new BookListAdapter(provider);
            mRecyclerView.setAdapter(mAdapter);
        }

        return mView;
    }

    @Override
    public void onPreDownload(int taskId) {
        mProgressBar = new ProgressBarFragment();
        getChildFragmentManager().beginTransaction().add(R.id.tab_news_progress, mProgressBar).commitAllowingStateLoss();
    }

    @Override
    public void onPostDownload(int taskId, DownloadTask.DownloadResult result) {

    }

    @Override
    public void onNetworkError(int taskId, String message) {
        if (mProgressBar != null) {
            getChildFragmentManager().beginTransaction().remove(mProgressBar).commitAllowingStateLoss();
        }
        mRetryFragment = new RetryFragment();
        mRetryFragment.setContent(R.drawable.spacebook, getString(R.string.msg_network_error), true);
        getChildFragmentManager().beginTransaction().add(R.id.tab_news_progress, mRetryFragment).commitAllowingStateLoss();
    }

    @Override
    public void onBooksPreParse(int taskId) {

    }

    @Override
    public void onBooksPostParse(int taskId, List<Book> result) {
        if (mProgressBar != null) {
            getChildFragmentManager().beginTransaction().remove(mProgressBar).commitAllowingStateLoss();
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }



    class BookListAdapter extends RecyclerView.Adapter<BookHolder> {
        private BookProvider mProvider;
        private CoverProvider mCoverProvider = new CoverProvider(getContext());

        public BookListAdapter(BookProvider provider) {
            this.mProvider = provider;

        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View itemView = inflater.inflate(R.layout.cv_books, parent, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mRecyclerView.getChildLayoutPosition(v);
                    Book book = mProvider.getBooks().get(position);

                    Intent intent = new Intent(getContext(), BookDetailActivity.class);
                    intent.putExtra("book", book);
                    startActivity(intent);
                }
            });

            return new BookHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            Book item = mProvider.getBooks().get(position);

            Bitmap bitmap = mCoverProvider.getCoverBitmap(item, holder.ivCover);
            holder.ivCover.setImageBitmap(bitmap);

            holder.tvTitle.setText(item.title);
            holder.tvPubdate.setText(Helper.milliToString(item.pubDate, RSSBook.TIME_FORMAT_PUBDATE_COMPACT));
            holder.tvDescription.setText(item.description);
        }

        @Override
        public int getItemCount() {
            return mProvider.getBooks().size();
        }
    }

    static class BookHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvPubdate;
        TextView tvDescription;

        public BookHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivnCover);
            tvTitle = itemView.findViewById(R.id.tvnTitle);
            tvPubdate = itemView.findViewById(R.id.tvnPubdate);
            tvDescription = itemView.findViewById(R.id.tvnDescription);
        }
    }
}
