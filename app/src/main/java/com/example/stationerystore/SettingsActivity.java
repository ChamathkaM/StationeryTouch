package com.example.stationerystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stationerystore.Prevalent.Prevalent;
import com.example.stationerystore.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    ImageView profImage,deleteBtn;
    EditText fNaeEdit,lNameEdit,uNameEdit,emailEdit;
    TextView profChangeBtn,saveBtn,cancelBtn;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        fNaeEdit= findViewById(R.id.change_fName);
        lNameEdit= findViewById(R.id.change_lName);
        uNameEdit= findViewById(R.id.change_uName);
        emailEdit= findViewById(R.id.change_email);
        profImage=findViewById(R.id.settins_img);
        profChangeBtn= findViewById(R.id.image_change_btn);
        saveBtn= findViewById(R.id.update_setting);
        cancelBtn= findViewById(R.id.close_setting);
        deleteBtn = findViewById(R.id.delete_btn);


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });
        userInfoDisplay(profImage,fNaeEdit,lNameEdit,uNameEdit,emailEdit);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateOnlyUserInfo();
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }

        });

        profChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Please confirm");
        builder.setMessage("Are you sure want to delete your account?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final ProgressDialog pd2 = new ProgressDialog(SettingsActivity.this);
                pd2.setMessage("Deleting...");
                pd2.setCanceledOnTouchOutside(false);
                pd2.show();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                ref.child(Prevalent.currentOnlineUser.getPhone()).removeValue();
                StorageReference proPicRef = FirebaseStorage.getInstance().getReferenceFromUrl(Prevalent.currentOnlineUser.getImage());
                proPicRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd2.dismiss();
                        Toast.makeText(SettingsActivity.this, "Your account deleted successfully.", Toast.LENGTH_SHORT).show();

                        Paper.book().destroy();
                        Intent intent = new Intent(SettingsActivity.this, SignUpActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd2.dismiss();
                        Toast.makeText(SettingsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profImage.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("firstName",fNaeEdit.getText().toString());
        userMap.put("lastName",lNameEdit.getText().toString());
        userMap.put("userName",uNameEdit.getText().toString());
        userMap.put("Email",emailEdit.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        Toast.makeText(SettingsActivity.this,"Details update successfully.",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fNaeEdit.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lNameEdit.getText().toString()))
        {
            Toast.makeText(this, "Name is address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(uNameEdit.getText().toString()))
        {
            Toast.makeText(this, "User is mandatory.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(emailEdit.getText().toString()))
        {
            Toast.makeText(this, "email is mandatory.", Toast.LENGTH_SHORT).show();
        }else if(checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(imageUri != null){
            final StorageReference fileRef = storageProfilePrictureRef.child(Prevalent.currentOnlineUser.getPhone()+ "jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("firstName", fNaeEdit.getText().toString());
                                userMap. put("lastName", lNameEdit.getText().toString());
                                userMap. put("userName", uNameEdit.getText().toString());
                                userMap. put("Email", emailEdit.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                                progressDialog.dismiss();
                                startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                                Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else{
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final ImageView profImage, final EditText fNaeEdit, final EditText lNameEdit, final EditText uNameEdit, final EditText emailEdit) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("User").child(Prevalent.currentOnlineUser.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String fName = snapshot.child("firstName").getValue().toString();
                        String lName = snapshot.child("lastName").getValue().toString();
                        String uName = snapshot.child("userName").getValue().toString();
                        String eMail = snapshot.child("Email").getValue().toString();
                        //  String passwd = snapshot.child("password").getValue().toString();

                        Picasso.get().load(image).into(profImage);
                        fNaeEdit.setText(fName);
                        lNameEdit.setText(lName);
                        uNameEdit.setText(uName);
                        emailEdit.setText(eMail);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}