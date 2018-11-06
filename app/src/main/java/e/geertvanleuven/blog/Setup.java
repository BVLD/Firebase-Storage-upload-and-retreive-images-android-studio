package e.geertvanleuven.blog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup extends AppCompatActivity {

    //LAYOUT
    private CircleImageView mIV_Profile;
    private EditText mET_Name;
    private Button mBtn_Save;

    //USER ID
    private String user_id;

    //URI
    private Uri mainImageURI;

    //REQUEST CODE
    public static final int REQUEST = 1;

    public static final int FUCK_UP = 2;

    //FIREBASE STORAGE
    private StorageReference mStorageReference;

    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;

    //FIREBASE FIRESTORE
    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //LAYOUT
        Toolbar setUpToolbar = findViewById(R.id.toolbar_Setup);
        mET_Name = findViewById(R.id.ET_Name_Setup);
        mBtn_Save = findViewById(R.id.Btn_Save_Setup);

        mIV_Profile = (CircleImageView) findViewById(R.id.IV_Profile_Setup);


        //TOOLBAR
        setSupportActionBar(setUpToolbar);
        getSupportActionBar().setTitle("Account setup");

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        //USER ID
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //FIREBASE STORAGE
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //FIREBASE FIRSTORE
        mFirestore = FirebaseFirestore.getInstance();

        //RETREIVE DATA
        mFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mET_Name.setText(name);


                        Glide.with(Setup.this).load(image).into(mIV_Profile);


                    }
                } else {

                    Toast.makeText(Setup.this, "ERROR", Toast.LENGTH_SHORT).show();

                }

            }
        });


        //SETONCLICKLISTENRERS
        mIV_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //LETS THE USER SEE THE PERMISION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    //LETS THE USER CHOSE TO EXEXPT IT OR DENIE IT
                    if (ContextCompat.checkSelfPermission(Setup.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(Setup.this, "Permision denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(Setup.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


                    } else {

                        CHOOSEFOTO();


                    }


                } else {

                    CHOOSEFOTO();

                }

            }
        });


        //IF THE USER SAVES IT.
        mBtn_Save.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                SAVE_NAME_AND_PHOTO();

            }
        });
    }


    //LETS THE USER GO TO THE GALLARY TO CHOOSE A PHOTO
    private void CHOOSEFOTO() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST);

    }


    //GETS THE URI FROM THE FOTO AND SETS THE FOTO INTO THE IMAGEVIEW
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST) {


            if (resultCode == RESULT_OK) {

                mainImageURI = data.getData();


                mIV_Profile.setImageURI(mainImageURI);

            } else if (resultCode == FUCK_UP) {


                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();

            }


        }
    }

    private void SAVE_NAME_AND_PHOTO() {

        final String user_name = mET_Name.getText().toString();

        if (!TextUtils.isEmpty(user_name)) {

            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            final StorageReference ImagesPath = mStorageReference.child("profile_images").child(user_id + ".jpg");


            ImagesPath.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ImagesPath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {


                        Uri downUri = task.getResult();
                        downUri.toString();


                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("image", downUri.toString());
                        userMap.put("name", user_name);

                        Glide.with(Setup.this).load(downUri).into(mIV_Profile);

                        mFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });



                    }else {

                        String ERROR = task.getException().getMessage();
                        Toast.makeText(Setup.this, ERROR, Toast.LENGTH_SHORT).show();

                    }
                }

            });
        }else {

            Toast.makeText(this, "Enter your name.", Toast.LENGTH_SHORT).show();

        }


    }
}







