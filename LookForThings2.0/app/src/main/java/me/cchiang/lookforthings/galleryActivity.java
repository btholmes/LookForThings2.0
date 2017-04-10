package me.cchiang.lookforthings;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class galleryActivity extends AppCompatActivity {


    ImageView selectedImage;
    private GalleryImageAdapter galleryImageAdapter;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_image);

    }

    public void populateImageIDs(){

//        galleryImageAdapter.mImageIds.add("http://proj-309-gb-3.cs.iastate.edu/images/btholmes@iastate.edu/currentGameFolder/1490478899979.png");
//        galleryImageAdapter.mImageIds.add("http://proj-309-gb-3.cs.iastate.edu/images/btholmes@iastate.edu/currentGameFolder/1490478899979.png");

        final Query ref = mFirebaseDatabaseReference.child("Images").child(user.getUid()).child("currentGameFolder");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                galleryImageAdapter.mImageIds.add(dataSnapshot.getValue(String.class));

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

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        selectedImage=(ImageView)findViewById(R.id.imageDisplay);
        gallery.setSpacing(1);
        galleryImageAdapter = new GalleryImageAdapter(this);

        populateImageIDs();

//        int size = galleryImageAdapter.mImageIds.size();
        gallery.setAdapter(galleryImageAdapter);


        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // show the selected Image
//                selectedImage.setImageResource(galleryImageAdapter.mImageIds.get(position));
                if(galleryImageAdapter.mImageIds.size() > position){
                    Picasso.with(galleryImageAdapter.getContext()).load(galleryImageAdapter.mImageIds.get(position)).into(selectedImage);
                }

            }
        });

    }








    //  ****************************************  THE BELOW CODE IS LIKE FRIEDLISTACTIVITY IMPLEMENTATION ****************************************




//    private RecyclerView mRecyclerView;
//    private static galleryActivity.MyRecyclerAdapter adapter;
//    private static ArrayList<String> userImageURLs = new ArrayList<>();
//    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//    private DatabaseReference mFirebaseDatabaseReference;
//    private LinearLayoutManager mLinearLayoutManager;
//    private ProgressBar mProgressBar;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_gallery);
//
//        mProgressBar = (ProgressBar) findViewById(R.id.galleryProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        mRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setStackFromEnd(true);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//
//        // New child entries
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//
////        setInitialImage("http://proj-309-gb-3.cs.iastate.edu/images/btholmes@iastate.edu/currentGameFolder/1490478899979.png");
////        setImageRotateListener();
//
//        populateImages();
//
//    }
//
//    public void populateImages() {
//
//        final Query ref = mFirebaseDatabaseReference.child("Images").child(user.getUid()).child("currentGameFolder");
//        ref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                userImageURLs.add(dataSnapshot.getValue(String.class));
//                adapter = new galleryActivity.MyRecyclerAdapter(getApplicationContext(), userImageURLs);
//                mRecyclerView.setAdapter(adapter);
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
//
//    }
//
//
//
//
//    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
//        public ImageView galleryImage;
////        public Button deleteFriendButton;
////        public Button sendPMButton;
//        //public CircleImageView friendImageView;
//        public GalleryViewHolder(View v) {
//            super(v);
//            galleryImage = (ImageView) itemView.findViewById(R.id.imageDisplay);
//
////            deleteFriendButton = (Button) itemView.findViewById(deleteFriendButton);
////            deleteFriendButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    String displayName = galleryImage.getText().toString();
////                    String uidToDelete;
////                    for (User user: userList) {
////                        if(user.getDisplayName().equals(displayName)) {
////                            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
////                            uidToDelete = user.getUid();
////                            DatabaseReference mFirebaseDatabaseReference;
////                            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();;
////                            mFirebaseDatabaseReference.child("userFriendList").child(currentUserUid).child(uidToDelete).removeValue();
////                            break;
////                        }
////                    }
////                }
////            });
////
////            sendPMButton = (Button) v.findViewById(sendPMButton);
////            sendPMButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////
////                    String displayName = galleryImage.getText().toString();
////                    String uidToSend = "";
////                    for (User user: userList) {
////                        if(user.getDisplayName().equals(displayName)) {
////                            uidToSend = user.getUid();
////                            break;
////                        }
////                    }
////                    Intent intent = new Intent(v.getContext(), SendPMActivity.class);
////                    intent.putExtra("uid", uidToSend);
////                    intent.putExtra("displayName", displayName);
////                    v.getContext().startActivity(intent);
////                }
////            });
//        }
//    }
//
//
//
//    public static class MyRecyclerAdapter extends RecyclerView.Adapter<galleryActivity.GalleryViewHolder> {
//
//        private List<String> listItems, filterList;
//        private Context mContext;
//
//        public MyRecyclerAdapter(Context context, ArrayList<String> listItems) {
//            this.listItems = listItems;
//            this.mContext = context;
//            this.filterList = new ArrayList<>();
//            // we copy the original list to the filter list and use it for setting row values
//            this.filterList.addAll(this.listItems);
//        }
//
//        @Override
//        public galleryActivity.GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//
//            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, null);
//            galleryActivity.GalleryViewHolder viewHolder = new galleryActivity.GalleryViewHolder(view);
//            return viewHolder;
//
//        }
//
//        @Override
//        public void onBindViewHolder(galleryActivity.GalleryViewHolder customViewHolder, int position) {
//
//            String listItem = filterList.get(position);
////            customViewHolder.galleryImage.setImageResource(R.drawable.camera);
//
//
//            Picasso.with(mContext).load("http://proj-309-gb-3.cs.iastate.edu/images/btholmes@iastate.edu/currentGameFolder/1490478899979.png").into(customViewHolder.galleryImage);
////            ImageDownloader imageDownLoader = new ImageDownloader(imageView);
////            imageDownLoader.execute(url);
//        }
//
//        @Override
//        public int getItemCount() {
//            return (null != filterList ? filterList.size() : 0);
//        }
//
//    }
//
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        userImageURLs.clear();
//    }


//  ****************************************  THE BELOW CODE SETS IMAGEVIEW BITMAP INSTEAD OF PICASSOO ****************************************

//    private int currImage = 0;

//    private String imageUrls[] = { "http://proj-309-gb-3.cs.iastate.edu/images/fufu@gmail.com/currentGameFolder/1490391805838.png",
//            "http://proj-309-gb-3.cs.iastate.edu/images/fufu@gmail.com/currentGameFolder/1490391827957.png",
//            "https://wallpaperbrowse.com/media/images/desktop-year-of-the-tiger-images-wallpaper.jpg"
//
//    };

//    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//        String url;
//
//        public ImageDownloader(ImageView bmImage, String url) {
//            this.bmImage = bmImage;
//            this.url = url;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
////            String url = urls[0];
//            Bitmap bitmap = null;
//            try {
//                InputStream in = new java.net.URL(url).openStream();
//                bitmap = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("MyApp", e.getMessage());
//            }
//            return bitmap;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//
//            bmImage.setImageBitmap(result);
//        }
//    }

//    private void setImageRotateListener() {
//        final Button rotatebutton = (Button) findViewById(R.id.nextImage);
//        rotatebutton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                currImage++;
//                if(currImage == 3) {
//                    currImage = 0;
//                }
//                setCurrentImage();
//            }
//        });
//    }

//    private void setInitialImage(String url) {
//        setCurrentImage(url);
//    }

//    private void setCurrentImage(String url) {
//        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
//        ImageDownloader imageDownLoader = new ImageDownloader(imageView);
//        imageDownLoader.execute(url);
//    }



}
