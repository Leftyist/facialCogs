package com.arianfarahani.facialcogs.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;


/**
 * Created by jesse on 11/12/17.
 */

public class TwinAdapter extends RecyclerView.Adapter<TwinAdapter.ViewHolder> {

    //Constructor
    public TwinAdapter() {

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //TODO fill the viewholder object with necessary data

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    //ViewHolder class
    class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView image;
        TextView username;
        EditText date;

        //Constructor
        public ViewHolder(View itemView) {
            super(itemView);

        }

    }
}


