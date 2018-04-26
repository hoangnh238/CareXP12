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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.hoang_000.carexp1.Model2.Rating;
import com.example.hoang_000.carexp1.adapter.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * hiển thị list các comment và đánh giá của tất cả người dùng ở một địa điểm
 * ví dụ   email : abc@gmail.com   đánh giá sao: 4sao
 *         cmt: nhân viên phục vụ tốt
 */
public class ShowComment extends AppCompatActivity {

    String placeShowRatingId="";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference ratingdb;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;

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
        setContentView(R.layout.activity_show_comment);

        //firebase
        database=FirebaseDatabase.getInstance();
        ratingdb=database.getReference("PlaceRatingDetail");
        recyclerView=(RecyclerView)findViewById(R.id.recyclerComment);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe layout
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!=null)
                {
                    placeShowRatingId=getIntent().getStringExtra(Common.INTENT_PLACE_ID);
                    if(!placeShowRatingId.isEmpty()&&placeShowRatingId!=null)
                    {
                        Query query=ratingdb.orderByChild("placeRatingID").equalTo(Common.currentResult.getPlace_id());
                        FirebaseRecyclerOptions<Rating>options=new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();
                        adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                                holder.ratingbarCmt.setRating(Integer.parseInt(model.getRateValue()));
                                holder.txtEmailCmt.setText(model.getEmailUser());
                                holder.txtCmt.setText(model.getComments());
                            }

                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.show_comment_layout,parent,false);
                                return new ShowCommentViewHolder(view);
                            }
                        };
                        loadComment(placeShowRatingId);
                        

                    }
                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                   mSwipeRefreshLayout.setRefreshing(true);
                if(getIntent()!=null)
                {
                    placeShowRatingId=getIntent().getStringExtra(Common.INTENT_PLACE_ID);
                  if(!placeShowRatingId.isEmpty()&&placeShowRatingId!=null)
                    {
                        Query query=ratingdb.orderByChild("placeRatingID").equalTo(Common.currentResult.getPlace_id());
                        FirebaseRecyclerOptions<Rating>options=new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();
                        adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                                holder.ratingbarCmt.setRating(Integer.parseInt(model.getRateValue()));
                                holder.txtEmailCmt.setText(model.getEmailUser());
                                holder.txtCmt.setText(model.getComments());

                            }

                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.show_comment_layout,parent,false);
                                return new ShowCommentViewHolder(view);
                            }
                        };
                        loadComment(placeShowRatingId);


                    }
                }

            }
        });
    }

    private void loadComment(String placeShowRatingId) {
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
