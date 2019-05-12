package com.example.bilbiophile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class FvAdapter extends RecyclerView.Adapter<FvAdapter.FvViewHolder> {
    @Override
    public FvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FvViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FvViewHolder extends RecyclerView.ViewHolder {


        public FvViewHolder(View itemView) {
            super(itemView);
        }
    }
}
