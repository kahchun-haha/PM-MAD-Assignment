package com.example.horapp;
 
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import kotlin.jvm.internal.Lambda;

public class MyJournal extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Note> list;

    private DatabaseReference mDatabase;
    private JournalAdapter journalAdapter;
    private ValueEventListener journalListener;
    private ImageView add;
    private TextView journalWord;
    private ImageView backButton;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journal);

        journalWord = findViewById(R.id.journal_pageTitle);
        Shader textShader = new LinearGradient(0, 0,journalWord.getPaint().measureText(journalWord.getText().toString()),journalWord.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFFFFFF"),
                        Color.parseColor("#6289D6")
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        journalWord.getPaint().setShader(textShader);
        journalWord.setTextColor(Color.argb(255,0,0,0));

        fragmentManager = getSupportFragmentManager();

        add = findViewById(R.id.add_note);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.journalFragment,new addNote(),"random");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        backButton = findViewById(R.id.back_in_journal);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyJournal.this,Dashboard.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(i);
                backButton.setImageAlpha(50);
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
       // linearLayoutManager.setReverseLayout(true);
       // linearLayoutManager.setStackFromEnd(false);

        recyclerView = findViewById(R.id.firstRecyclerView);



        list = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

       journalListener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               list.clear();
               for(DataSnapshot username : snapshot.getChildren()){
                   String currentUser = username.getKey();
                   if(username.child("Email").getValue().equals(currentUserEmail)){
                        for(DataSnapshot note: username.child("Journal").getChildren()){
                            String date = note.child("Date").getValue(String.class);
                            String description = note.child("Description").getValue(String.class);
                            list.add(new Note(date,description));
                        }

                        break;
                   }
               }
               journalAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };

       mDatabase.addValueEventListener(journalListener);

        journalAdapter = new JournalAdapter(list);
        recyclerView.setAdapter(journalAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

    }
}

class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateTag;
        public TextView descriptionTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTag = itemView.findViewById(R.id.note_date);
            descriptionTag = itemView.findViewById(R.id.note_content);
        }

    }

    private ArrayList<Note> list;

    public JournalAdapter(ArrayList<Note> list){
        this.list = list;
    }



    @NonNull
    @Override
    public JournalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalAdapter.ViewHolder holder, int position) {
        Note note = list.get(position);

        TextView dateView = holder.dateTag;
        TextView descriptionView = holder.descriptionTag;

        dateView.setText(note.getDate());
        descriptionView.setText(note.getDescription());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }





}





// Data Model
class Note{
    private String date;
    private String description;

    public Note(String date, String description){
        this.date = date;
        this.description = description;
    }

    public String getDate(){return date;}

    public String getDescription(){return description;}

    public void setDate(String date){this.date = date;}

    public void setDescription(String content){this.description = description;}




}


