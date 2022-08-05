package com.example.walikeproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walikeproject.ChatsDetailActivity;
import com.example.walikeproject.Models.Users;
import com.example.walikeproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{
    ArrayList<Users> usersArrayList;
    Context context;

    public UsersAdapter(ArrayList<Users> usersArrayList, Context context) {
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = usersArrayList.get(position);

        //Instance
        String userName = user.getUsername();
        String profilePic = user.getProfileImage();
        String lastMessage = user.getLastMessage();
        //Load Data Holder
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_baseline_people_24).into(holder.profilePic);
        holder.userName.setText(userName);
        holder.lastMessage.setText(lastMessage);

//        FirebaseDatabase.getInstance().getReference()
//                        .child("chats"
//                        )
//                        .child(FirebaseAuth.getInstance().getUid() + user.getUserId())
//                        .orderByChild("timestamp")
//                        .limitToLast(1)
//                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        if (snapshot.hasChildren()){
//                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                holder.lastMessage.setText(dataSnapshot.child("messages").getValue().toString());
//                                                            }
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChatsDetail = new Intent(context, ChatsDetailActivity.class);
                goToChatsDetail.putExtra("userId", user.getUserId());
                goToChatsDetail.putExtra("profilePic", user.getProfileImage());
                goToChatsDetail.putExtra("userName", user.getUsername());
                context.startActivity(goToChatsDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        TextView userName, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);


        }
    }
}
