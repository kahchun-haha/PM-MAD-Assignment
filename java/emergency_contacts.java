package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class emergency_contacts extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView pageTitle;
    private ValueEventListener contactListener;
    private AutoCompleteTextView searchContacts;
    private ArrayList<Contacts> list;
    private ContactsAdapter contactsAdapter;
    private ImageView addButton;
    private ImageView backButton;
    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        pageTitle = findViewById(R.id.e_contacts_pageTitle);

        Shader textShader = new LinearGradient(0, 0,pageTitle.getPaint().measureText(pageTitle.getText().toString()),pageTitle.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        pageTitle.getPaint().setShader(textShader);
        pageTitle.setTextColor(Color.argb(255,0,0,0));

        addButton = findViewById(R.id.add_in_contacts);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchContacts.setVisibility(View.VISIBLE);
                addButton.setImageAlpha(100);
            }
        });

        backButton = findViewById(R.id.back_in_contacts);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(emergency_contacts.this,Dashboard.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(i);
                backButton.setImageAlpha(50);
            }
        });

        list = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Users");
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        contactListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear the underlying list of the adapter everytime database changes, example,
                // database contains data A ->  list{A}
                // new data B added to database, database now A,B -> list{A, A, B} (without clearing)
                // notice that the list still holds the old data if we did not explicitly clear it
                list.clear();
                for(DataSnapshot username : snapshot.getChildren()){

                    // to retrieve the node at the current signed-in username
                    if(username.child("Email").getValue().equals(currentUserEmail)){
                        for(DataSnapshot e_contacts: username.child("E-Contacts").getChildren()){
                            list.add(new Contacts(e_contacts.getValue(String.class)));
                        }
                        // if we have retrieved the correct username, don't need to go through other usernames
                        break;
                    }
                }
                contactsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mDatabase.addValueEventListener(contactListener);
        contactsAdapter = new ContactsAdapter(this,R.layout.grid_item,list);
        gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(contactsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        searchContacts = findViewById(R.id.searchContacts);

        searchContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButton.setImageAlpha(255);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(emergency_contacts.this, android.R.layout.simple_list_item_1);
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
                    // adapter.add(name);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors, if any
            }
        });

        searchContacts.setThreshold(1);
        searchContacts.setAdapter(adapter);
        searchContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userName : dataSnapshot.getChildren()) {
                            String name = userName.getKey();
                            if (userName.child("Email").getValue(String.class).equals(currentUserEmail)) {
                                long count =  0;
                                for(DataSnapshot e_contacts : userName.child("E-Contacts").getChildren()){
                                    if(e_contacts.getValue(String.class).equals(adapterView.getItemAtPosition(i).toString())){
                                        Toast.makeText(getApplicationContext(),"Contact already added",Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    count++;
                                }
                                if(count == userName.child("E-Contacts").getChildrenCount())
                                    mDatabase.child(name).child("E-Contacts").push().setValue(adapterView.getItemAtPosition(i).toString());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors, if any
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(contactListener);
    }
}

class ContactsAdapter extends ArrayAdapter<Contacts>{
    private int resourceID;
    private ArrayList<Contacts> list;
    public ContactsAdapter(@NonNull Context context, int resourceID, ArrayList<Contacts> list) {
        super(context, resourceID, list);
        this.resourceID = resourceID;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contacts contact = getItem(position);
        TextView profilePicName;
        TextView username;
        ImageView deleteBtn;

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item,parent,false);
            profilePicName = (TextView) convertView.findViewById(R.id.emergency_profile_pic);
            username = (TextView) convertView.findViewById(R.id.emergency_profile_name);
            profilePicName.setText(contact.getUsername().substring(0,2));
            username.setText(contact.getUsername());
        }
        else{
            profilePicName = (TextView) convertView.findViewById(R.id.emergency_profile_pic);
            username = (TextView) convertView.findViewById(R.id.emergency_profile_name);
            profilePicName.setText(contact.getUsername().substring(0,2));
            username.setText(contact.getUsername());
        }

        deleteBtn = (ImageView) convertView.findViewById(R.id.deleteButton);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBtn.setImageAlpha(100);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot username : snapshot.getChildren()){
                            String currentUser = username.getKey();
                            System.out.println(currentUser);
                            if (username.child("Email").getValue(String.class).equals(currentUserEmail)){
                                System.out.println("2");
                                for(DataSnapshot e_contacts : username.child("E-Contacts").getChildren()){
                                    if(e_contacts.getValue(String.class).equals(contact.getUsername())){
                                        e_contacts.getRef().removeValue();
                                        System.out.println("3");
                                    }
                                }
                                break;
                            }
                        }
                        deleteBtn.setImageAlpha(255);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return convertView;
    }
}


class Contacts{
    private String username;
    public Contacts(String username){
        this.username = username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

}
