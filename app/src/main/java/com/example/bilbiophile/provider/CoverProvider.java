package com.example.bilbiophile.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.bilbiophile.model.data.Book;
import com.example.bilbiophile.task.DownloadTask;

import java.io.FileInputStream;
import java.util.HashMap;

public class CoverProvider implements
        DownloadTask.OnDownloadListener {
    public static final String TAG = "___CoverProvider";

    public static final String URL_COVER_SMALL_PREFIX = "https://archive.org/services/img/";
    public static final String URL_COVER_LARGE_PREFIX = "http://archive.org/services/get-item-image.php?identifier=";

    private Context mContext;
    private static HashMap<String, Bitmap> sCachedCovers = new HashMap<>();
    private static HashMap<String, ImageView> sImageViewMap = new HashMap<>();
    private static HashMap<String, DownloadTask> sDownloadTaskMap = new HashMap<>();

    private static OnCoverListener sListener;

    public CoverProvider(Context context) {
        mContext = context;
    }

    @Override
    public void onPreDownload(int taskId) {

    }

    public void setListener(OnCoverListener listener) {
        sListener = listener;
    }

    @Override
    public void onPostDownload(int taskId, DownloadTask.DownloadResult result) {
        // effect from onCancelled???
        if (result == null) {
            return;
        }

        if (result.successful) {
            Bitmap bitmap = loadBitmapFromFile(result.output);

            if (bitmap != null && sImageViewMap.containsKey(result.output)) {
                ImageView iv = sImageViewMap.get(result.output);
                if (iv != null && iv.getTag().toString().equals(result.output)) {
                    TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(mContext.getResources(), bitmap)
                    });

                    iv.setImageDrawable(transitionDrawable);
                    transitionDrawable.startTransition(200);

                    if (sListener != null) {
                        sListener.onCoverLoaded(result.output, bitmap, iv);
                    }
                }
            }
        }

        sImageViewMap.remove(result.output);

        sDownloadTaskMap.remove(result.output);
    }

    @Override
    public void onNetworkError(int taskId, String message) {

    }


    public Bitmap getCoverBitmap(Book book, ImageView iv) {
        String guid = book.guid;

        //
        String prevGuid = (String) iv.getTag();
        if (prevGuid != null && !prevGuid.equals(guid)) {
            cancelDownload(prevGuid);
        }

        // set the tag
        iv.setTag(guid);

        // found in cached
        if (sCachedCovers.containsKey(guid)) {
            return sCachedCovers.get(guid);
        }

        // try to load from internal storage
        Bitmap bitmap = loadBitmapFromFile(guid);
        if (bitmap != null) {
            return bitmap;
        }

        if (sDownloadTaskMap.containsKey(guid)) {
            // already downloading
            // TODO: should we replace the imageview map?
            return null;
        }

        sImageViewMap.put(guid, iv);

        // start downloading
        DownloadTask task = new DownloadTask(mContext, IdProvider.generateTaskId() , guid);
        sDownloadTaskMap.put(guid, task);
        task.addListener(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_COVER_SMALL_PREFIX + guid);

        return null;
    }


    private Bitmap loadBitmapFromFile(String guid) {
        try {
            FileInputStream fis = mContext.openFileInput(guid);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            if (bitmap != null) {
                sCachedCovers.put(guid, bitmap);
                return bitmap;
            }
        } catch (Exception ex) {}

        return null;
    }

    public static void cancelDownload(String guid) {
        if (sDownloadTaskMap.containsKey(guid)) {
            DownloadTask task = sDownloadTaskMap.get(guid);
            if (task != null) {
                task.cancel(true);

                sImageViewMap.remove(guid);
                sDownloadTaskMap.remove(guid);
            }
        }
    }


    public interface OnCoverListener {
        void onCoverLoaded(String guid, Bitmap cover, ImageView imageView);
    }
}
