package com.example.mapsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity
        implements View.OnClickListener {

    ImageView mIvPicture;
    ListView mLvComments;

    String mMyName;
    String mPostKey;

    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mIvPicture = findViewById(R.id.iv_post_picture);
        mLvComments = findViewById(R.id.lv_comments);

        Bundle extras = getIntent().getExtras();

        mPostKey = extras.getString("KEY");
        mMyName = extras.getString("USER");

        GeoPicture data = (GeoPicture) extras.getSerializable("DATA");

        byte[] decodedString = Base64.decode(data.picture, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        mIvPicture.setImageBitmap(bitmap);
        mLvComments.setAdapter(new CommentsAdapter(this, mPostKey));
    }

    @Override
    public void onClick(View view) {
        if(view == mFab) {
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("KEY", mPostKey);
            intent.putExtra("USER", mMyName);
            startActivity(intent);
        }
    }
}



class CommentsAdapter extends BaseAdapter
        implements ChildEventListener {
    Context mContext;
    ArrayList<String> mCommentKeys;
    HashMap<String, CommentEntry> mComments;

    public CommentsAdapter(Context context, String postKey) {
        mContext = context;
        mCommentKeys = new ArrayList<>();
        mComments = new HashMap<>();

        FirebaseDatabase
                .getInstance()
                .getReference("comments")
                .child(postKey)
                .addChildEventListener(this);
    }

    @Override
    public int getCount() {
        return mCommentKeys.size();
    }

    @Override
    public Object getItem(int i) {
        String key = mCommentKeys.get(i);
        return mComments.get(key);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.comment, viewGroup, false);
        }

        CommentEntry item = (CommentEntry) getItem(i);

        TextView userTv = view.findViewById(R.id.comment_user);
        TextView textTv = view.findViewById(R.id.comment_text);

        userTv.setText(item.user);
        textTv.setText(item.text);

        return view;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(250);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        CommentEntry entry = dataSnapshot.getValue(CommentEntry.class);

        mCommentKeys.add(key);
        mComments.put(key, entry);
        notifyDataSetChanged();

        vibrate();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        CommentEntry entry = dataSnapshot.getValue(CommentEntry.class);

        mComments.put(key, entry);
        notifyDataSetChanged();

        vibrate();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();

        mCommentKeys.remove(key);
        mComments.remove(key);
        notifyDataSetChanged();

        vibrate();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // Do nothing
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Do nothing
    }
}