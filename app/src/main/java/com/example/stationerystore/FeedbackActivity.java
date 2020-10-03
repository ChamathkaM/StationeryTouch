package com.example.stationerystore;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedbackActivity extends AppCompatActivity {
    private Button Feedbackbtn;

    AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Feedbackbtn = findViewById(R.id.feedbackbtn);

        //initialize validation style
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //add validation for Name
        awesomeValidation.addValidation(this,R.id.editName, RegexTemplate.NOT_EMPTY,R.string.invalid_Name);

        //add validation for email
        awesomeValidation.addValidation(this,R.id.editEmail, Patterns.EMAIL_ADDRESS,R.string.invalid_Email);


        //call dialogbox upon clicking on feedbackbtn
        Feedbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private  void showDialog(){

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Feedback form");
        dialog.setMessage("Provide us Your Valueable feeback");

        LayoutInflater inflater = LayoutInflater.from(this);

        View reg_layout = inflater.inflate(R.layout.send_feedback,null);//access send_feedback layout

        final EditText editEmail = reg_layout.findViewById(R.id.editEmail);
        final EditText editName = reg_layout.findViewById(R.id.editName);
        final EditText feedbackmsg = reg_layout.findViewById(R.id.feedbackmsg);

        dialog.setView(reg_layout);

        //set button
        dialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (awesomeValidation.validate()) {
                    //check validation
                    if (TextUtils.isEmpty(editEmail.getText().toString())) {
                        Toast.makeText(FeedbackActivity.this, "Please enter Your email..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(editName.getText().toString())) {
                        Toast.makeText(FeedbackActivity.this, "Please enter Your name..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(feedbackmsg.getText().toString())) {
                        Toast.makeText(FeedbackActivity.this, "Please enter Your Feedback..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //connect to firebase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference();

                    myref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Object value = snapshot.getValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(FeedbackActivity.this, "Falied to read value", Toast.LENGTH_SHORT).show();
                        }
                    });
                    myref.child("User").child("Feedback").child(editName.getText().toString()).child("Email").setValue(editEmail.getText().toString());
                    myref.child("User").child("Feedback").child(editName.getText().toString()).child("Name").setValue(editName.getText().toString());
                    myref.child("User").child("Feedback").child(editName.getText().toString()).child("Feedback").setValue(feedbackmsg.getText().toString());
                    Toast.makeText(FeedbackActivity.this, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}
