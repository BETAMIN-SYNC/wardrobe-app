package com.example.wardrobe.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wardrobe.MainActivity;
import com.example.wardrobe.Model.Comment;
import com.example.wardrobe.Model.User;
import com.example.wardrobe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Comment> mComment;
    private final String postid;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment, String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(i);

        viewHolder.comment.setText(comment.getComment());
        getUserInfo(viewHolder.image_profile, viewHolder.username, comment.getPublisher());

        viewHolder.comment.setOnClickListener(view -> {

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        });

        viewHolder.image_profile.setOnClickListener(view -> {

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            if (comment.getPublisher().equals(firebaseUser.getUid())){

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Do you want to delete?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                        (dialogInterface, i1) -> dialogInterface.dismiss());

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        (dialogInterface, i1) -> {
                            FirebaseDatabase.getInstance().getReference("Comments")
                                    .child(postid).child(comment.getCommentid())
                                    .removeValue().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dialogInterface.dismiss();
                        });
                alertDialog.show();
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
