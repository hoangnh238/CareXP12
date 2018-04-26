package com.example.hoang_000.carexp1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoang_000.carexp1.Model2.Favorites;
import com.example.hoang_000.carexp1.R;

import java.util.ArrayList;

/**
 * Created by hoang_000 on 09/04/2018.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    ArrayList<Favorites> favorites;
    Context context;

    public FavoriteAdapter(ArrayList<Favorites> favorites, Context context) {
        this.favorites = favorites;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View itemView=layoutInflater.inflate(R.layout.item_fav,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       holder.tv_name_fav.setText(favorites.get(position).getAddressFav());
       holder.tv_address_fav.setText(favorites.get(position).getNameofplacefav());
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }
    public void removeItem(int position)
    {
        favorites.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favorites item,int position)
    {
        favorites.add(position,item);
        notifyItemInserted(position);
    }
    public Favorites getItem(int position)
    {
        return favorites.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
         TextView tv_name_fav,tv_address_fav;
        public ViewHolder(View itemView) {
            super(itemView);
//            tv_name_fav=(TextView)itemView.findViewById(R.id.tv_name_fav);
//            tv_address_fav=(TextView)itemView.findViewById(R.id.tv_place_fav);
        }
    }
}
