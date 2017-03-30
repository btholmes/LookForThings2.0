package me.cchiang.lookforthings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static me.cchiang.lookforthings.GlobalChatActivity.ANONYMOUS;

public class SendPMActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public com.google.android.gms.appindexing.Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new com.google.android.gms.appindexing.Action.Builder(com.google.android.gms.appindexing.Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(com.google.android.gms.appindexing.Action.STATUS_TYPE_COMPLETED)
                .build();
    }
    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public TextView timeTextView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
        }
    }

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
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pm);

        Bundle extras = getIntent().getExtras();
        String uidToMessage = extras.getString("uid");
        String displayNameToMessage = extras.getString("displayName");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        String[] uids = {uidToMessage, mFirebaseUser.getUid()};
        Arrays.sort(uids);

        final String firebaseChild = uids[0] + "_" + uids[1];


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .addApi(AppIndex.API).build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child("pm_Messages").child(firebaseChild)) {

            @Override
            protected Message parseSnapshot(DataSnapshot snapshot) {
                Message message = super.parseSnapshot(snapshot);
                if (message != null) {
                    message.setId(snapshot.getKey());
                }
                return message;
            }

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              Message message, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(message.getText());
                viewHolder.messengerTextView.setText(message.getName());
                viewHolder.timeTextView.setText(message.getTime());
                if (message.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(SendPMActivity.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(SendPMActivity.this)
                            .load(message.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
                // log a view action on it
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                String AMPM = "AM";
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                int hour = c.get(Calendar.HOUR_OF_DAY);
                if(hour > 12){
                    AMPM = "PM";
                }
                hour = hour % 12;
                if(hour == 0){
                    hour = 12;
                }
                int minute = c.get(Calendar.MINUTE);

                String date = hour + ":" + minute + " " + AMPM +  "   " + day + "-" + month + "-" + year;

                Message message = new Message(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl, date);
                mFirebaseDatabaseReference.child("pm_Messages").child(firebaseChild)
                        .push().setValue(message);
                mMessageEditText.setText("");
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS) {
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, PictureActivity.CAMERA_REQUEST);
                    CAN_READ_CONTACTS = true;
                }
            }
        }
        if(requestCode == WRITE_EXTERNAL_STORAGE){
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_WRITE_EXTERNAL_STORAGE = true;
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, PictureActivity.CAMERA_REQUEST);
                }
            }
        }
        if (requestCode == READ_EXTERNAL_STORAGE){
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_EXTERNAL_STORAGE = true;
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, PictureActivity.CAMERA_REQUEST);
                }
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        // Need to add code to check if user is signed in
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.settings:
                return true;
            case R.id.silence:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
