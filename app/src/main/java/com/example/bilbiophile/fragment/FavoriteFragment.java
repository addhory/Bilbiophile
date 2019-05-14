package com.example.bilbiophile.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bilbiophile.R;
import com.example.bilbiophile.adapter.FvAdapter;
import com.example.bilbiophile.model.data.Book;
import com.example.bilbiophile.model.data.db.FvBookHelper;
import com.example.bilbiophile.provider.BookProvider;
import com.example.bilbiophile.task.BookParseTask;
import com.example.bilbiophile.task.DownloadTask;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment implements
        DownloadTask.OnDownloadListener,
        BookParseTask.OnBooksParseListener{

    private FvAdapter fvAdapter;
    private ArrayList<Book> bookArrayList = new ArrayList<>();
    private FvBookHelper fvBookHelper;
    private ProgressBarFragment mProgressBar;
    private RetryFragment mRetryFragment;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_favorite, container, false);
            recyclerView = (RecyclerView) mView.findViewById(R.id.rv_listFav);
            recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

            BookProvider provider = new BookProvider(getContext());
            provider.setDownloadListener(this);
            provider.setParseListener(this);



            fvAdapter = new FvAdapter(getContext());
            fvAdapter.setListBk(bookArrayList);
            recyclerView.setAdapter(fvAdapter);
        }

        return mView;
    }
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);
        return view;
    }*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setHasFixedSize(true);


        fvBookHelper=new FvBookHelper(getContext());
        fvBookHelper.open();

        new LoadBookAsync().execute();


        swipeRefreshLayout = mView.findViewById(R.id.swipeRefreshFav);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadBookAsync().execute();

            }
        });


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
        //mRetryFragment.setRetryListener(this);
    }

    @Override
    public void onBooksPreParse(int taskId) {

    }
    @Override
    public void onResume() {
        super.onResume();
        fvAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBooksPostParse(int taskId, List<Book> result) {
        if (mProgressBar != null) {
            getChildFragmentManager().beginTransaction().remove(mProgressBar).commitAllowingStateLoss();
        }

        if (fvAdapter != null) {
            fvAdapter.notifyDataSetChanged();
        }
    }

    private class LoadBookAsync extends AsyncTask<Void, Void, ArrayList<Book>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if (bookArrayList.size() > 0){
                bookArrayList.clear();
            }
        }

        @Override
        protected ArrayList<Book> doInBackground(Void... voids) {
            return fvBookHelper.query();
        }
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            super.onPostExecute(books);
            swipeRefreshLayout.setRefreshing(false);

            bookArrayList.addAll(books);
            fvAdapter.setListBk(bookArrayList);
            fvAdapter.notifyDataSetChanged();

            if (bookArrayList.size() == 0){

            }
        }
    }
}
