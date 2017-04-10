//How to Capture Image from Camera and Display in Android ImageView/ Activity

package me.cchiang.lookforthings;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import me.cchiang.lookforthings.model.Player;


public class cameraActivity extends AppCompatActivity {

    public static void navigate(AppCompatActivity activity, Game obj) {
        Intent intent = new Intent(activity, cameraActivity.class);
        intent.putExtra(KEY_GAME, obj);
        ActivityCompat.startActivity(activity, intent, null);
    }

    private static final int READ_CONTACTS = 1000;
    private static final int WRITE_EXTERNAL_STORAGE = 1001;
    private static final int READ_EXTERNAL_STORAGE = 1002;

    public static boolean CAN_READ_CONTACTS = false;
    public static boolean CAN_WRITE_EXTERNAL_STORAGE = false;
    public static boolean CAN_READ_EXTERNAL_STORAGE = false;
    public static String imageURL;


    private static final int CAMERA_REQUEST = 1888;

    private ImageButton cameraButton;
    private TextView head;
    private TextView tagText, checkText, needList;
    private ArrayList<String> tags = new ArrayList<>();
    private ArrayList<String> checkList = new ArrayList<>(Arrays.asList("chair", "table", "computer"));
//    private ArrayList<String> checkList = gameRoomActivity.list;
    private ArrayList<String> leftList = new ArrayList<>();

    String encodedImage;
    ImageView mimageView;
    Bitmap photo;
    private Uri selectedImage;
    private Game game;

    private final ClarifaiClient clarifaiClient = new ClarifaiBuilder(Credential.CLIENT_ID,
            Credential.CLIENT_SECRET).buildSync();

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 200;

    private  FirebaseUser user;
    private DatabaseReference mFirebaseDatabaseReference;
    public static String KEY_GAME = "Game";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initComponent();

