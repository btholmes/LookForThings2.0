package me.cchiang.lookforthings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.cchiang.lookforthings.adapter.ChatDetailsListAdapter;
import me.cchiang.lookforthings.data.Tools;
import me.cchiang.lookforthings.model.ChatsDetails;
import me.cchiang.lookforthings.model.Friend;

import static me.cchiang.lookforthings.GlobalChatActivity.ANONYMOUS;

public class ActivityChatDetails extends AppCompatActivity {


    public static String KEY_FRIEND     = "me.cchiang.chatting.FRIEND";
    public static String KEY_SNIPPET   = "me.cchiang.chatting.SNIPPET";
    public String firebaseChild;

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Friend obj, String snippet, String uid, String displayName) {
        Intent intent = new Intent(activity, ActivityChatDetails.class);
        intent.putExtra(KEY_FRIEND, obj);
        intent.putExtra(KEY_SNIPPET, snippet);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    private Button btn_send;
    private EditText et_content;
    public static ChatDetailsListAdapter adapter;

    private ListView listview;
    private ActionBar actionBar;
    private Friend friend;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.cchiang.lookforthings.R.layout.activity_chat_details);
        parent_view = findViewById(android.R.id.content);

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);
        String snippet = intent.getStringExtra(KEY_SNIPPET);
        initToolbar();

        iniComponen();



        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String[] uids = { mFirebaseUser.getUid(), friend.getUid()};
        Arrays.sort(uids);

        firebaseChild = uids[0] + "_" + uids[1];


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;


        // New child entries
        mFirebaseAdapter = new FirebaseListAdapter<Message>(
                this,
                Message.class,
                R.layout.row_chat_details,
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
            protected void populateView(View viewHolder,
                                              Message message, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                TextView content = (TextView) viewHolder.findViewById(R.id.text_content);
                content.setText(message.getText());
                TextView time = (TextView) viewHolder.findViewById(R.id.text_time);
                time.setText(message.getTime());


                ImageView image_status = (ImageView) viewHolder.findViewById(R.id.image_status);


                if (message.getPhotoUrl() == null) {
                    image_status
                            .setImageDrawable(ContextCompat
                                    .getDrawable(ActivityChatDetails.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(ActivityChatDetails.this)
                            .load(message.getPhotoUrl())
                            .into(image_status);
                }
                // log a view action on it
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Message msg = (Message) getItem(position);
                ChatDetailsListAdapter.ViewHolder holder;
                if(convertView == null){
                    holder 				= new ChatDetailsListAdapter.ViewHolder();
                    convertView			= LayoutInflater.from(ActivityChatDetails.this).inflate(R.layout.row_chat_details, parent, false);
                    holder.time 		= (TextView) convertView.findViewById(R.id.text_time);
                    holder.message 		= (TextView) convertView.findViewById(R.id.text_content);
                    holder.lyt_thread 	= (CardView) convertView.findViewById(R.id.lyt_thread);
                    holder.lyt_parent 	= (LinearLayout) convertView.findViewById(R.id.lyt_parent);
                    holder.image_status	= (ImageView) convertView.findViewById(R.id.image_status);
                    convertView.setTag(holder);
                }else{
                    holder = (ChatDetailsListAdapter.ViewHolder) convertView.getTag();
                }

                holder.message.setText(msg.getText());
                holder.time.setText(msg.getTime());

                if(msg.getSentFrom() != null){
                    if(msg.getSentFrom().equals(mFirebaseUser.getUid())){
                        msg.setFromMe(true);
                    }else{
                        msg.setFromMe(false);
                    }
                }

                if(msg.isFromMe()){
                    holder.lyt_parent.setPadding(100, 10, 15, 10);
                    holder.lyt_parent.setGravity(Gravity.RIGHT);
                    holder.lyt_thread.setCardBackgroundColor(ActivityChatDetails.this.getResources().getColor(R.color.me_chat_bg));
                }else{
                    holder.lyt_parent.setPadding(15, 10, 100, 10);
                    holder.lyt_parent.setGravity(Gravity.LEFT);
                    holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    //holder.image_status.setImageResource(android.R.color.transparent);
                }
                return convertView;
            }


        };

        listview.setAdapter(mFirebaseAdapter);
        listview.requestFocus();
        registerForContextMenu(listview);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(me.cchiang.lookforthings.R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(friend.getName());
    }

//    public void bindView() {
//        try {
//            adapter.notifyDataSetChanged();
//            listview.setSelectionFromTop(adapter.getCount(), 0);
//        } catch (Exception e) {
//
//        }
//    }

    public void iniComponen() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }else {
            mPhotoUrl = "Nothing";
        }

        listview = (ListView) findViewById(me.cchiang.lookforthings.R.id.listview);
        btn_send = (Button) findViewById(me.cchiang.lookforthings.R.id.btn_send);
        et_content = (EditText) findViewById(me.cchiang.lookforthings.R.id.text_content);
        btn_send.setOnClickListener(new OnClickListener() {
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

                Message message = new Message(et_content.getText().toString(),
                        mFirebaseUser.getDisplayName(),
                        mPhotoUrl, date);
                message.setSentFrom(mFirebaseUser.getUid());
                mFirebaseDatabaseReference.child("pm_Messages").child(firebaseChild)
                        .push().setValue(message);
                et_content.setText("");
            }
        });
        et_content.addTextChangedListener(contentWatcher);
        if (et_content.length() == 0) {
            btn_send.setEnabled(false);
        }
        hideKeyboard();
    }


    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setEnabled(false);
            } else {
                btn_send.setEnabled(true);
            }
            //draft.setContent(etd.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

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
