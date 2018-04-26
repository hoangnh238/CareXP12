package com.example.hoang_000.carexp1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.adapter.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class test extends AppCompatActivity {

    String placeShowRatingId="";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference ratingdb=database.getReference("PlaceRatingDetail");
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;
    Query query=ratingdb.orderByChild("placeRatingID").equalTo(Common.currentResult.getPlace_id());
    @Override
    protected void attachBaseContext(Context newBase) {

        // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        super.attachBaseContext(newBase);
    }
    @Override
    protected void onStart() {
        super.onStart();
        attachRecyclerViewAdapter();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }
    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Rating> options =
                new FirebaseRecyclerOptions.Builder<Rating>()
                        .setQuery(query, Rating.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
            @Override
            public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ShowCommentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.show_comment_layout, parent, true));
            }

            @Override
            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                holder.ratingbarCmt.setRating(Integer.parseInt(model.getRateValue()));
                holder.txtEmailCmt.setText(model.getEmailUser());
                holder.txtCmt.setText(model.getComments());
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                /*mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);*/
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Arkhip_font.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());
        setContentView(R.layout.activity_show_comment);
        // cách 2
       final  ArrayList<Rating> ratings = new ArrayList<>();
        //firebase
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Rating rating=issue.getValue(Rating.class);
                        ratings.add(rating);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
        // có ratins tạo custom adapter cho listview

    //swipe layout



    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
