package com.example.horapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.PushbackReader;
import java.util.ArrayList;

public class EachPost extends AppCompatActivity {

    private TextView topic;
    private TextView description;
    private TextView username;
    private ListView commentList;
    private ArrayList<Comment> list;
    private TextView profileName;
    private CommentAdapter commentAdapter;
private ValueEventListener commentListener;
    private EditText commentInput;
    private DatabaseReference mDatabase;
    private DatabaseReference xDatabase;
    private ImageView sendComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_post);
        Intent x = getIntent();
        Bundle bundle = x.getExtras();

        list = new ArrayList<>();

        topic = findViewById(R.id.post_Topic);
        description = findViewById(R.id.post_Description);
        username = findViewById(R.id.post_username);
        profileName = findViewById(R.id.post_profilePic);

        topic.setText(bundle.getString("topic"));
        description.setText(bundle.getString("description"));
        username.setText(bundle.getString("username"));
        profileName.setText(bundle.getString("username").substring(0,2));

        commentInput = findViewById(R.id.comment_input);
        sendComment = findViewById(R.id.send_comment);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!commentInput.getText().toString().isEmpty()){
                sendComment.setImageAlpha(100);
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot name : snapshot.getChildren()){
                            String currentUser = name.getKey();
                            if(name.child("Email").getValue(String.class).equals(currentUserEmail)){
                                for(DataSnapshot post : snapshot.child("post").getChildren()){
                                    if(!post.getKey().equals("Email")){
                                        if(post.child("description").getValue(String.class).equals(bundle.getString("description"))){
                                            if(post.child("topic").getValue(String.class).equals(bundle.getString("topic"))){
                                                if(post.child("user").getValue(String.class).equals(bundle.getString("username"))){
                                                    System.out.println(222);
                                                    DatabaseReference atPost =  post.getRef().child("comment").push().getRef();
                                                    atPost.child("username").setValue(currentUser);
                                                    atPost.child("comment").setValue(commentInput.getText().toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        commentInput.getText().clear();
                        sendComment.setImageAlpha(255);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });}
                else{
                    Toast.makeText(getApplicationContext(),"Please enter text",Toast.LENGTH_SHORT).show();
                }
            }
        });

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        xDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");


        commentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot post: snapshot.child("post").getChildren()){
                    if(!post.getKey().equals("Email")){
                        if(post.child("description").getValue(String.class).equals(bundle.getString("description"))){
                            if(post.child("topic").getValue(String.class).equals(bundle.getString("topic"))){
                                if(post.child("user").getValue(String.class).equals(bundle.getString("username"))){
                                    for(DataSnapshot comment : post.child("comment").getChildren()){
                                        list.add(new Comment(comment.child("username").getValue(String.class),comment.child("comment").getValue(String.class)));
                                    }
                                }
                            }
                        }
                    }


                }
                commentAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        xDatabase.addValueEventListener(commentListener);

        commentAdapter = new CommentAdapter(this,R.layout.comment,list);
        commentList = findViewById(R.id.commentList);
        commentList.setAdapter(commentAdapter);

    }

    @Override
    protected void onDestroy() {
        xDatabase.removeEventListener(commentListener);
        super.onDestroy();

    }
}

class CommentAdapter extends ArrayAdapter<Comment>{
    private int resourceID;
    private ArrayList<Comment> list;
    public CommentAdapter(@NonNull Context context, int resourceID, ArrayList<Comment> list) {
        super(context, resourceID, list);
        this.resourceID = resourceID;
        this.list = list;
    }

    @Nullable
    @Override
    public Comment getItem(int position) {
        return super.getItem(getCount()-1-position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Comment comment = getItem(position);
        TextView username;
        TextView content;
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            username = convertView.findViewById(R.id.comment_name);
            content = convertView.findViewById(R.id.comment_content);
            username.setText(comment.getUsername());
            content.setText(comment.getComment());
        }
        else{
            username = convertView.findViewById(R.id.comment_name);
            content = convertView.findViewById(R.id.comment_content);
            username.setText(comment.getUsername());
            content.setText(comment.getComment());
        }
        return convertView;
    }
}


class Comment{
    private String username;
    private String comment;

    public Comment(String username, String comment){
            this.username = username;
            this.comment = comment;
    }

    public String getUsername(){
        return username;
    }

    public String getComment(){
        return comment;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

}
