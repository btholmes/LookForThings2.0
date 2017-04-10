package me.cchiang.lookforthings;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.cchiang.lookforthings.adapter.ChatDetailsListAdapter;
import me.cchiang.lookforthings.data.Tools;
import me.cchiang.lookforthings.model.ChatsDetails;
import me.cchiang.lookforthings.model.Friend;
import me.cchiang.lookforthings.model.Player;

public class ActivityGameDetails extends AppCompatActivity {


    public static String KEY_FRIEND     = "me.cchiang.chatting.FRIEND";
    public static String KEY_SNIPPET   = "me.cchiang.chatting.SNIPPET";
    public String firebaseChild;

    private Button btn_send;
    private EditText et_content;
    public static ChatDetailsListAdapter adapter;

    private ListView listview;
    private ActionBar actionBar;
    private Friend friend;
    private List<ChatsDetails> items = new ArrayList<>();
    private View parent_view;

    private Game gameObj;



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

    FloatingActionButton logOutBtn;
    Button startBtn, pictureBtn;
    CheckBox one, three, friends;
    Random rn = new Random(System.currentTimeMillis());
    String WORDS[] = {"chair","table","person" , "bottle", "animal", "drink", "adult", "laptop", "telephone", "computer"};

    private static int count = 0;
    public static List<String> list = new ArrayList<>();
    public static StringBuilder word = new StringBuilder();



    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Friend obj, String snippet) {
        Intent intent = new Intent(activity, ActivityGameDetails.class);
        intent.putExtra(KEY_FRIEND, obj);
        intent.putExtra(KEY_SNIPPET, snippet);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.cchiang.lookforthings.R.layout.activity_game_details);
//        parent_view = findViewById(android.R.id.content);

        // animation transition
//        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);
        String snippet = intent.getStringExtra(KEY_SNIPPET);

        initToolbar();

        iniComponen();



        count = 0;
        list = new ArrayList<>();
        word = new StringBuilder();

        generateRandom();
        setChallengeListener();

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }



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
                    randomView.setText(word + list.toString());
                    System.out.println("word is: " + word);


                    one.setEnabled(false);
                    three.setEnabled(false);
                    startBtn.setEnabled(false);
                    pictureBtn.setVisibility(View.VISIBLE);
                    pictureBtn.setEnabled(true);
                }else if((one.isChecked() && three.isChecked())){
                    Toast.makeText(ActivityGameDetails.this, "Choose one only plz", Toast.LENGTH_SHORT).show();
                }else if(!one.isChecked() && !three.isChecked()){
                    Toast.makeText(ActivityGameDetails.this, "Choose one plz", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void setChallengeListener(){

        // change to picture Layout
        pictureBtn = (Button) findViewById(R.id.pictureBtn);
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Create Game object here to be saved in Firebase
//                    public Game(Long gameID, String challengerUID, String opponentUID )

                String challengerUID = mFirebaseUser.getUid();
                String opponentUID = friend.getUid();
                gameObj = new Game(challengerUID, opponentUID, list);

                addGameToFirebase(gameObj);
                setPlayerInfo();


//                showNotification("You Have a New Game Request");
                Intent intent = new Intent(ActivityGameDetails.this, cameraActivity.class);
                Toast.makeText(getApplicationContext(), "Challenged Sent!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    private void addGameToFirebase(Game gameObj){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mFirebaseDatabaseReference.child("Games").child(user.getUid()).child("currentGameFolder").push().setValue(url);
        mFirebaseDatabaseReference.child("Games").child(gameObj.getGameID()).setValue(gameObj);
        mFirebaseDatabaseReference.child("userList").child(gameObj.getChallenger()).child("games").push().setValue(gameObj.getGameID().toString());
        mFirebaseDatabaseReference.child("userList").child(gameObj.getOpponent()).child("games").push().setValue(gameObj.getGameID().toString());

    }

    private void setPlayerInfo(){

        String opponentUID = friend.getUid();
        Player opponent = new Player(opponentUID, list);
        opponent.setAcceptedChallenge(false);
        opponent.setChallenger(false);

        String challengerUID = mFirebaseUser.getUid();
        Player challenger = new Player(challengerUID, list);
        challenger.setAcceptedChallenge(true);
        challenger.setChallenger(true);
//        gameObj = new Game(challengerUID, opponentUID);

        mFirebaseDatabaseReference.child("PlayerInfo").child(gameObj.getGameID()).child(challengerUID).setValue(challenger);
        mFirebaseDatabaseReference.child("PlayerInfo").child(gameObj.getGameID()).child(opponentUID).setValue(opponent);

    }


    private void showNotification(String msg){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.simplifiedcoding.net"));
//        Intent intent = new Intent(Intent.ACTION_VIEW, );
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Look For Things");
        builder.setContentText(msg);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(me.cchiang.lookforthings.R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(friend.getName());
    }


    public void iniComponen() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


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
