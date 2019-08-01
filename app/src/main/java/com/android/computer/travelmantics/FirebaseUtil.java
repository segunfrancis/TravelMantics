package com.android.computer.travelmantics;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 234;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageRef;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> deals;
    private static MainActivity activity;

    public FirebaseUtil() {
    }

    // Configuring offline persistence
    public static FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    public static void openFirebaseRef(String ref, MainActivity mainActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            activity = mainActivity;

            authStateListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() == null) {
                    FirebaseUtil.signIn();
                } else {
                    String userId = firebaseAuth.getUid();
                }
                Toast.makeText(mainActivity.getBaseContext(), "Welcome Back!", Toast.LENGTH_SHORT).show();
            };
            connectStorage();
        }
        deals = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void signOut() {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUtil.attachListener();
                    }
                });
    }

    public static void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        /*.setLogo(R.drawable.my_great_logo)      // Set logo drawable
                        .setTheme(R.style.MySuperAppTheme) */     // Set theme
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }

    public static boolean isAdmin() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            return mFirebaseAuth.getCurrentUser().getDisplayName().equals("Segun Francis");
        }
        return false;
    }

    public static void connectStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference().child("pictures");
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(authStateListener);
    }
}
