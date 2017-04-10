package me.cchiang.lookforthings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.cchiang.lookforthings.adapter.FriendsListAdapter;
import me.cchiang.lookforthings.data.Tools;
import me.cchiang.lookforthings.model.Friend;
import me.cchiang.lookforthings.widget.DividerItemDecoration;

public class ActivityStartGame extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private FriendsListAdapter mAdapter;
    private SearchView search;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser currentUser;
    private List<Friend> items = new ArrayList<>();
    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.cchiang.lookforthings.R.layout.activity_new_chat);

        initToolbar();
        initComponent();
        populateFriends();

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void populateFriends() {

        final Query ref = mFirebaseDatabaseReference.child("userFriendList").child(currentUser.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Query ref2 = mFirebaseDatabaseReference.child("userList").child(dataSnapshot.getValue().toString());
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        if(user != null){
                            Friend friend = new Friend(user);
                            items.add(friend);
                        }

                        mAdapter = new FriendsListAdapter(ctx, items);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new FriendsListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, Friend obj, int position) {
                                ActivityGameDetails.navigate(ActivityStartGame.this, v.findViewById(me.cchiang.lookforthings.R.id.lyt_parent), obj, null);
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });

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


    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(me.cchiang.lookforthings.R.id.recyclerView);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ctx = getApplicationContext();

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(me.cchiang.lookforthings.R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        if(mAdapter != null){
            actionBar.setSubtitle(mAdapter.getItemCount()+" friends");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.cchiang.lookforthings.R.menu.menu_new_chat, menu);
        search = (SearchView) menu.findItem(me.cchiang.lookforthings.R.id.action_search).getActionView();
        search.setIconified(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(mAdapter != null){
                    mAdapter.getFilter().filter(s);
                }
                return true;
            }
        });
        search.onActionViewCollapsed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case me.cchiang.lookforthings.R.id.action_search: {
                // this do magic
                supportInvalidateOptionsMenu();
                return true;
            }
        }
        return false;
    }
}

