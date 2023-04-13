package com.example.wardrobe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wardrobe.Fragment.HomeFragment;
import com.example.wardrobe.Fragment.InboxFragment;
import com.example.wardrobe.Fragment.ProfileFragment;
import com.example.wardrobe.Fragment.SearchFragment;
import com.example.wardrobe.Fragment.WardrobeFragment;
import com.example.wardrobe.databinding.ActivityHomeBinding;


public class MainActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            replaceFragment(new ProfileFragment());
        } else {
            replaceFragment(new HomeFragment());
        }


        binding.nav.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.search:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.post:
                    selectedFragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;
                case R.id.wardrobe:
                    replaceFragment(new WardrobeFragment());
                    break;
                case R.id.inbox:
                    replaceFragment(new InboxFragment());
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }

}