        Intent intent = getIntent();
        game = (Game) intent.getExtras().getSerializable(KEY_GAME);

//        Only need to check permissions on newer versions of android
//        checkPermissions();
        for (int i = 0 ; i < checkList.size(); i++){
            String temp = checkList.get(i);
            leftList.add(temp);
        }
    }


    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if(gameRoomActivity.CAN_WRITE_EXTERNAL_STORAGE){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, WRITE_EXTERNAL_STORAGE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS) {
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_CONTACTS = true;
                }
            }
        }
        else if(requestCode == WRITE_EXTERNAL_STORAGE){
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_WRITE_EXTERNAL_STORAGE = true;
                }
            }
        }
        else if (requestCode == READ_EXTERNAL_STORAGE){
            if(grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_EXTERNAL_STORAGE = true;
                }
            }
        }
    }

    /**
     * Store views for camera and gallery buttons and for the TextView for displaying tags
     */

    /**
     * Camera button handler
     */
    public void handleCameraBtnClick() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearFields();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }


    private void initComponent(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        head = (TextView) findViewById(R.id.head);
        tagText = (TextView) findViewById(R.id.tag_text);
        checkText = (TextView) findViewById(R.id.check_text);

        needList = (TextView) this.findViewById(R.id.needList);
        needList.setText("chair, table, computer");
        head.setText(gameRoomActivity.word);
        mimageView = (ImageView) this.findViewById(R.id.picture);
        handleCameraBtnClick();
    }


    /**
     * Clears tag values, tag TextView, and preview ImageView
     */
    public void clearFields() {
        tags.clear();
        tagText.setText("");
        ((ImageView)findViewById(R.id.picture)).setImageResource(android.R.color.transparent);
    }

    /**
     * Prints the first 10 tags for an image
     */
    public void printTags() {
        String results = "TAGS: ";
        for(int i = 0; i < 10; i++) {
            results += "\n" + tags.get(i);
        }
        tagText.setText(results);
    }

    /*
     * Check if the Clarifai tags matches the requested tags 
     */
    public void checkMatch(){
        String looking = "MATCHED: ";

        // Check for matches and remove match form leftList
        for(int i = 0; i < 10; i++) {
            if(checkList.contains(tags.get(i))){
                looking += "\n" + tags.get(i);
                leftList.remove(tags.get(i));
            }
        }

        looking += "\n\n NEEDS:";

        // Displays whats lefts
        for(int j = 0; j < leftList.size(); j++){
            looking += "\n" + leftList.get(j);
        }

        checkText.setText(looking);

        // If list if empty you win...
        if(leftList.isEmpty()){
            Intent intent = new Intent(cameraActivity.this, gameRoomActivity.class);
            startActivity(intent);
        }
    }


    //   THIS BLOCK IS THE SERVER UPLOADING CODE  *****************************************



    public String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    public void uploadToServer() {

        final ProgressDialog loading = ProgressDialog.show(cameraActivity.this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int responseCode = Integer.parseInt(jsonObject.getString("responseCode"));
                            String response = jsonObject.getString("response");
                            if (responseCode == 1) {
                                Toast.makeText(cameraActivity.this, response, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(cameraActivity.this, "Error: " + response, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(cameraActivity.this, "Failed to upload.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        try {
                            JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                            int responseCode = Integer.parseInt(jsonObject.getString("responseCode"));
                            String response = jsonObject.getString("response");
                            if (responseCode == 1) {
                                Toast.makeText(cameraActivity.this, response, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(cameraActivity.this, "Error: " + response, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(cameraActivity.this, "Failed to upload.", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                Long currentTime = System.currentTimeMillis();
                String milis = currentTime.toString();
                params.put("base64", encodedImage);
                params.put("fileName", milis);
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                params.put("userEmail", email);
                params.put("gameFolder", "currentGameFolder");


                imageURL = "http://proj-309-gb-3.cs.iastate.edu/images/" + email + "/currentGameFolder/" + milis + ".png";

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        if(imageURL != null){
            addImageUrlToFirebase(imageURL);
        }


    }


    private void setPlayerInfo(final String url){

        Query ref = mFirebaseDatabaseReference.child("PlayerInfo").child(game.getGameID()).child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    Player player = snapshot.getValue(Player.class);
                    player.setLastPictureTaken(url);
                    mFirebaseDatabaseReference.child("PlayerInfo").child(game.getGameID()).child(user.getUid()).setValue(player);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addImageUrlToFirebase(String url){

            mFirebaseDatabaseReference.child("Images").child(user.getUid()).child("currentGameFolder").push().setValue(url);
            mFirebaseDatabaseReference.child("userList").child(user.getUid()).child("lastPictureTaken").setValue(url);

            setPlayerInfo(url);
            Toast.makeText(cameraActivity.this, "Image Saved", Toast.LENGTH_LONG).show();

    }

    //   THIS ENDS THE BLOCK FOR THE SERVER UPLOADING CODE  *****************************************

    /*
     * Clarifai API Call
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            encodedImage = toBase64(photo);
            uploadToServer();
        }

        //check if image was collected successfully
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE &&
                resultCode == RESULT_OK) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            final ImageView preview = (ImageView)findViewById(R.id.picture);
            preview.setImageBitmap(bitmap);

            new AsyncTask<Bitmap, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {

                // Model prediction
                @Override
                protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Bitmap... bitmaps) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] byteArray = stream.toByteArray();
                    final ConceptModel general = clarifaiClient.getDefaultModels().generalModel();
                    return general.predict()
                            .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                            .executeSync();
                }

                /*
                 * Handling API response and then collecting and printing tags
                 */
                @Override
                protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "API contact error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final List<ClarifaiOutput<Concept>> predictions = response.get();
                    if (predictions.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No results from API", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final List<Concept> predictedTags = predictions.get(0).data();
                    for(int i = 0; i < predictedTags.size(); i++) {
                        tags.add(predictedTags.get(i).name());
                    }
                    printTags();
                    checkMatch();
                }
            }.execute(bitmap);
        }
    }
}
