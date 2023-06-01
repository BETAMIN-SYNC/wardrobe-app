package com.example.wardrobe.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.wardrobe.ActiveActivity;
import com.example.wardrobe.BusinessActivity;
import com.example.wardrobe.CasualActivity;
import com.example.wardrobe.EveningActivity;
import com.example.wardrobe.FormalActivity;
import com.example.wardrobe.OthersActivity;
import com.example.wardrobe.R;


public class WardrobeFragment extends Fragment {

    ImageButton formalBtn, casualBtn, activeBtn, businessBtn, eveningBtn;
    Button othersBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        formalBtn = view.findViewById(R.id.formalBtn);
        casualBtn = view.findViewById(R.id.casualBtn);
        activeBtn = view.findViewById(R.id.activeBtn);
        businessBtn = view.findViewById(R.id.businessBtn);
        eveningBtn = view.findViewById(R.id.eveningBtn);
        othersBtn = view.findViewById(R.id.othersBtn);

        formalBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), FormalActivity.class)));
        casualBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), CasualActivity.class)));
        activeBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), ActiveActivity.class)));
        businessBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), BusinessActivity.class)));
        eveningBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), EveningActivity.class)));
        othersBtn.setOnClickListener(view1 -> startActivity(new Intent(getContext(), OthersActivity.class)));

        return view;
    }
}