package me.cchiang.lookforthings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;


public class FriendListActivity extends AppCompatActivity {

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public Button deleteFriendButton;
        public Button sendPMButton;
        //public CircleImageView friendImageView;
        public FriendViewHolder(View v) {
            super(v);
            friendName = (TextView) itemView.findViewById(R.id.friendDisplayNameTextView);

            deleteFriendButton = (Button) itemView.findViewById(R.id.deleteFriendButton);
            deleteFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String displayName = friendName.getText().toString();
                    String uidToDelete;
                    for (User user: userList) {
                        if(user.getDisplayName().equals(displayName)) {
                            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            uidToDelete = user.getUid();
                            DatabaseReference mFirebaseDatabaseReference;
                            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();;
                            mFirebaseDatabaseReference.child("userFriendList").child(currentUserUid).child(uidToDelete).removeValue();
                            break;
                        }
                    }
                }
            });

            sendPMButton = (Button) v.findViewById(R.id.sendPMButton);
            sendPMButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String displayName = friendName.getText().toString();
                    String uidToSend = "";
                    for (User user: userList) {
                        if(user.getDisplayName().equals(displayName)) {
                            uidToSend = user.getUid();
                            break;
                        }
                    }
                    Intent intent = new Intent(v.getContext(), SendPMActivity.class);
                    intent.putExtra("uid", uidToSend);
                    intent.putExtra("displayName", displayName);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public static class MyRecyclerAdapter extends RecyclerView.Adapter<FriendListActivity.FriendViewHolder> {

        private List<User> listItems, filterList;
        private Context mContext;

        public MyRecyclerAdapter(Context context, List<User> listItems) {
            this.listItems = listItems;
            this.mContext = context;
            this.filterList = new ArrayList<User>();
            // we copy the original list to the filter list and use it for setting row values
            this.filterList.addAll(this.listItems);
        }

        @Override
        public FriendListActivity.FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, null);
            FriendListActivity.FriendViewHolder viewHolder = new FriendListActivity.FriendViewHolder(view);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(FriendListActivity.FriendViewHolder customViewHolder, int position) {

            User listItem = filterList.get(position);
            customViewHolder.friendName.setText(listItem.getDisplayName());
        }

        @Override
        public int getItemCount() {
            return (null != filterList ? filterList.size() : 0);
        }

    }

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<User, FriendViewHolder> mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private RecyclerView mMessageRecyclerView;
    private RecyclerView mRecyclerView;
    private static MyRecyclerAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private FirebaseUser currentUser;
    private static ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        mProgressBar = (ProgressBar) findViewById(R.id.friendProgressBar);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) findViewById(R.id.friendRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        populateFriends();

    }

    private void populateFriends() {

        final Query ref = mFirebaseDatabaseReference.child("userFriendList").child(currentUser.getUid());
            ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Query ref2 = mFirebaseDatabaseReference.child("userList").child(dataSnapshot.getKey());
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        userList.add(snapshot.getValue(User.class));
                        adapter = new MyRecyclerAdapter(getApplicationContext(), userList);
                        mRecyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

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

    @Override
    public void onStop() {
        super.onStop();
        userList.clear();
    }

}
