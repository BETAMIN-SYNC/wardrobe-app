package com.example.wardrobe;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobe.Model.Gallery;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FormalActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formal);

        Button uploadBtn = findViewById(R.id.uploadBtn);
        Button showAllBtn = findViewById(R.id.showAllBtn);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        progressBar.setVisibility(View.INVISIBLE);

        showAllBtn.setOnClickListener(v -> startActivity( new Intent(FormalActivity.this , ShowFormalActivity.class)));


        imageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent , 2);
        });

        uploadBtn.setOnClickListener(v -> {
            if (imageUri != null){
                uploadToFirebase(imageUri);
            }else{
                Toast.makeText(FormalActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }
    }

    private void uploadToFirebase(Uri uri){

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {

            Gallery gallery = new Gallery(uri1.toString());
            String modelId = root.push().getKey();
            assert modelId != null;
            root.child(modelId).setValue(gallery);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(FormalActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            imageView.setImageResource(R.drawable.add);
        })).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(FormalActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
        });
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }
}
