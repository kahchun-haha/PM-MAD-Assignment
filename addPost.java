package com.example.horapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import java.util.Objects;
import java.util.Calendar;
import java.util.Date;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addPost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addPost extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Button addButton;
    private EditText topic;
    private EditText description;
    private String mParam2;
private ImageView back;
    public addPost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addPost.
     */
    // TODO: Rename and change types and number of parameters
    public static addPost newInstance(String param1, String param2) {
        addPost fragment = new addPost();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("fragment onAttach()");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        System.out.println("fragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("fragment onCreateView()");
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        System.out.println("fragment onViewCreated()");
        back = view.findViewById(R.id.back_in_addPost);
        addButton = view.findViewById(R.id.add_lo);
        topic = view.findViewById(R.id.topic_header);
        description = view.findViewById(R.id.content);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();

                back.setImageAlpha(100);
//                Intent i = new Intent(getActivity(), Forum.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);


            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(topic.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Please enter the topic",Toast.LENGTH_SHORT).show();
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
                                DatabaseReference parent = mDatabase.child("post").push().getRef();
                                parent.child("user").setValue(currentUser);
                                parent.child("topic").setValue(topic.getText().toString());
                                parent.child("description").setValue(description.getText().toString());

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.add_post).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.navigationBar_forum).setVisibility(View.VISIBLE);
        System.out.println("fragment onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("fragment onDetach()");
    }
}