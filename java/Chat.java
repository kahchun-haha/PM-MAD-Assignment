package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private ImageView backButton;
    private ListView listView;
    private TextView chatBar;
    private TextView titleName;
    private ArrayList<ChatUser> chat;
    private EditText chatInput;
    private ChatAdapter chatAdapter;
    private Bundle extras;
    private ImageView sendButton;
    private Toolbar toolBar;

    private DatabaseReference mDatabase;
    private ValueEventListener chatListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        Intent intent = getIntent();
        extras = intent.getExtras();

        chat = new ArrayList<>();

        toolBar = findViewById(R.id.my_toolbar);
       setSupportActionBar(toolBar);
       toolBar.setTitle(extras.getString("otherUser"));
        titleName = toolBar.findViewById(R.id.title_name);
        titleName.setText(extras.getString("otherUser"));


        backButton = toolBar.findViewById(R.id.back_in_chat);
        backButton.setImageAlpha(255);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backButton.setImageAlpha(100);
                Intent i = new Intent(Chat.this,ChatList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });






        // To display chat history
        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat.clear();
                for (DataSnapshot userName : dataSnapshot.getChildren()) {
                    String name = userName.getKey();
                    if(userName.child("Email").getValue(String.class).equals(currentUserEmail)){
                        for(DataSnapshot chatMessages : dataSnapshot.child(name).child("Chat").child(extras.getString("otherUser")).getChildren()){
                            if(chatMessages.child("me").getValue()!=null){
                                if(chatMessages.child("me").getValue() instanceof java.lang.String)
                                    chat.add(new ChatUser(name,chatMessages.child("me").getValue(String.class),"me"));
                                else
                                    chat.add(new ChatUser(name, String.valueOf(chatMessages.child("me").child("latitude").getValue(Double.class))
                                            +" "+ String.valueOf(chatMessages.child("me").child("longitude").getValue(Double.class)),
                                            "location_me"));
                               System.out.println("me");
                            }
                            if(chatMessages.child("other").getValue()!=null){
                                if(chatMessages.child("other").getValue() instanceof java.lang.String)
                                    chat.add(new ChatUser(extras.getString("otherUser"),chatMessages.child("other").getValue(String.class),"other"));
                                else
                                    chat.add(new ChatUser(extras.getString("otherUser"), String.valueOf(chatMessages.child("other").child("latitude").getValue(Double.class))
                                            +" "+ String.valueOf(chatMessages.child("other").child("longitude").getValue(Double.class)),
                                            "location_other"));
                                System.out.println("other");
                            }
                        }
                        break;
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mDatabase.addValueEventListener(chatListener);


        chatInput = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.send_button);

        // To chat in real-time and update chat log, basically pushing new message to realtime database
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!String.valueOf(chatInput.getText()).isEmpty()){
                    sendButton.setImageAlpha(100);

                    String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userName : dataSnapshot.getChildren()) {
                            String name = userName.getKey();

                            if(userName.child("Email").getValue(String.class).equals(currentUserEmail)){
                               // chat.add(new ChatUser(name,chatInput.getText().toString(),"me"));
                               // chat.add(new ChatUser(extras.getString("otherUser"),chatInput.getText().toString(),"other"));
                                mDatabase.child(name).child("Chat").child(extras.getString("otherUser")).push().child("me").setValue(chatInput.getText().toString());
                                mDatabase.child(extras.getString("otherUser")).child("Chat").child(name).push().child("other").setValue(chatInput.getText().toString());

                                break;
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        sendButton.setImageAlpha(255);
                        chatInput.getText().clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors, if any
                    }
                });
            }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter text",Toast.LENGTH_SHORT).show();
                }
          }
        });


        chatAdapter = new ChatAdapter(this, android.R.layout.simple_list_item_1,chat);

        listView = findViewById(R.id.chat);
        listView.setAdapter(chatAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(chatListener);
        System.out.println("chat destroyed");
    }
}

class ChatAdapter extends ArrayAdapter<ChatUser>{

