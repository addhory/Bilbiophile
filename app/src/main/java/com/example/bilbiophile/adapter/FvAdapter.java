package com.example.bilbiophile.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

public class FvAdapter extends RecyclerView.Adapter<FvAdapter.FvViewHolder> {
    private Cursor cursor;
    private Context context;
    private ArrayList<Book> bookArrayList;

    public FvAdapter(Context context) {
        this.context = context;
    }
    public ArrayList<Book> getListBook(){
        return bookArrayList;
    }
    public void setListBk(ArrayList<Book> bk) {
        this.bookArrayList = bk;
    }
    @NonNull
    @Override
    public FvViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FvViewHolder(LayoutInflater.from(context).inflate(R.layout.cv_books, viewGroup ,false));

    }

    @Override
    public void onBindViewHolder(@NonNull FvViewHolder fvViewHolder, int i) {
        final Book book = getListBook().get(i);
        fvViewHolder.tvTitle.setText(book.title);
        fvViewHolder.tvPubdate.setText(Helper.milliToString(book.pubDate, RSSBook.TIME_FORMAT_PUBDATE_COMPACT));
        fvViewHolder.tvDescription.setText(book.description);


        fvViewHolder.itemVieweFv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveDetailMovieActivity = new Intent(context, BookDetailActivity.class);
                moveDetailMovieActivity.putExtra("book", book);
               // moveDetailMovieActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(moveDetailMovieActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListBook().size();
    }

    public class FvViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCover;
        TextView tvTitle;
        TextView tvPubdate;
        TextView tvDescription;
        private View itemVieweFv;
        public FvViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.ivnCover);
            tvTitle = (TextView) itemView.findViewById(R.id.tvnTitle);
            tvPubdate = (TextView) itemView.findViewById(R.id.tvnPubdate);
            tvDescription = (TextView) itemView.findViewById(R.id.tvnDescription);
            itemVieweFv=itemView;
        }

    }
}

