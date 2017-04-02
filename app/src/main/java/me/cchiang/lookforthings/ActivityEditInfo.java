package me.cchiang.lookforthings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ActivityEditInfo extends AppCompatActivity {
    private EditText input_username, input_password;
    private Button btnChange;
    private View parent_view;
    private FirebaseUser user;
    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        parent_view = findViewById(android.R.id.content);

        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);

        btnChange = (Button) findViewById(R.id.btn_change);

        Intent i = getIntent();
        String display = i.getStringExtra("view");

        populateView(display);


    }

    private void populateView(String display){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(display.equals("Display")){
            input_username.setVisibility(View.VISIBLE);
            input_username.setText(user.getDisplayName());
            input_password.setHint("New Display");
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    changeDisplay();
                }
            });

        }else if(display.equals("Email")){
            input_username.setVisibility(View.VISIBLE);
            input_username.setHint("Email");
            input_password.setHint("New Email");
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    changeEmail();
                }
            });

        }else if(display.equals("Password")){
            input_username.setVisibility(View.GONE);
            input_password.setHint("New Password");
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   changePassword();
                }
            });

        }
    }

    private void changeDisplay(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (user != null && !input_password.getText().toString().trim().equals("")) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabaseReference.child("userList").child(user.getUid()).child("displayName").setValue(input_password.getText().toString());
            Toast.makeText(ActivityEditInfo.this, "Changed successfully", Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
        } else if (input_password.getText().toString().trim().equals("")) {
            input_password.setError("Enter new display name");
//            progressBar.setVisibility(View.GONE);
        }
    }

    private void changeEmail(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (user != null && !input_password.getText().toString().trim().equals("")) {
            user.updateEmail(input_password.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ActivityEditInfo.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
//                                signOut();
//                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(ActivityEditInfo.this, "Failed to update email!", Toast.LENGTH_LONG).show();
//                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else if (input_password.getText().toString().trim().equals("")) {
            input_password.setError("Enter email");
//            progressBar.setVisibility(View.GONE);
        }
    }

    private void changePassword(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (user != null && !input_password.getText().toString().trim().equals("")) {
            if (input_password.getText().toString().trim().length() < 6) {
                input_password.setError("Password too short, enter minimum 6 characters");
//                progressBar.setVisibility(View.GONE);
            } else {
                user.updatePassword(input_password.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ActivityEditInfo.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
//                                    signOut();
//                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(ActivityEditInfo.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
//                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else if (input_password.getText().toString().trim().equals("")) {
            input_password.setError("Enter password");
//            progressBar.setVisibility(View.GONE);
        }
    }


    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        Snackbar.make(parent_view, "Registration Success", Snackbar.LENGTH_SHORT).show();
        hideKeyboard();
    }

    private boolean validateName() {
        if (input_username.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(parent_view, getString(R.string.err_msg_name), Snackbar.LENGTH_SHORT).show();
            requestFocus(input_username);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String password = input_password.getText().toString().trim();

        if (password.isEmpty()) {
            Snackbar.make(parent_view, getString(R.string.err_msg_password), Snackbar.LENGTH_SHORT).show();
            requestFocus(input_password);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
