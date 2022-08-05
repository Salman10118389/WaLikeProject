package com.example.walikeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.walikeproject.Adapter.ChatsAdapter;
import com.example.walikeproject.Models.MessageModels;
import com.example.walikeproject.databinding.ActivityChatsDetailBinding;
import com.example.walikeproject.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ChatsDetailActivity extends AppCompatActivity {
    ActivityChatsDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    private byte enc[] = {5, 51, 12, 86, 105, 4, -31, -21, -23, 13, 12, 54, 100, 45, 23, 67};
    private Cipher cipher, deCipher;
    private SecretKeySpec secretKeySpec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Instance
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        secretKeySpec = new SecretKeySpec(enc, "AES");
        try {
            cipher = Cipher.getInstance("AES");
            deCipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        //Instansiate & Get the Data From Adapter
        final String senderId = firebaseAuth.getUid();
        String ReceiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");
        getSupportActionBar().hide();

        binding.usernameDetailChats.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.circleImageView);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModels> messageModels = new ArrayList<>();
        final ChatsAdapter adapter = new ChatsAdapter(messageModels, this, ReceiverId);

        binding.recyclerVewDetailChats.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerVewDetailChats.setLayoutManager(linearLayoutManager);

        final String senderRoom = senderId + ReceiverId;
        final String receiverRoom = ReceiverId + senderId;

        database.getReference().child("chats")
                        .child(senderRoom)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        messageModels.clear();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            MessageModels model = dataSnapshot.getValue(MessageModels.class);
                                            model.setMessageId(dataSnapshot.getKey());
                                            try {
                                                model.setMessageText(AESDecryptionMethod(model.getMessageText()));
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
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
                String message = AESEncryptionMethod(binding.enterMessage.getText().toString());
                final MessageModels model = new MessageModels(senderId, message);
                model.setTimestamp(new Date().getTime());
                binding.enterMessage.setText("");

                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });

    }
    private String AESEncryptionMethod(String string) {
        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String returnString = null;
        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] encryptedByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;
        byte[] decryption;
        try {
            deCipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = deCipher.doFinal(encryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }
}