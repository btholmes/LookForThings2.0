package me.cchiang.lookforthings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.cchiang.lookforthings.adapter.ChatDetailsListAdapter;
import me.cchiang.lookforthings.data.Tools;
import me.cchiang.lookforthings.model.ChatsDetails;

public class ActivitySelectedGame extends AppCompatActivity {


    public static String KEY_GAME = "Game";
    public String firebaseChild;

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Game obj) {
        Intent intent = new Intent(activity, ActivitySelectedGame.class);
        intent.putExtra(KEY_GAME, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_GAME);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    private Button btn_send;
    private EditText et_content;
    public static ChatDetailsListAdapter adapter;

    private ListView listview;
    private ActionBar actionBar;
    private Game game;
    private List<ChatsDetails> items = new ArrayList<>();
    private View parent_view;



    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private static final int READ_CONTACTS = 1000;
    private static final int WRITE_EXTERNAL_STORAGE = 1001;
    private static final int READ_EXTERNAL_STORAGE = 1002;
    public static boolean CAN_READ_CONTACTS = false;
    public static boolean CAN_WRITE_EXTERNAL_STORAGE = false;
    public static boolean CAN_READ_EXTERNAL_STORAGE = false;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 140;
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseListAdapter<Message> mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private TextView opponentName;
    private TextView opponentWords;
    private TextView myWords;
    private ImageView myLastPicture;
    private Button startBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.cchiang.lookforthings.R.layout.activity_selected_game);

        Intent intent = getIntent();
        game = (Game) intent.getExtras().getSerializable(KEY_GAME);

        initToolbar();
        initComponent();
//        setLastPictureTaken();
        setOpponentInfo();
        setMyInfo();

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }



    public void setLastPictureTaken(){

//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final Query ref = mFirebaseDatabaseReference.child("userList").child(mFirebaseUser.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                if(dataSnapshot.getKey().equals("lastPictureTaken")){
                    Picasso.with(getApplicationContext()).load(dataSnapshot.getValue().toString()).into(myLastPicture);

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

    private void setOpponentWords(){
//        opponentWords = (TextView) findViewById(R.id.opponentWords);


        final Query ref = mFirebaseDatabaseReference.child("PlayerInfo").child(game.getGameID())
                .child(game.getOpponent()).child("words");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                String word = dataSnapshot.getValue().toString();
                opponentWords.append(word + " ");

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

    private void setOpponentName(){
        //        opponentName = (TextView) findViewById(R.id.opponentName);

        final Query ref = mFirebaseDatabaseReference.child("userList").child(game.getOpponent());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                if(dataSnapshot.getKey().equals("displayName")){
                    String name = dataSnapshot.getValue().toString();
                    opponentName.setText(name);
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


    private void setOpponentInfo(){
        setOpponentWords();
        setOpponentName();

    }

    private void setMyInfo(){
//        myWords = (TextView) findViewById(R.id.myWords);

        final Query ref = mFirebaseDatabaseReference.child("PlayerInfo").child(game.getGameID())
                .child(mFirebaseUser.getUid()).child("words");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                String word = dataSnapshot.getValue().toString();
                myWords.append(word + " ");

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

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(me.cchiang.lookforthings.R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(game.getOpponent());
    }


    public void initComponent() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        opponentName = (TextView) findViewById(R.id.opponentName);
        opponentWords = (TextView) findViewById(R.id.opponentWordsLeft);
        myWords = (TextView) findViewById(R.id.myWordsLeft);
        myLastPicture = (ImageView) findViewById(R.id.myLastPicutre);
        startBtn = (Button) findViewById(R.id.startBtn);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraActivity.navigate(ActivitySelectedGame.this, game);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(me.cchiang.lookforthings.R.menu.menu_chat_details, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Handle click on action bar
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case me.cchiang.lookforthings.R.id.action_sample:
//                Snackbar.make(parent_view, item.getTitle() + " Clicked ", Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
