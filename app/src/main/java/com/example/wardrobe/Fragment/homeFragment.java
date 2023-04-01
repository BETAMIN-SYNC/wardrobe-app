package com.example.wardrobe.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.wardrobe.R;


public class homeFragment extends Fragment {

    ImageButton profileBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profileBtn = view.findViewById(R.id.profileBtn);

        // directs in profile section
        profileBtn.setOnClickListener(v -> {
            profileFragment profileFragment = new profileFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
        });

        return view;
    }
}