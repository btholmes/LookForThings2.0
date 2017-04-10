package me.cchiang.lookforthings.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import me.cchiang.lookforthings.ActivityChatDetails;
import me.cchiang.lookforthings.ActivityMain;
import me.cchiang.lookforthings.FriendListActivity;
import me.cchiang.lookforthings.R;
import me.cchiang.lookforthings.User;
import me.cchiang.lookforthings.adapter.ChatsListAdapter;
import me.cchiang.lookforthings.model.Chat;
import me.cchiang.lookforthings.model.Friend;
import me.cchiang.lookforthings.widget.DividerItemDecoration;

public class ChatsFragment extends Fragment {

    public RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    public ChatsListAdapter mAdapter;
    private ProgressBar progressBar;
    private ActionMode actionMode;
    private List<Chat> items = new ArrayList<>();
    private int counter;
    View view;




    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<User, FriendListActivity.FriendViewHolder> mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private RecyclerView mMessageRecyclerView;
    private RecyclerView mRecyclerView;
    private static FriendListActivity.MyRecyclerAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private FirebaseUser currentUser;
    private static ArrayList<User> userList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);

        // activate fragment menu
        setHasOptionsMenu(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        counter = 0;
        items.clear();

        populateChats();

//        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(mLayoutManager);
//		recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
//
//        items = Constant.getChatsData(getActivity());
//
//        // specify an adapter (see also next example)
//        mAdapter = new ChatsListAdapter(getActivity(), items);
//        recyclerView.setAdapter(mAdapter);
//
//        mAdapter.setOnItemClickListener(new ChatsListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, Chat obj, int position) {
//                if (actionMode != null) {
//                    myToggleSelection(position);
//                    return;
//                }
//                ActivityChatDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj.getFriend(), obj.getSnippet());
//            }
//        });
//
//        mAdapter.setOnItemLongClickListener(new ChatsListAdapter.OnItemLongClickListener() {
//            @Override
//            public void onItemClick(View view, Chat obj, int position) {
//                actionMode = getActivity().startActionMode(modeCallBack);
//                myToggleSelection(position);
//            }
//        });
//
//        bindView();

        return view;
    }

    private void populateChats() {

        final Query ref = mFirebaseDatabaseReference.child("userFriendList").child(currentUser.getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Query ref2 = mFirebaseDatabaseReference.child("userList").child(dataSnapshot.getKey());
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
//                        userList.add(snapshot.getValue(User.class));
//                        adapter = new FriendListActivity.MyRecyclerAdapter(getContext(), userList);
//                        mRecyclerView.setAdapter(adapter);

                        User user = snapshot.getValue(User.class);


                        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

                        // use a linear layout manager
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

//                        items.add(new Chat(i, s_date[i], true, new Friend(s_arr[i+5], drw_arr.getResourceId(i+5, -1)), s_cht[i]));

                        if(user != null){
                            items.add(new Chat(counter, "Yesterday", true, new Friend(user), "Just a snippet"));
                            counter++;
                        }
//                        items = Constant.getChatsData(getActivity());

                        // specify an adapter (see also next example)
                        mAdapter = new ChatsListAdapter(getActivity(), items);
                        recyclerView.setAdapter(mAdapter);

                        mAdapter.setOnItemClickListener(new ChatsListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, Chat obj, int position) {
                                if (actionMode != null) {
                                    myToggleSelection(position);
                                    return;
                                }
                                ActivityChatDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj.getFriend(), obj.getSnippet(), currentUser.getUid(), currentUser.getDisplayName());
                            }
                        });

                        mAdapter.setOnItemLongClickListener(new ChatsListAdapter.OnItemLongClickListener() {
                            @Override
                            public void onItemClick(View view, Chat obj, int position) {
                                actionMode = getActivity().startActionMode(modeCallBack);
                                myToggleSelection(position);
                            }
                        });

                        bindView();


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


    private void dialogDeleteMessageConfirm(final int count) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Confirmation");
        builder.setMessage("All chat from " + count + " selected item will be deleted?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.removeSelectedItem();
                mAdapter.notifyDataSetChanged();
                Snackbar.make(view, "Delete "+count+" items success", Snackbar.LENGTH_SHORT).show();
                modeCallBack.onDestroyActionMode(actionMode);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }

    }

    public void onRefreshLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


    private void myToggleSelection(int idx) {
        mAdapter.toggleSelection(idx);
        String title = mAdapter.getSelectedItemCount() + " selected";
        actionMode.setTitle(title);
    }

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            // Inflate a menu resource providing context menu items
             MenuInflater inflater = actionMode.getMenuInflater();
             inflater.inflate(R.menu.menu_multiple_select, menu);
            ((ActivityMain)getActivity()).setVisibilityAppBar(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.action_delete && mAdapter.getSelectedItemCount()>0){
                dialogDeleteMessageConfirm(mAdapter.getSelectedItemCount());
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode act) {
            actionMode.finish();
            actionMode = null;
            mAdapter.clearSelections();
            ((ActivityMain)getActivity()).setVisibilityAppBar(true);
        }
    };
}
