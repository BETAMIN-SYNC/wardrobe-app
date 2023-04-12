package com.example.wardrobe.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
// import com.example.wardrobe.Adapter.MyDashboardAdapter;
import com.example.wardrobe.Model.Post;
import com.example.wardrobe.Model.User;
import com.example.wardrobe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class profileFragment extends Fragment {

    ImageView image_profile, settings;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

    RecyclerView recyclerView;
    //MyDashboardAdapter myDashboardAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton dashboard, bookmark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        if (getContext() != null) {
            profileid = prefs.getString("profileid", "none");
        }

        image_profile = view.findViewById(R.id.image_profile);
        settings = view.findViewById(R.id.settings);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        edit_profile= view.findViewById(R.id.edit_profile);
        dashboard = view.findViewById(R.id.dashboard);
        bookmark = view.findViewById(R.id.bookmark);

        /**
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3 );
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myDashboardAdapter = new MyDashboardAdapter(getContext(), postList);
        recyclerView.setAdapter(myDashboardAdapter);
        */

        userInfo();
        getFollowers();
        getNrPosts();
        // myDashboard();

        if (profileid.equals(firebaseUser.getUid())) {
            edit_profile.setText("Edit Profile");
        } else {
            checkFollow();
            bookmark.setVisibility(View.GONE);
        }

        // updates the follow status of the displayed user.
        edit_profile.setOnClickListener(v -> {
            String btn = edit_profile.getText().toString();

            switch (btn) {
                case "Edit Profile":
                    // go to Edit Profile
                    break;
                case "follow":
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    break;
                case "following":
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                    break;
            }
        });

        return view;
    }

    // retrieves user information from the Firebase database and populate the UI elements.
    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = snapshot.getValue(User.class);

                Glide.with(getContext()).load(Objects.requireNonNull(user).getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // checks if the current user is following the displayed user.
    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // retrieves the number of followers and following from the Firebase database and display them on the UI.
    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // retrieves the number of posts from the Firebase database and display them on the UI.
    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(profileid)) {
                        i++;
                    }
                }

                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
    private void myDashboard() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    assert post!= null;
                    if (post.getPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myDashboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     */

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", firebaseUser.getUid());
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", firebaseUser.getUid());
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", firebaseUser.getUid());
        editor.apply();
    }

}