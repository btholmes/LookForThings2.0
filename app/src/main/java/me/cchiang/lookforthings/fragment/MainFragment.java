package me.cchiang.lookforthings.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import me.cchiang.lookforthings.LoginActivity;
import me.cchiang.lookforthings.R;
import me.cchiang.lookforthings.UserProfileActivity;
import me.cchiang.lookforthings.dataCollectionActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainFragment extends Fragment {

    public RecyclerView recyclerView;
    View view;


    //    FIREBASE VARIABLES
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    //get current user
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mFirebaseDatabaseReference;
////    END FIREBASE VARIABLES

    private Button gameBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_main_look, container, false);
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    finish();
                }else {
                    setLastPictureTaken();

                }
            }
        };

        auth.addAuthStateListener(authListener);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if(user != null){
            getDisplayName();
        }

        Button dataButton = (Button) view.findViewById(R.id.dataButton);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), dataCollectionActivity.class);
                startActivity(intent);
            }

        });

//        auth.addAuthStateListener(authListener);

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.logOutBtn);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                auth.signOut();
                if(LoginManager.getInstance() != null){
                    LoginManager.getInstance().logOut();
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        ImageView userProfileImageButton = (ImageView) view.findViewById(R.id.userImage);
        userProfileImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

        checkIfUserIsInDB();

        return view;
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        auth.addAuthStateListener(authListener);
//
//    }


    //    FIREBASE METHODS

    @Override
    public void onResume() {
        super.onResume();
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);

        }
    }

    private void getDisplayName() {

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final Query ref = mFirebaseDatabaseReference.child("userList").child(user.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //  dataSnapshot.getValue(User.class);
                if(dataSnapshot.getKey().equals("displayName")){
                    String userDisplayName = dataSnapshot.getValue(String.class);
                    TextView displayName = (TextView) view.findViewById(R.id.displayName);
                    if(userDisplayName != null){
                        displayName.setText(userDisplayName);

                    }else{
                        displayName.setText("Please set Display Name");
                    }

                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError error) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
        });
    }
//
//
    public void setLastPictureTaken(){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final Query ref = mFirebaseDatabaseReference.child("userList").child(user.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                if(dataSnapshot.getKey().equals("lastPictureTaken")){
                    ImageView lastPictureTaken = (ImageView) view.findViewById(R.id.lastPictureTaken);
                    Picasso.with(getApplicationContext()).load(dataSnapshot.getValue().toString()).into(lastPictureTaken);

                }

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError error) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
        });


    }

    // Checks if user is in the database
    private void checkIfUserIsInDB() {
        FirebaseUser _user = auth.getCurrentUser();
        if(null != _user){
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }


}
