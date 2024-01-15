package com.example.horapp;
 
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChatList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemReselectedListener  {


private BottomNavigationView bottomBar;
    private AutoCompleteTextView inputSearch;
    private ListView listView;
    private ImageView magnifyingGlass;
    private ArrayList<ChatListUser> list;

    private TextView chatWord;
    private ValueEventListener chatListener;
private ChatListAdpater chatListAdpater;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatWord = findViewById(R.id.chat_pageTitle);

        Shader textShader = new LinearGradient(0, 0,chatWord.getPaint().measureText(chatWord.getText().toString()),chatWord.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        chatWord.getPaint().setShader(textShader);
        chatWord.setTextColor(Color.argb(255,0,0,0));

        bottomBar = findViewById(R.id.chatList_navigationBar);

        bottomBar.setSelectedItemId(R.id.messaging);

        bottomBar.setOnNavigationItemSelectedListener(this);
        bottomBar.setOnNavigationItemReselectedListener(this);



        listView = findViewById(R.id.chatList);
        list = new ArrayList<>();

        magnifyingGlass = findViewById(R.id.search_in_chatList);

        magnifyingGlass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearch.setVisibility(View.VISIBLE);
            }
        });

        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot users : snapshot.getChildren()){
                    // String currentUser = users.getKey();
                    if(users.child("Email").getValue().equals(currentUserEmail)){
                        for(DataSnapshot chatPerson : users.child("Chat").getChildren()){
                            String chatPersonName = chatPerson.getKey();
                            long i = 1;
                            for(DataSnapshot last: chatPerson.getChildren()){
                                if(i==chatPerson.getChildrenCount()){   // to get the last/latest message
                                    if(last.child("me").getValue()!=null){
                                        if(last.child("me").getValue() instanceof java.lang.String)
                                            list.add(new ChatListUser(chatPersonName,last.child("me").getValue(String.class)));
                                        else
                                            list.add(new ChatListUser(chatPersonName,"Sent a map"));
                                    }
                                    if(last.child("other").getValue()!=null){
                                        if(last.child("other").getValue() instanceof java.lang.String)
                                            list.add(new ChatListUser(chatPersonName,last.child("other").getValue(String.class)));
                                        else
                                            list.add(new ChatListUser(chatPersonName,"Sent a map"));
                                    }
                                }
                                i++;
                            }
                        }
                        break;
                    }
                }
                chatListAdpater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.addValueEventListener(chatListener);

        chatListAdpater = new ChatListAdpater(this,R.layout.chat_list,list);

        listView.setAdapter(chatListAdpater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChatList.this, Chat.class);
                intent.putExtra("otherUser", chatListAdpater.getItem(i).getUsername());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        inputSearch = findViewById(R.id.inputSearch);

     //   String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChatList.this, android.R.layout.simple_list_item_1);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot userName : dataSnapshot.getChildren()) {
                    String name = userName.getKey();
                    if (!userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
                        if(!userName.getKey().equals("post") && !userName.getKey().equals("notification"))
                             adapter.add(name);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors, if any
            }
        });

        inputSearch.setThreshold(1);
        inputSearch.setAdapter(adapter);
        inputSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChatList.this, Chat.class);
                intent.putExtra("otherUser", adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
                inputSearch.setVisibility(View.INVISIBLE);
                inputSearch.getText().clear();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(chatListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ChatList.this, Dashboard.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.home){
                Intent i = new Intent(this,Dashboard.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
        }
        else if(item.getItemId()==R.id.helpline){

            Intent i = new Intent(this,Helpline.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        else if(item.getItemId()==R.id.learn) {

            Intent i = new Intent(ChatList.this, Learn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        else if(item.getItemId()==R.id.forum){
            Intent i = new Intent(ChatList.this, Forum.class);
             i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.home){


        }
        else if(item.getItemId()==R.id.messaging){
        }
    }
}

class ChatListAdpater extends ArrayAdapter<ChatListUser>{
    private int resourceID;
    private ArrayList<ChatListUser> arrayList;
    public ChatListAdpater(Context context, int resourceID, ArrayList<ChatListUser> arrayList){
        super(context,resourceID,arrayList);
        this.resourceID = resourceID;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatListUser user = getItem(position);
        TextView username;
        TextView lastMessage;
        TextView profileName;
    if(convertView==null){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_list,parent,false);
        username = (TextView) convertView.findViewById(R.id.chat_list_name);
        lastMessage = (TextView) convertView.findViewById(R.id.chat_list_lastMessage);
        profileName = (TextView) convertView.findViewById(R.id.chat_list_profile_name);
        username.setText(user.getUsername());
        lastMessage.setText(user.getLastMessage());
        profileName.setText(user.getUsername().substring(0,2));

        Shader textShader = new LinearGradient(profileName.getPaint().measureText(profileName.getText().toString()), profileName.getTextSize(),0,0,
                new int[]{
                        Color.parseColor("#FF000000"),
                        Color.parseColor("#FFFFFFFF"),
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        profileName.getPaint().setShader(textShader);
        profileName.setTextColor(Color.argb(255,0,0,0));
    }
    else{
        username = (TextView) convertView.findViewById(R.id.chat_list_name);
        lastMessage = (TextView) convertView.findViewById(R.id.chat_list_lastMessage);
        profileName = (TextView) convertView.findViewById(R.id.chat_list_profile_name);
        username.setText(user.getUsername());
        lastMessage.setText(user.getLastMessage());
        profileName.setText(user.getUsername().substring(0,2));

        Shader textShader = new LinearGradient(profileName.getPaint().measureText(profileName.getText().toString()), profileName.getTextSize(),0,0,
                new int[]{
                        Color.parseColor("#FF000000"),
                        Color.parseColor("#FFFFFFFF"),
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        profileName.getPaint().setShader(textShader);
        profileName.setTextColor(Color.argb(255,0,0,0));
    }
        return convertView;
    }
}


class ChatListUser{
    private String username;
    private String lastMessage;

    public ChatListUser(String username, String lastMessage){
        this.username = username;
        this.lastMessage = lastMessage;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }

    public String getUsername(){
        return username;
    }

    public String getLastMessage(){
        return lastMessage;
    }


}