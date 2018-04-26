package com.example.hoang_000.carexp1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.adapter.ShowCommentViewHolder;
import com.example.hoang_000.carexp1.adapter.UserShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * hiển thị comment và rating của người dùng đã đánh giá một địa điểm nào đó ở menu user
 * ví dụ    tên địa điểm A   đánh giá sao:2sao
 *          comment:abc
 */
public class UserShowComment extends AppCompatActivity {
    String placeShowRatingId="";
    String userIDshowCmt="";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference ratingdb;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<Rating,UserShowCommentViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {

        // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Arkhip_font.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());
        setContentView(R.layout.activity_user_show_comment);
        userIDshowCmt = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //firebase
        database=FirebaseDatabase.getInstance();
        ratingdb=database.getReference("PlaceRatingDetail");
        recyclerView=(RecyclerView)findViewById(R.id.recyclerComment2);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe layout
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!=null)
                {

                        Query query=ratingdb.orderByChild("userID").equalTo(userIDshowCmt);
                        FirebaseRecyclerOptions<Rating> options=new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();
                        adapter=new FirebaseRecyclerAdapter<Rating, UserShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull UserShowCommentViewHolder holder, int position, @NonNull Rating model) {
                                holder.ratingbarCmt2.setRating(Integer.parseInt(model.getRateValue()));
                                holder.txtPlaceNameCmt2.setText(model.getPlacenameRate());
                                holder.txtCmt2.setText(model.getComments());
                            }

                            @Override
                            public UserShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.show_comment_user_layout,parent,false);
                                return new UserShowCommentViewHolder(view);
                            }
                        };
                        loadComment();



                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if(getIntent()!=null)
                {
                        Query query=ratingdb.orderByChild("userID").equalTo(userIDshowCmt);
                        FirebaseRecyclerOptions<Rating>options=new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();
                        adapter=new FirebaseRecyclerAdapter<Rating, UserShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull UserShowCommentViewHolder holder, int position, @NonNull Rating model) {
                                holder.ratingbarCmt2.setRating(Integer.parseInt(model.getRateValue()));
                                holder.txtPlaceNameCmt2.setText(model.getPlacenameRate());
                                holder.txtCmt2.setText(model.getComments());

                            }

                            @Override
                            public UserShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.show_comment_user_layout,parent,false);
                                return new UserShowCommentViewHolder(view);
                            }
                        };
                        loadComment();



                }

            }
        });
    }

    private void loadComment( ) {
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
