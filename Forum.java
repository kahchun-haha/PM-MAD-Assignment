package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Forum extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentContainerView fragmentContainerView;
    private ImageView addPost_button;
    private ListView forumList;
    private BottomNavigationView bottomNavigationView;
    private ForumAdapter forumAdapter;
    private ArrayList<ForumPost> list;
    private ValueEventListener forumListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        System.out.println("Forum onCreate");
        fragmentManager = getSupportFragmentManager();
        addPost_button = findViewById(R.id.add_post);

        bottomNavigationView = findViewById(R.id.navigationBar_forum);

        list = new ArrayList<>();

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");

        forumListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot post : snapshot.child("post").getChildren()){
                    if(!post.getKey().equals("Email")){

                        String username = post.child("user").getValue(String.class);
                        String topic = post.child("topic").getValue(String.class);
                        String description = post.child("description").getValue(String.class);
                        list.add(new ForumPost(username,topic,description));
                    }
                }
                forumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDatabase.addValueEventListener(forumListener);

        addPost_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPost_button.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.myFragment, new addPost(),"addPost_fragment");
                ft.addToBackStack("bye");
                ft.commit();
            }
        });

        forumAdapter = new ForumAdapter(this,R.layout.post_item,list);

        forumList = findViewById(R.id.postList);
        forumList.setAdapter(forumAdapter);

        forumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent x = new Intent(Forum.this, EachPost.class);
                String username = forumAdapter.getItem(i).getUsername();
                String topic = forumAdapter.getItem(i).getTopic();
                String description = forumAdapter.getItem(i).getDescription();
                x.putExtra("username",username);
                x.putExtra("topic",topic);
                x.putExtra("description",description);
                startActivity(x);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Forum onResume");
//        addPost_button.setVisibility(View.VISIBLE);
//        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Forum onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Forum onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Forum onStop");
    }

}

class ForumAdapter extends ArrayAdapter<ForumPost>{
    private int resourceID;
    private ArrayList<ForumPost> list;
    public ForumAdapter(@NonNull Context context, int resourceID, ArrayList<ForumPost> list) {
        super(context, resourceID,list);
        this.resourceID = resourceID;
        this.list = list;
    }

    @Nullable
    @Override
    public ForumPost getItem(int position) {
        return super.getItem(getCount()-1-position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ForumPost forumPost = getItem(position);
        TextView profilePicName;
        TextView username;
        TextView topic;
        TextView description;
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            profilePicName = convertView.findViewById(R.id.forum_profilePic);
            username = convertView.findViewById(R.id.forum_username);
            topic = convertView.findViewById(R.id.myTopic);
            description = convertView.findViewById(R.id.myDescription);
            profilePicName.setText(forumPost.getUsername().substring(0,2));
            username.setText(forumPost.getUsername());
            topic.setText(forumPost.getTopic());
            description.setText(forumPost.getDescription());
        }
        else{
            profilePicName = convertView.findViewById(R.id.forum_profilePic);
            username = convertView.findViewById(R.id.forum_username);
            topic = convertView.findViewById(R.id.myTopic);
            description = convertView.findViewById(R.id.myDescription);
            profilePicName.setText(forumPost.getUsername().substring(0,2));
            username.setText(forumPost.getUsername());
            topic.setText(forumPost.getTopic());
            description.setText(forumPost.getDescription());
        }
        return convertView;
    }
}



class ForumPost{
    private String username;
    private String topic;
    private String description;

    public ForumPost(String username, String topic, String description){
        this.username = username;
        this.topic = topic;
        this.description = description;
    }

    public String getUsername(){
        return username;
    }

    public String getTopic(){
        return topic;
    }

    public String getDescription(){
        return description;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public void setDescription(String description){
        this.description = description;
    }

}
