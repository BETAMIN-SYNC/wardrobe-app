package com.example.wardrobe;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wardrobe.Fragment.homeFragment;
import com.example.wardrobe.Fragment.inboxFragment;
import com.example.wardrobe.Fragment.searchFragment;
import com.example.wardrobe.Fragment.wardrobeFragment;
import com.example.wardrobe.databinding.ActivityHomeBinding;


public class MainActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new homeFragment());

        binding.nav.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new homeFragment());
                    break;
                case R.id.search:
                    replaceFragment(new searchFragment());
                    break;
                case R.id.post:
                    selectedFragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;
                case R.id.wardrobe:
                    replaceFragment(new wardrobeFragment());
                    break;
                case R.id.inbox:
                    replaceFragment(new inboxFragment());
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}
