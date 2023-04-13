package com.example.wardrobe.Adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wardrobe.Fragment.PostDetailFragment;
import com.example.wardrobe.Fragment.ProfileFragment;
import com.example.wardrobe.Model.Post;
import com.example.wardrobe.R;

import java.util.List;

public class MyDashboardAdapter extends RecyclerView.Adapter<MyDashboardAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> mPosts;

    public MyDashboardAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Post post = mPosts.get(i);

        Glide.with(context).load(post.getPostimage()).into(viewHolder.post_image);

        viewHolder.post_image.setOnClickListener(view -> {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("postid", post.getPostid());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PostDetailFragment()).commit();
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
