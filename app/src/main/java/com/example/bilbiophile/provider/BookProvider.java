package com.example.bilbiophile.provider;

import android.content.Context;
import android.os.AsyncTask;

import com.example.bilbiophile.R;
import com.example.bilbiophile.model.data.Book;
import com.example.bilbiophile.task.BookParseTask;
import com.example.bilbiophile.task.DownloadTask;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class BookProvider implements
        DownloadTask.OnDownloadListener,
        BookParseTask.OnBooksParseListener {
    private static final String TAG = "___BookProvider";

    public static final String FILE_BOOKS_XML = "books.xml";
    public static final String URL_ARCHIVE_LIBRIVOX = "http://archive.org/services/collection-rss.php?collection=librivoxaudio";

    private Context mContext;
    private DownloadTask.OnDownloadListener mDownloadListener;
    private BookParseTask.OnBooksParseListener mParseListener;
    private static List<Book> sBooks = new ArrayList<>();

    private static boolean taskInProgress = false;

    public BookProvider(Context context) {
        this.mContext = context;
    }

    public void setDownloadListener(DownloadTask.OnDownloadListener listener) {
        this.mDownloadListener = listener;
    }

    public void setParseListener(BookParseTask.OnBooksParseListener listener) {
        this.mParseListener = listener;
    }

    public List<Book> getBooks() {
        if (sBooks.size() == 0 && !taskInProgress) {
            startDownload();
        }

        return sBooks;
    }

    private void startDownload() {
        DownloadTask task = new DownloadTask(mContext, IdProvider.generateTaskId(), FILE_BOOKS_XML);
        task.addListener(this);
        if (mDownloadListener != null) {
            task.addListener(mDownloadListener);
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_ARCHIVE_LIBRIVOX);
    }

    private void startParsing() {
        // start a new task to parse
        BookParseTask task = new BookParseTask(mContext, IdProvider.generateTaskId());
        task.addListener(this);
        if (mParseListener != null) {
            task.addListener(mParseListener);
        }

        try {
            FileInputStream file = mContext.openFileInput(FILE_BOOKS_XML);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
        } catch (Exception ex) {
            if (mDownloadListener != null) {
                mDownloadListener.onNetworkError(0, mContext.getResources().getString(R.string.msg_network_error));
            }
        }
    }

    @Override
    public void onPreDownload(int taskId) {
        taskInProgress = true;
    }

    @Override
    public void onPostDownload(int taskId, DownloadTask.DownloadResult result) {
        startParsing();
    }

    @Override
    public void onNetworkError(int taskId, String message) {

    }

    @Override
    public void onBooksPreParse(int taskId) {
    }

    @Override
    public void onBooksPostParse(int taskId, List<Book> result) {
        taskInProgress = false;
        sBooks = result;
    }
}
