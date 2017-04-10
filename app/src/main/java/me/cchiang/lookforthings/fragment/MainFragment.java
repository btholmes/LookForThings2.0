package me.cchiang.lookforthings.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import java.util.ArrayList;
import java.util.List;

import me.cchiang.lookforthings.ActivityMain;
import me.cchiang.lookforthings.ActivitySelectedGame;
import me.cchiang.lookforthings.ActivityStartGame;
import me.cchiang.lookforthings.Game;
import me.cchiang.lookforthings.LoginActivity;
import me.cchiang.lookforthings.R;
import me.cchiang.lookforthings.adapter.MainListAdapter;
import me.cchiang.lookforthings.widget.DividerItemDecoration;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainFragment extends Fragment {

    public RecyclerView recyclerView;
    View view;

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;


    private Button gameBtn;

    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mRecyclerView;
    private static MainListAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseUser currentUser;
    private static ArrayList<Game> gameList = new ArrayList<>();
    private long gameCount;
    private static Context ctx;

    public Button newGameBtn, userStatsBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_main_look, container, false);
        initComponent();

        populateGames();
        setLastPictureTaken();

//        checkIfUserIsInDB();

        return view;
    }

    private void initComponent(){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        user =  FirebaseAuth.getInstance().getCurrentUser();


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setStackFromEnd(false);
        mLinearLayoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                    finish();
                }
            }
        };
        auth.addAuthStateListener(authListener);

        // New child entries
        newGameBtn = (Button) view.findViewById(R.id.new_game_btn);
        // button to redirect to new game
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityStartGame.class);
                startActivity(intent);
            }
        });

//        userStatsBtn = (Button) view.findViewById(R.id.user_stats);
//        // button to redirect to new game
//        userStatsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), gameRoomActivity.class);
//                startActivity(intent);
//            }
//        });

        //        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//
//        if (progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }
//        if(user != null){
//            getDisplayName();
//        }

//        Button dataButton = (Button) view.findViewById(R.id.dataButton);
//        dataButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), dataCollectionActivity.class);
//                startActivity(intent);
//            }
//
//        });

//        ImageView userProfileImageButton = (ImageView) view.findViewById(R.id.userImage);
//        userProfileImageButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//                startActivity(intent);
//            }
//        });


    }


    private void populateGames() {

        final Query ref = mFirebaseDatabaseReference.child("userList").child(currentUser.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().equals("games")){
                    List<String> list = new ArrayList<>();
                    gameCount = dataSnapshot.getChildrenCount();

                    for(DataSnapshot dsp : dataSnapshot.getChildren()){
                        list.add(String.valueOf(dsp.getValue())); //add result into array list

                        Query ref2 = mFirebaseDatabaseReference.child("Games").child(dsp.getValue().toString());
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.getValue() != null){
                                    Game newGame = snapshot.getValue(Game.class);
                                    gameList.add(newGame);
                                    adapter = new MainListAdapter(getApplicationContext(), gameList);

                                    adapter.setOnItemClickListener(new MainListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, Game obj, int position) {
//                                            if (actionMode != null) {
//                                                myToggleSelection(position);
//                                                return;
//                                            }
                                            ActivitySelectedGame.navigate((ActivityMain)getActivity(), v.findViewById(R.id.lyt_parent), obj);
                                        }
                                    });

                                    mRecyclerView.setAdapter(adapter);

                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


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
//        auth.addAuthStateListener(authListener);
//        populateGames();
//        setLastPictureTaken();

    }


    //    FIREBASE METHODS

    @Override
    public void onResume() {
        super.onResume();
//        if(progressBar != null){
//            progressBar.setVisibility(View.GONE);
//
//        }
    }

    public void setLastPictureTaken(){

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final Query ref = mFirebaseDatabaseReference.child("userList").child(currentUser.getUid());
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
//    private void checkIfUserIsInDB() {
//        FirebaseUser _user = auth.getCurrentUser();
//        if(null != _user){
//            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
//        gameList.clear();
//        if (authListener != null) {
//            auth.removeAuthStateListener(authListener);
//        }
    }


}
