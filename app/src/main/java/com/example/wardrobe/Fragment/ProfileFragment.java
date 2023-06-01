package com.example.wardrobe.Fragment;

import android.content.Context;
import android.content.Intent;
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
import com.example.wardrobe.Adapter.MyDashboardAdapter;
import com.example.wardrobe.EditProfileActivity;
import com.example.wardrobe.FollowersActivity;
import com.example.wardrobe.Model.Post;
import com.example.wardrobe.Model.User;
import com.example.wardrobe.SettingsActivity;
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
import java.util.HashMap;
import java.util.List;
public class ProfileFragment extends Fragment {

    ImageView image_profile, settings;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyDashboardAdapter myDashboardAdapter_saves;
    List<Post> postList_saves;

    RecyclerView recyclerView;
    MyDashboardAdapter myDashboardAdapter;
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

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2 );
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myDashboardAdapter = new MyDashboardAdapter(getContext(), postList);
        recyclerView.setAdapter(myDashboardAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 2 );
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myDashboardAdapter_saves = new MyDashboardAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myDashboardAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myDashboard();
        mysaves();

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
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                    break;
                case "Follow":
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications();
                    break;
                case "Following":
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                    break;
            }
        });

        settings.setOnClickListener(view12 -> startActivity(new Intent(getContext(), SettingsActivity.class)));


        dashboard.setOnClickListener(view1 -> {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView_saves.setVisibility(View.GONE);
        });

        bookmark.setOnClickListener(v -> {
            recyclerView.setVisibility(View.GONE);
            recyclerView_saves.setVisibility(View.VISIBLE);
        });

        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", profileid);
            intent.putExtra("title","Followers");
            startActivity(intent);
        });

        following.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", profileid);
            intent.putExtra("title","Following");
            startActivity(intent);
        });

        return view;
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
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

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
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
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    edit_profile.setText("Following");
                } else {
                    edit_profile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // retrieves the number of followers and following from the Firebase database and display them on the UI.
    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("Following");
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

    private void myDashboard() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    assert post != null;
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

    private void mysaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mySaves.add(dataSnapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_saves.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        assert post != null;
                        if (post.getPostid().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }
                myDashboardAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


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