    // View lookup cache
    private static class ViewHolder {
        TextView username;
        TextView message;
        MapView map;
    }
    private MapView mapView;
    public static final int TYPE_ME = 0;
    public static final int TYPE_OTHER = 1;
    public static final int LOCATION_ME = 2;
    public static final int LOCATION_OTHER = 3;
    private ArrayList<ChatUser> chat;
    private int resourceID;
    public ChatAdapter(@NonNull Context context,int resourceID, ArrayList<ChatUser> chat) {
        super(context,resourceID,chat);
        this.resourceID = resourceID;
        this.chat = chat;

    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ChatUser user = getItem(position);

        TextView userName = null;
        TextView message = null;

        ViewHolder viewHolder;

        int viewType = getItemViewType(position);

        if(convertView==null){
            if(viewType==0){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.me,parent,false);
                userName = (TextView) convertView.findViewById(R.id.fromMe);
                message = (TextView) convertView.findViewById(R.id.myMessage);
                userName.setText(user.getUsername());
                message.setText(user.getMessage());
            }
            if(viewType==1){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.other,parent,false);
                userName = (TextView) convertView.findViewById(R.id.sender);
                message = (TextView) convertView.findViewById(R.id.senderMessage);
                userName.setText(user.getUsername());
                message.setText(user.getMessage());
            }
            if(viewType==2){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.me_map,parent,false);
                String coordinate = user.getMessage();
                System.out.println(coordinate);
                String[] array = coordinate.split(" ");
                double latitude = Double.parseDouble(array[0]);
                double longitude = Double.parseDouble(array[1]);
                userName = (TextView) convertView.findViewById(R.id.fromMe_map);
                mapView = convertView.findViewById(R.id.me_map);
                mapView.onCreate(null);
                mapView.onStart();
                mapView.onResume();
                mapView.getMapAsync(googleMap -> {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    // Toast.makeText(getApplicationContext(), "Come B", Toast.LENGTH_SHORT).show();
                });
                userName.setText(user.getUsername());
            }
            if(viewType==3){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.other_map,parent,false);
                String coordinate = user.getMessage();
                System.out.println(coordinate);
                String[] array = coordinate.split(" ");
                double latitude = Double.parseDouble(array[0]);
                double longitude = Double.parseDouble(array[1]);
                userName = (TextView) convertView.findViewById(R.id.sender_map);
                mapView = convertView.findViewById(R.id.other_map);
                mapView.onCreate(null);
                mapView.onStart();
                mapView.onResume();
                mapView.getMapAsync(googleMap -> {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    // Toast.makeText(getApplicationContext(), "Come B", Toast.LENGTH_SHORT).show();
                });
                userName.setText(user.getUsername());
            }
        }
        else{
            if(viewType==0){
                userName = (TextView) convertView.findViewById(R.id.fromMe);
                message = (TextView) convertView.findViewById(R.id.myMessage);
                userName.setText(user.getUsername());
                message.setText(user.getMessage());
            }
            if(viewType==1){
                userName = (TextView) convertView.findViewById(R.id.sender);
                message = (TextView) convertView.findViewById(R.id.senderMessage);
                userName.setText(user.getUsername());
                message.setText(user.getMessage());
            }
            if(viewType==2){
                String coordinate = user.getMessage();
                System.out.println("recycle"+coordinate);
                String[] array = coordinate.split(" ");
                double latitude = Double.parseDouble(array[0]);
                double longitude = Double.parseDouble(array[1]);
                userName = (TextView) convertView.findViewById(R.id.fromMe_map);
                mapView = convertView.findViewById(R.id.me_map);
                mapView.onCreate(null);
                mapView.onStart();
                mapView.onResume();
                mapView.getMapAsync(googleMap -> {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    // Toast.makeText(getApplicationContext(), "Come B", Toast.LENGTH_SHORT).show();
                });
                userName.setText(user.getUsername());

            }
            if(viewType==3){
                String coordinate = user.getMessage();
                System.out.println(coordinate);
                String[] array = coordinate.split(" ");
                double latitude = Double.parseDouble(array[0]);
                double longitude = Double.parseDouble(array[1]);
                userName = (TextView) convertView.findViewById(R.id.sender_map);
                mapView = convertView.findViewById(R.id.other_map);
                mapView.onCreate(null);
                mapView.onStart();
                mapView.onResume();
                mapView.getMapAsync(googleMap -> {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    // Toast.makeText(getApplicationContext(), "Come B", Toast.LENGTH_SHORT).show();
                });
                userName.setText(user.getUsername());
            }
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if(chat.get(position).getType().equals("me"))
            return TYPE_ME;
        else if(chat.get(position).getType().equals("other"))
            return TYPE_OTHER;
        else if(chat.get(position).getType().equals("location_me"))
            return LOCATION_ME;
        else
            return LOCATION_OTHER;
    }
}

class ChatUser implements Serializable{
    private String type;
    private String username;
    private String message;
    public ChatUser(String username, String message, String type){
        this.username = username;
        this.message = message;
        this.type = type;
    }

    public void setType(String type){this.type = type;}

    public String getType(){return type;}



    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){return message;}

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){return username;}
}
