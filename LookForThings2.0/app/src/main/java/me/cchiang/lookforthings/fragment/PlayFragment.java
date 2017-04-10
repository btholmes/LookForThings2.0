package me.cchiang.lookforthings.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import me.cchiang.lookforthings.R;
import me.cchiang.lookforthings.User;

/**
 * Created by btholmes on 3/29/17.
 */

public class PlayFragment extends Fragment {

    View view;

    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mRecyclerView;
//    private static GameMainActivity.MyRecyclerAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseUser currentUser;
    private static ArrayList<User> userList = new ArrayList<>();
    public Button newGameBtn;

    public TextView friendName;
    public Button answerBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main_look, container, false);



        return view;

    }

}
