package com.idris.travelmantic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    EditText txtTitle;
    EditText txtCost;
    EditText txtDescription;
    Button btnSelectImage;
    ImageView mImageView;

    FirebaseFirestore mFirebaseFirestore;
    DocumentReference mDocumentReference;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private TravelDeal mTravelDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mFirebaseFirestore =FirebaseFirestore.getInstance();


        txtTitle = findViewById(R.id.txtTitle);
        txtCost = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);
btnSelectImage = findViewById(R.id.button);
mImageView = findViewById(R.id.imageView);

btnSelectImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, REQUEST_CODE);
    }
});
        FirebaseUtil.loadReference("deals",this);
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference =FirebaseUtil.sDatabaseReference;
        readIntentStateValue();

    }

    private void readIntentStateValue() {

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("data");

        if (deal != null){
            txtTitle.setText(deal.getTitle());
            txtCost.setText(deal.getPrice() );
            txtDescription.setText(deal.getDescription());
            loadImage(deal.getImageUrl());
        }else {
            mTravelDeal = new TravelDeal();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_menu){
            saveTravelDeal();
            clean();
            Toast.makeText(this, "Save sucessful", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }



    private void saveTravelDeal() {
        mTravelDeal = new TravelDeal( txtTitle.getText().toString(), txtDescription.getText().toString(), txtCost.getText().toString() , "");
    //mDatabaseReference.setValue(mTravelDeal);
    CollectionReference collectionReference= mFirebaseFirestore.collection("deals");
    collectionReference.add(mTravelDeal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
        @Override
        public void onSuccess(DocumentReference documentReference) {
            Toast.makeText(AdminActivity.this, "Travel save successful", Toast.LENGTH_LONG).show();
        }
    });

    }

    private void clean() {
        txtDescription.getText().clear();
        txtCost.getText().clear();
        txtTitle.getText().clear();
    }

    private void openStorage(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Uri uri = data.getData();

//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//            final StorageReference imagePath = storageReference.child("deal_picture");
//            UploadTask task = imagePath.putFile(uri);

//            StorageReference reference = FirebaseUtil.sStorageReference.child(uri.getLastPathSegment());
//            reference.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                    mTravelDeal.setImageUrl(url);
//                    loadImage(url);
//
//                }
//            });

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imagePathReference = storageRef.child(uri.getLastPathSegment());
        UploadTask uploadTask = imagePathReference.putFile(uri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imagePathReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){
                    mTravelDeal.setImageUrl(task.getResult().toString());
                    loadImage(task.getResult().toString());

                }


            }
        });

    }

    private void loadImage(String deal) {
        Glide.with(this).load(deal).into(mImageView);
    }
}
