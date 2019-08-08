package com.idris.travelmantic;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static final int RC_SIGN_IN = 123;
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static ArrayList<TravelDeal> sTravelDeals;
    private static FirebaseUtil firebaseUtil;

    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseStorage sFirebaseStorage;
    public static StorageReference sStorageReference;

    public static  FirebaseAuth.AuthStateListener sAuthStateListener;

    private static Activity caller;

    public FirebaseUtil() { }
    public static void loadReference(String path, Activity caller1){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            sFirebaseDatabase =FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sTravelDeals = new ArrayList<>();
            caller = caller1;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                    FirebaseUtil.signin();                     }
                    Toast.makeText(caller.getBaseContext(), "welcome", Toast.LENGTH_LONG).show();
                }
            };
        }
        sDatabaseReference = sFirebaseDatabase.getReference().child(path);
            connectStorage();
    }

    private static void signin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                );

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                 RC_SIGN_IN);
    }

    public static void attachListener(){
    sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }
    public static void detachListener(){
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

public static void  connectStorage(){
    sFirebaseStorage = FirebaseStorage.getInstance();
sStorageReference = sFirebaseStorage.getReference().child("deals_picture");
}

}
