package com.example.walikeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.walikeproject.Adapter.ChatsAdapter;
import com.example.walikeproject.Models.MessageModels;
import com.example.walikeproject.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    ChatsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMainAct = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(goToMainAct);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessageModels> messageModels = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.usernameDetailChats.setText("Group Chat");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerVewDetailChats.setLayoutManager(linearLayoutManager);

        final ChatsAdapter adapter = new ChatsAdapter(messageModels, this);
        binding.recyclerVewDetailChats.setAdapter(adapter);

        database.getReference()
                        .child("Group Chat")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        messageModels.clear();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            MessageModels model = dataSnapshot.getValue(MessageModels.class);
                                            messageModels.add(model);
                                        }
                                            adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = binding.enterMessage.getText().toString();
                final MessageModels model = new MessageModels(senderId, message);

                model.setTimestamp(new Date().getTime());
                binding.enterMessage.setText("");

                database.getReference().child("Group Chats")
                        .push()
                        .setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(GroupChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}