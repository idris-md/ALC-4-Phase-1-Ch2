package com.idris.travelmantic;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.ExecutorEventListener;

import java.util.ArrayList;

public class DealsRecyclerAdapter extends RecyclerView.Adapter<DealsRecyclerAdapter.ViewHolder> {

    public static final String TAG= "BUGG";
    private FirebaseDatabase mFirebaseDatabase ;
    private DatabaseReference mDatabaseReference;

    ArrayList<TravelDeal> mDeals;

    private ChildEventListener mChildEventListener;
    ImageView imageView;

    public DealsRecyclerAdapter() {
        mDeals = new ArrayList<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firebaseFirestore.collection("deals");

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

//                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                    TravelDeal travelDeal = documentSnapshot.toObject(TravelDeal.class);
//                    mDeals.add(travelDeal);
//                    notifyItemInserted(mDeals.size() - 1);
//                }
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    switch (dc.getType()){
                        case ADDED:
                            TravelDeal deal = dc.getDocument().toObject(TravelDeal.class);
                            mDeals.add(deal);
                            notifyItemInserted(mDeals.size() - 1);

                            break;

                    }

                }

            }
        });


//        FirebaseUtil.loadReference("deals",null);
//        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
//        mDatabaseReference = FirebaseUtil.sDatabaseReference;
//
//        mDeals = FirebaseUtil.sTravelDeals;
//        mChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                TravelDeal travelDeals = dataSnapshot.getValue(TravelDeal.class);
//                travelDeals.setId(dataSnapshot.getKey());
//                mDeals.add(travelDeals);
//                notifyItemInserted(mDeals.size() - 1);
//
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        mDatabaseReference.addChildEventListener(mChildEventListener);


    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler, parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TravelDeal deal =mDeals.get(position);
        holder.tvdescription.setText(deal.getDescription());
        holder.tvTitle.setText(deal.getTitle());
        holder.tvCost.setText(deal.getPrice());
        loadImage( deal.getImageUrl());

    }



    @Override
    public int getItemCount() {
        return mDeals.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView tvTitle;
    TextView tvCost;
    TextView tvdescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvCost = itemView.findViewById(R.id.txtPrice);
            tvTitle = itemView.findViewById(R.id.txtTitle);
            tvdescription = itemView.findViewById(R.id.txtDescription);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
        int ppos = getAdapterPosition();
        TravelDeal deal = mDeals.get(ppos);
        Intent intent = new Intent(view.getContext(), AdminActivity.class);
        intent.putExtra("data", deal);
        view.getContext().startActivity(intent); }

    }

    private void loadImage(String deal) {
        Glide.with(imageView.getContext()).load(deal).into(imageView);
    }


}
