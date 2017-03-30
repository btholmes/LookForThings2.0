package me.cchiang.lookforthings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Random;

//FIREBASE IMPORTS and FACEBOOK IMports
//FIREBASE IMPORTS END
//GOOGLE


public class gameRoomActivity extends AppCompatActivity {

    // VARIABLES
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    //get current user
//    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference mFirebaseDatabaseReference;
    Button searchBtn, chooseBtn;
    EditText searchView;
    TextView userView;
    User searchedUser;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    final String FRIEND_LIST = "userFriendList";


    //    GOOGLE
    private GoogleApiClient mGoogleApiClient;

    private static final int READ_CONTACTS = 1000;
    private static final int WRITE_EXTERNAL_STORAGE = 1001;
    private static final int READ_EXTERNAL_STORAGE = 1002;

    public static boolean CAN_READ_CONTACTS = false;
    public static boolean CAN_WRITE_EXTERNAL_STORAGE = false;
    public static boolean CAN_READ_EXTERNAL_STORAGE = false;

    private static int count = 0;
    public static ArrayList<String> list = new ArrayList<>();
    public static StringBuilder word = new StringBuilder();




    FloatingActionButton logOutBtn;
    Button startBtn, pictureBtn;
    CheckBox one, three, friends;
    Random rn = new Random(System.currentTimeMillis());
    String WORDS[] = {"chair","table","person" , "bottle", "animal", "drink", "adult", "laptop", "telephone", "computer"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);


        // Configure Google Sign In Need these credentials to signout

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [END config_signin]




        //get firebase auth instance

        auth = FirebaseAuth.getInstance();



        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(gameRoomActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

//        END FIREBASE STUFF



        count = 0;
        list = new ArrayList<>();
        word = new StringBuilder();

        generateRandom();


        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.logOutBtn);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });

        // change to picture Layout
        pictureBtn = (Button) findViewById(R.id.pictureBtn);
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Create Game object here to be saved in Firebase
//                    public Game(Long gameID, String challengerUID, String opponentUID )

                String challengerUID = mFirebaseUser.getUid();
                String opponentUID = searchedUser.getUid();
                Game gameObj = new Game(challengerUID, opponentUID);

                addGameToFirebase(gameObj);


                Intent intent = new Intent(gameRoomActivity.this, GameMainActivity.class);
                Toast.makeText(getApplicationContext(), "Challenged Sent!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });


        /*
         * User Searching
         */
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        searchView = (EditText) findViewById(R.id.searchView);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        chooseBtn = (Button) findViewById(R.id.chooseBtn);
        userView = (TextView) findViewById(R.id.userView);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClick(searchView.getText().toString());
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(searchedUser);
            }
        });

    }

    public void addGameToFirebase(Game gameObj){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mFirebaseDatabaseReference.child("Games").child(user.getUid()).child("currentGameFolder").push().setValue(url);
        mFirebaseDatabaseReference.child("Games").child(gameObj.getGameID()).setValue(gameObj);
        mFirebaseDatabaseReference.child("userList").child(gameObj.getChallenger()).child("games").push().setValue(gameObj.getGameID().toString());
        mFirebaseDatabaseReference.child("userList").child(gameObj.getOpponent()).child("games").push().setValue(gameObj.getGameID().toString());

    }


    /*
     * User Searching
     */
    private void searchClick(String displayName) {
        Query ref = mFirebaseDatabaseReference.child("userList").orderByChild("displayName").equalTo(displayName);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                searchedUser = dataSnapshot.getValue(User.class);

                if(null != searchedUser) {
//                    String email = searchedUser.getEmail();
                    String displayName = searchedUser.getDisplayName();
                    userView.setText(displayName);
                }

                else {
                    userView.setText("No Users Found");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }
        });
    }

    /*
     * Adds friend to your Friendlist
     */
    private void addFriend(User userToAdd) {

        if (null != userToAdd) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mFirebaseDatabaseReference.child(FRIEND_LIST).child(mFirebaseUser.getUid()).child(userToAdd.getUid()).setValue(userToAdd.getUid());
        }

        else {
            Toast.makeText(getApplicationContext(), "No User Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {

//        NEED TO FIX THIS CHECK HERE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, WRITE_EXTERNAL_STORAGE);

        }else {
            CAN_READ_EXTERNAL_STORAGE = true;
            CAN_WRITE_EXTERNAL_STORAGE = true;
            CAN_READ_CONTACTS = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS) {
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_CONTACTS = true;
                }
            }
        }
        if(requestCode == WRITE_EXTERNAL_STORAGE){
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_WRITE_EXTERNAL_STORAGE = true;
                }
            }
        }
        if (requestCode == READ_EXTERNAL_STORAGE){
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_EXTERNAL_STORAGE = true;
                }
            }
        }
    }

    /*
     * Generate the random words from the provided array 
     */
    public void generateRandom(){

        startBtn = (Button) findViewById(R.id.startBtn);
        one =(CheckBox)findViewById(R.id.box1);
        three =(CheckBox)findViewById(R.id.box3);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((one.isChecked() && !(three.isChecked())) || (!(one.isChecked()) && three.isChecked())){
                    list = new ArrayList<>();
                    word.append("Find: ");

                    if (one.isChecked()) {
                        count = 1;
                    } else if (three.isChecked()) {
                        count = 3;
                    }


                    // Just Changing the mock up

                    list.add("chair");
                    list.add("table");
                    list.add("computer");

//                    for (int i = 0; i < count; ) {
//                        int x = rn.nextInt(WORDS.length);
//                        if (!list.contains(WORDS[x])) {
//                            word.append(WORDS[x]);
//                            if (i != count - 1) {
//                                word.append(", ");
//                            }
//                            list.add(WORDS[x]);
//                            i++;
//                        }
//                    }

                    TextView randomView = (TextView) findViewById(R.id.randomView);
                    randomView.setText(word + "!");
                    System.out.println("word is: " + word);


                    one.setEnabled(false);
                    three.setEnabled(false);
                    startBtn.setEnabled(false);
                    pictureBtn.setEnabled(true);
                }else if((one.isChecked() && three.isChecked())){
                    Toast.makeText(gameRoomActivity.this, "Choose one only plz", Toast.LENGTH_SHORT).show();
                }else if(!one.isChecked() && !three.isChecked()){
                    Toast.makeText(gameRoomActivity.this, "Choose one plz", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void signOut() {

//    LoginActivity.
        auth.signOut();
//        LoginManager is for facebook
        LoginManager.getInstance().logOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
//                        updateUI(null);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
