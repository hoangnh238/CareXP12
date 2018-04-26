package com.example.hoang_000.carexp1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.R;

/**
 * Created by hoang_000 on 10/04/2018.
 */

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {
    public TextView txtEmailCmt,txtCmt;
    public RatingBar ratingbarCmt;
    public ShowCommentViewHolder(View itemView) {
        super(itemView);
        txtEmailCmt=(TextView)itemView.findViewById(R.id.txtEmailCmt);
        txtCmt=(TextView)itemView.findViewById(R.id.txtCmt);
        ratingbarCmt=(RatingBar)itemView.findViewById(R.id.ratingBarCmt);


    }
}
