package me.cchiang.lookforthings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

//FIREBASE & Facebook IMPORTS


public class MainActivity extends AppCompatActivity {

    //    FIREBASE VARIABLES
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    //get current user
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mFirebaseDatabaseReference;
////    END FIREBASE VARIABLES

    private Button gameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_look);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }else {
                    setLastPictureTaken();

                }
            }
        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }


//        END FIREBASE STUFF
        checkIfUserIsInDB();

    }


//    FIREBASE METHODS

    @Override
    protected void onResume() {
        super.onResume();
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);

        }
    }

    private void getDisplayName() {

        final Query ref = mFirebaseDatabaseReference.child("userList").child(user.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //  dataSnapshot.getValue(User.class);
                if(dataSnapshot.getKey().equals("displayName")){
                    String userDisplayName = dataSnapshot.getValue(String.class);
                    TextView displayName = (TextView)findViewById(R.id.displayName);
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


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        gameBtn = (Button) findViewById(R.id.gameBtn);

        if(user != null){
            getDisplayName();
        }

        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameMainActivity.class);
                startActivity(intent);
            }
        });

        Button chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainChatActivity.class);
                startActivity(intent);
            }

        });

        Button dataButton = (Button) findViewById(R.id.dataButton);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, dataCollectionActivity.class);
                startActivity(intent);
            }

        });

        Button galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference images = mFirebaseDatabaseReference.child("Images");
                images.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(user.getUid())) {
                            // run some code
                            Intent intent = new Intent(MainActivity.this, galleryActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "You Don't Have Any Images", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }

        });

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.logOutBtn);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                signOut();
                auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ImageView userProfileImageButton = (ImageView) findViewById(R.id.userImage);
        userProfileImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });


    }


    public void setLastPictureTaken(){

        final Query ref = mFirebaseDatabaseReference.child("userList").child(user.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                if(dataSnapshot.getKey().equals("lastPictureTaken")){
                    ImageView lastPictureTaken = (ImageView) findViewById(R.id.lastPictureTaken);
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

//    FIREBASE METHODS

}
