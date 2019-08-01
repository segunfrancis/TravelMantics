package com.android.computer.travelmantics;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TravelDealAdapter extends RecyclerView.Adapter<TravelDealAdapter.TravelDealViewHolder> {

    private ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    public TravelDealAdapter() {
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        this.deals = FirebaseUtil.deals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public TravelDealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.deal_item, viewGroup, false);
        return new TravelDealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDealViewHolder travelDealViewHolder, int i) {
        TravelDeal deal = deals.get(i);
        travelDealViewHolder.tvTitle.setText(deal.getTitle());
        travelDealViewHolder.tvDescription.setText(deal.getDescription());
        travelDealViewHolder.tvPrice.setText(deal.getPrice());
        FirebaseUtil.connectStorage();
        travelDealViewHolder.showImage(deal.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class TravelDealViewHolder extends RecyclerView.ViewHolder /*TODO: implements View.OnClickListener*/ {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;
        ImageView imageDeal;
        Context context;

        public TravelDealViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageDeal = itemView.findViewById(R.id.imageDeal);
        }

        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                CircularProgressDrawable placeHolder = new CircularProgressDrawable(context.getApplicationContext());
                placeHolder.setStrokeWidth(5.0f);
                placeHolder.setColorSchemeColors(Color.rgb(50, 255, 50), Color.rgb(216, 27, 96));
                placeHolder.setCenterRadius(30.0f);
                placeHolder.start();
                Glide.with(context)
                        .load(url)
                        .apply(new RequestOptions().override(160, 160))
                        .placeholder(placeHolder)
                        .error(R.drawable.googleg_disabled_color_18)
                        .centerCrop()
                        .into(imageDeal);
            }
        }
    }
}