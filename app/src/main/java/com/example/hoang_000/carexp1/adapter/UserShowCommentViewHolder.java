package com.example.hoang_000.carexp1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hoang_000.carexp1.R;

/**
 * Created by hoang_000 on 13/04/2018.
 */

public class UserShowCommentViewHolder extends RecyclerView.ViewHolder{
    public TextView txtPlaceNameCmt2,txtCmt2;
    public RatingBar ratingbarCmt2;
    public UserShowCommentViewHolder(View itemView) {
        super(itemView);
        txtPlaceNameCmt2=(TextView)itemView.findViewById(R.id.txtPlaceNameCmt2);
        txtCmt2=(TextView)itemView.findViewById(R.id.txtCmt2);
        ratingbarCmt2=(RatingBar)itemView.findViewById(R.id.ratingBarCmt2);


    }
}
