package com.example.wardrobe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wardrobe.Adapter.GalleryAdapter;
import com.example.wardrobe.Model.Gallery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EveningActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private DatabaseReference root;
    private StorageReference reference;
    private Uri imageUri;
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ArrayList<Gallery> galleryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evening);

        root = FirebaseDatabase.getInstance().getReference("Evening Wear");
        reference = FirebaseStorage.getInstance().getReference();

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recyclerView);

        progressBar.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        galleryList = new ArrayList<>();
        adapter = new GalleryAdapter(this, galleryList);
        recyclerView.setAdapter(adapter);

        // Fetch and display user's images from Firebase
        fetchUserImages();
    }

    private void fetchUserImages() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            root.orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    galleryList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Gallery gallery = dataSnapshot.getValue(Gallery.class);
                        galleryList.add(gallery);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EveningActivity.this, "Failed to fetch images.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            // Automatically upload the selected image
            if (imageUri != null) {
                uploadToFirebase(imageUri);
            } else {
                Toast.makeText(EveningActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadToFirebase(Uri uri) {
        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            Gallery gallery = new Gallery(uri1.toString(), currentUser.getUid());
                            String modelId = root.push().getKey();
                            if (modelId != null) {
                                root.child(modelId).setValue(gallery)
                                        .addOnSuccessListener(aVoid -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(EveningActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            imageView.setImageResource(R.drawable.add);
                                        })
                                        .addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(EveningActivity.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    });
                })
                .addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EveningActivity.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
