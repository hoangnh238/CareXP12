package com.example.hoang_000.carexp1.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoang_000.carexp1.Model.PlaceDetail;
import com.example.hoang_000.carexp1.R;

/**
 * Created by hoang_000 on 11/04/2018.
 */

public class FavoriteViewHolder extends RecyclerView.ViewHolder {
     public PlaceDetail mPlace;
     public String url;
    public TextView txtNameAddrFav,txtAddrFav;
    public Button btnDirection;
    public FavoriteViewHolder(final View itemView) {
        super(itemView);
        txtNameAddrFav=(TextView)itemView.findViewById(R.id.txt_name_addr_fav);

        txtAddrFav=(TextView)itemView.findViewById(R.id.txt_addr_fav);
        btnDirection=(Button)itemView.findViewById(R.id.btn_chiduong);


    }


}
