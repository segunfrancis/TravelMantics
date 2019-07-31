package com.android.computer.travelmantics;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 234;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseStorage firebaseStorage;
    public static FirebaseAuth.AuthStateListener authStateListener;
    private static MainActivity activity;

    public FirebaseUtil() {
    }

    public static void openFirebaseRef(String ref, MainActivity mainActivity) {

    }
}
