package com.example.horapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addNote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addNote extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText date;
    private EditText description;

    private ImageView back;

    private Button addBtn;
    private DatabaseReference mDatabase;

    public addNote() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addNote.
     */
    // TODO: Rename and change types and number of parameters
    public static addNote newInstance(String param1, String param2) {
        addNote fragment = new addNote();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
          back = view.findViewById(R.id.back_in_addNote);
         date = view.findViewById(R.id.whenNote);
        description = view.findViewById(R.id.whatNote);
        addBtn = view.findViewById(R.id.submitNote);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Please enter the date",Toast.LENGTH_SHORT).show();
                    return;
                }

                if( description.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://horapp-68eec-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot username: snapshot.getChildren()){
                            String currentUser = username.getKey();
                            if(username.child("Email").getValue(String.class).equals(currentUserEmail)){
                                DatabaseReference parent = mDatabase.child(currentUser).child("Journal").push();
                                parent.child("Date").setValue(date.getText().toString());
                                parent.child("Description").setValue(description.getText().toString());

                                getActivity().getSupportFragmentManager().popBackStack();

                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}