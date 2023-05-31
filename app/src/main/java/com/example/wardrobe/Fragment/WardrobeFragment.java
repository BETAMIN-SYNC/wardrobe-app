package com.example.wardrobe.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.wardrobe.FormalActivity;
import com.example.wardrobe.R;


public class WardrobeFragment extends Fragment {

    Button formalBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        formalBtn = view.findViewById(R.id.formalBtn);

        formalBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), FormalActivity.class)));

        return view;
    }
}