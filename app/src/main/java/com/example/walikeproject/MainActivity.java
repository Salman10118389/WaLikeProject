package com.example.walikeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.walikeproject.Adapter.FragmentsAdapter;
import com.example.walikeproject.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.security.acl.Group;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth firebaseAuthMain;
    FragmentsAdapter fragmentsAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TabLayout tabLayout = findViewById(R.id.tabMain);
        ViewPager2 viewPager2 = findViewById(R.id.viewPagerMain);


        firebaseAuthMain = FirebaseAuth.getInstance();
        fragmentsAdapter = new FragmentsAdapter(this);
        viewPager2.setAdapter(fragmentsAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               if (position == 0 ){
                   tab.setText("CHATS");
               }
                if (position == 1 ){
                    tab.setText("STATUS");
                }
                if (position == 2 ){
                    tab.setText("CALLS");
                }
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutItem:
                Toast.makeText(this, "See you next Time :)", Toast.LENGTH_SHORT).show();
                firebaseAuthMain.signOut();
                Intent goToSignInAct = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(goToSignInAct);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}