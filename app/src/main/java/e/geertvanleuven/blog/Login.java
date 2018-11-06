package e.geertvanleuven.blog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    //LAYOUT
    private EditText mET_Email;
    private EditText mET_Password;

    private Button mBtn_Login;
    private Button mBtn_Create_ACC;

    private ProgressBar mProgressBar;

    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;


    //FIREBASE USER
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //LAYOUT
        mET_Email = (EditText) findViewById(R.id.ET_Email_Login);
        mET_Password = (EditText) findViewById(R.id.ET_Password_Login);

        mBtn_Login = (Button) findViewById(R.id.Btn_Login_Login);
        mBtn_Create_ACC = (Button) findViewById(R.id.Btn_New_Acc_Login);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_Login);

        //FIREBASE AUTHENTICATION
        mAuth = FirebaseAuth.getInstance();

        //FIREBASE USER
        mUser = FirebaseAuth.getInstance().getCurrentUser();



        mProgressBar.setVisibility(View.INVISIBLE);

        //LOGIN


        //ONCLICKLISTENERS

        mBtn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LOGIN();


            }
        });

        mBtn_Create_ACC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);

            }
        });


    }


    //HNADELS THE USER LOGIN
    private void LOGIN(){

        mProgressBar.setVisibility(View.VISIBLE);

        String email = mET_Email.getText().toString();
        String password = mET_Password.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Intent intent = new Intent(Login.this, Home.class);
                                startActivity(intent);
                                finish();

                            } else {

                                mProgressBar.setVisibility(View.INVISIBLE);

                                String ERROR = task.getException().getMessage();

                                Toast.makeText(Login.this, ERROR, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

        } else {

            Toast.makeText(Login.this, "Please fill in al the fields.", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }
    }


}