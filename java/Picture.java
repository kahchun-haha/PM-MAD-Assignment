package com.example.horapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.http.ParseHttpBody;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Picture extends AppCompatActivity {
    private ArrayList<PictureMetadata> list;
    private PictureAdapter pictureAdapter;
    private ListView photoList;
    private FragmentManager fragmentManager;

    private ImageView backButton;

    private TextView photoWord;
private DatabaseReference mDatabase;
private ValueEventListener pictureListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        fragmentManager = getSupportFragmentManager();

        backButton = findViewById(R.id.back_in_Photo);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Picture.this,Dashboard.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(i);
                backButton.setImageAlpha(50);
            }
        });

        photoWord = findViewById(R.id.photo_pageTitle);
        Shader textShader = new LinearGradient(0, 0,photoWord.getPaint().measureText(photoWord.getText().toString()),photoWord.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        photoWord.getPaint().setShader(textShader);
        photoWord.setTextColor(Color.argb(255,0,0,0));

        list = new ArrayList<>();

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
       mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");

        pictureListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot username:snapshot.getChildren()){
                    String currentUser = username.getKey();
                    if(username.child("Email").getValue(String.class).equals(currentUserEmail)){
                        for(DataSnapshot images: snapshot.child(currentUser).child("Image").getChildren()){
                            list.add(new PictureMetadata(images.getValue(String.class)));
                        }
                    }
                }
                pictureAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDatabase.addListenerForSingleValueEvent(pictureListener);

        pictureAdapter = new PictureAdapter(this,R.layout.picture_holder,list);

        photoList = findViewById(R.id.pictureList);
        photoList.setAdapter(pictureAdapter);

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = pictureAdapter.getItem(i).getUrl();
                Bundle bundle = new Bundle();
                bundle.putString("url",url);

                PhotoFragment photo = new PhotoFragment();
                photo.setArguments(bundle);


                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.pictureFragment, photo, "pop");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }
}

class PictureAdapter extends ArrayAdapter<PictureMetadata>{
    private ArrayList<PictureMetadata> list;
    private int resourceID;
    public PictureAdapter(@NonNull Context context, int resourceID, ArrayList<PictureMetadata> list) {
        super(context, resourceID, list);
        this.list = list;
        this.resourceID = resourceID;
    }

    @Nullable
    @Override
    public PictureMetadata getItem(int position) {
        // return items in reverse order so that newly captured photo appears at the top of the gallery
        return super.getItem(getCount()-1-position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PictureMetadata pictureMetadata= getItem(position);
        ImageView picture;
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            picture = convertView.findViewById(R.id.justPlaceHolder);
            Picasso.get().load(pictureMetadata.getUrl()).fit().into(picture);
        }
        else{
            picture = convertView.findViewById(R.id.justPlaceHolder);
            Picasso.get().load(pictureMetadata.getUrl()).fit().into(picture);
        }
        return  convertView;
    }
}



class PictureMetadata{
    private String url;


    public PictureMetadata(String url) {
        this.url = url;
    }

    public String getUrl(){
        return  url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
