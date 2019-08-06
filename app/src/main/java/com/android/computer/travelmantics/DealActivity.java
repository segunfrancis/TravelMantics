package com.android.computer.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private static final int PICTURE_RESULT = 42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    TravelDeal deal;
    ImageView imageView;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        imageView = findViewById(R.id.image);
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());

        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(view -> {
            Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
            imageIntent.setType("image/*").putExtra(imageIntent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(imageIntent.createChooser(imageIntent, "Insert Picture"), PICTURE_RESULT);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu: {
                saveDeal();
                clean();
                backToList();
                return true;
            }
            case R.id.delete_menu: {
                deleteDeal();
                backToList();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
            findViewById(R.id.btnImage).setEnabled(true);
        } else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
            findViewById(R.id.btnImage).setEnabled(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            if (imageUri != null) {
                final StorageReference fileReference = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());

                uploadTask = fileReference.putFile(imageUri);
                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        String pictureName = task.getResult().getLastPathSegment();

                        deal.setImageUrl(mUri);
                        deal.setImageName(pictureName);
                        Log.d("Uri", mUri);
                        Log.d("Name", pictureName);
                        showImage(mUri);
                    } else {
                        Toast.makeText(DealActivity.this, "FAILED!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e ->
                        Toast.makeText(DealActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveDeal() {
        if (txtTitle.getText().toString().isEmpty() || txtPrice.getText().toString().isEmpty()
                || txtDescription.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            deal.setTitle(txtTitle.getText().toString().trim());
            deal.setDescription(txtDescription.getText().toString().trim());
            deal.setPrice(txtPrice.getText().toString().trim());
            if (deal.getId() == null) {
                // Creating a new entry
                mDatabaseReference.push().setValue(deal).addOnSuccessListener(aVoid ->
                        Toast.makeText(DealActivity.this, "Deal Saved!", Toast.LENGTH_SHORT).show());
            } else {
                // Updating an existing entry
                mDatabaseReference.child(deal.getId()).setValue(deal);
            }
        }
    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
        if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {
            FirebaseUtil.connectStorage();
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("Delete Image", "Image Successfully deleted");
                Toast.makeText(DealActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Log.d("Delete image", e.getMessage()));
        }
    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void backToList() {
        startActivity(new Intent(this, ListActivity.class));
        finish();
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            // Getting the width of the device
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .error(R.drawable.travel_icon)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
