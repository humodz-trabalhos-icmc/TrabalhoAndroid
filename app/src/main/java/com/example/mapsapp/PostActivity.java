package com.example.mapsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class PostActivity extends AppCompatActivity
        implements View.OnClickListener{

    EditText mEdComment;
    Button mBtnSend;

    String mPostKey;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mEdComment = findViewById(R.id.ed_post);
        mBtnSend = findViewById(R.id.btn_send_comment);

        mBtnSend.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();

        mPostKey = extras.getString("KEY");
        mUsername = extras.getString("USER");
    }

    @Override
    public void onClick(View view) {
        if(view == mBtnSend) {
            String msg = mEdComment.getText().toString();

            if(msg.length() == 0) {
                Toast.makeText(this, "Escreva alguma coisa!", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            CommentEntry comment = new CommentEntry(mUsername, msg);

            FirebaseDatabase
                    .getInstance()
                    .getReference("comments")
                    .child(mPostKey)
                    .push()
                    .setValue(comment);
            finish();
        }
    }
}
