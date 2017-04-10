package me.cchiang.lookforthings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PMChatActivity extends AppCompatActivity {

    DatabaseReference mFirebaseDatabaseReference;
    Button searchButton, addFriendButton;
    EditText displayNameSearch;
    TextView userDisplayNameView, userEmailView;
    User searchedUser;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    final String FRIEND_LIST = "userFriendList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmchat);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        searchButton = (Button) findViewById(R.id.PM_searchButton);
        addFriendButton = (Button) findViewById(R.id.PM_addFriendButton);
        displayNameSearch = (EditText) findViewById(R.id.PM_searchEditText);
        userDisplayNameView = (TextView) findViewById(R.id.PM_userDisplayName);
        userEmailView = (TextView) findViewById(R.id.PM_userEmail);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClick(displayNameSearch.getText().toString());
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(searchedUser);
            }
        });

    }

    private void searchClick(String displayName) {
        Query ref = mFirebaseDatabaseReference.child("userList").orderByChild("displayName").equalTo(displayName);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                searchedUser = dataSnapshot.getValue(User.class);

                if(null != searchedUser) {
                    String email = searchedUser.getEmail();
                    String displayName = searchedUser.getDisplayName();
                    userDisplayNameView.setText(email);
                    userEmailView.setText(displayName);
                }

                else {
                    userDisplayNameView.setText("No Users Found");
                    userEmailView.setText("No Users Found");
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
}
