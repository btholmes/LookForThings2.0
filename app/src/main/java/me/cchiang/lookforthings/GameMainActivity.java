package me.cchiang.lookforthings;

import android.support.v7.app.AppCompatActivity;


public class GameMainActivity extends AppCompatActivity {

//    public static class FriendViewHolder extends RecyclerView.ViewHolder {
//        public TextView friendName;
//        public Button answerBtn;


//        public CircleImageView friendImageView;
//        public FriendViewHolder(View v) {
//            super(v);
//            friendName = (TextView) itemView.findViewById(R.id.friendDisplayNameTextView);
//            answerBtn = (Button) v.findViewById(answerBtn);
//            CircleImageView friendImageView = (CircleImageView) v.findViewById(R.id.friendImageView);
//
//            answerBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    String displayName = friendName.getText().toString();
//                    String uidToSend = "";
//                    for (User user: userList) {
//                        if(user.getDisplayName().equals(displayName)) {
//                            uidToSend = user.getUid();
//                            break;
//                        }
//                    }
//                    Intent intent = new Intent(v.getContext(), cameraActivity.class);
//                    intent.putExtra("uid", uidToSend);
//                    intent.putExtra("displayName", displayName);
//                    v.getContext().startActivity(intent);
//                }
//            });
//        }
    }

//    public static class MyRecyclerAdapter extends RecyclerView.Adapter<GameMainActivity.FriendViewHolder> {
//
//        private List<User> listItems, filterList;
//        private Context mContext;
//
//        public MyRecyclerAdapter(Context context, List<User> listItems) {
//            this.listItems = listItems;
//            this.mContext = context;
//            this.filterList = new ArrayList<User>();
//            // we copy the original list to the filter list and use it for setting row values
//            this.filterList.addAll(this.listItems);
//        }
//
//        @Override
//        public GameMainActivity.FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//
//            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lobby_friend, null);
//            GameMainActivity.FriendViewHolder viewHolder = new GameMainActivity.FriendViewHolder(view);
//            return viewHolder;
//
//        }
//
//        @Override
//        public void onBindViewHolder(GameMainActivity.FriendViewHolder customViewHolder, int position) {
//
//            User listItem = filterList.get(position);
//            customViewHolder.friendName.setText(listItem.getDisplayName());
//        }
//
//        @Override
//        public int getItemCount() {
//            return (null != filterList ? filterList.size() : 0);
//        }
//
//    }
//
////    private DatabaseReference mFirebaseDatabaseReference;
////    private RecyclerView mRecyclerView;
////    private static MyRecyclerAdapter adapter;
////    private LinearLayoutManager mLinearLayoutManager;
////    private FirebaseUser currentUser;
////    private static ArrayList<User> userList = new ArrayList<>();
//
////    public Button newGameBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game_main);
//
//        // Elements of Activity
//        newGameBtn = (Button) this.findViewById(R.id.newGameBtn);
//
//
//
//        mRecyclerView = (RecyclerView) findViewById(R.id.friendRecyclerView);
//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setStackFromEnd(true);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // New child entries
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//
//        populateFriends();
//
//
//        // button to redirect to new game
//        newGameBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(GameMainActivity.this, gameRoomActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//
//    private void populateFriends() {
//
//        final Query ref = mFirebaseDatabaseReference.child("userFriendList").child(currentUser.getUid());
//        ref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                Query ref2 = mFirebaseDatabaseReference.child("userList").child(dataSnapshot.getKey());
//                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        userList.add(snapshot.getValue(User.class));
//                        adapter = new MyRecyclerAdapter(getApplicationContext(), userList);
//                        mRecyclerView.setAdapter(adapter);
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        userList.clear();
//    }

//}
