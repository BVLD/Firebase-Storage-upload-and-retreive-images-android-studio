package e.geertvanleuven.blog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {


    //LAYOUT
    private EditText mET_Email;
    private EditText mET_Password;
    private EditText mET_Confirm_Password;

    private Button mBtn_Register;
    private Button mBtn_Already_Account;

    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        //LAYOUT
        mET_Email = (EditText) findViewById(R.id.ET_Email_Register);
        mET_Password = (EditText) findViewById(R.id.ET_Password_Register);
        mET_Confirm_Password = (EditText) findViewById(R.id.ET_Password_Confirm_Register);

        mBtn_Register = (Button) findViewById(R.id.Btn_Register_Register);
        mBtn_Already_Account = (Button) findViewById(R.id.Btn_Already_ACC_Register);

        //FIREBASE AUTHENTICATIOn
        mAuth = FirebaseAuth.getInstance();

        //ONCLICKLISTENERS
        mBtn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    REGISTER();

                }
            });


        mBtn_Already_Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);

            }
        });

    }


    //HANDLES THE REGISTER PART
    private void REGISTER() {

        String email = mET_Email.getText().toString();
        String password = mET_Password.getText().toString();
        String confirmPassword = mET_Confirm_Password.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){

            if(password.equals(confirmPassword)){

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            Intent intent = new Intent(Register.this, Setup.class);
                            startActivity(intent);
                            finish();

                        }else{

                            String ERROR = task.getException().getMessage();

                            Toast.makeText(Register.this, ERROR, Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }else{

                Toast.makeText(Register.this, "The password doesn't mach", Toast.LENGTH_SHORT).show();

            }

        }else{

            Toast.makeText(Register.this, "Please fill in al the fields", Toast.LENGTH_SHORT).show();

        }

    }


    //CHECKS IF THE USER IS ALREADY LOGGED IN
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            SENDTOMAIN();

        }

    }


    //SENDS THE USER TO THE MAIN ACCOUNT
    private void SENDTOMAIN() {

        Intent intent = new Intent(Register.this, Home.class);
        startActivity(intent);
        finish();

    }
}
