package com.example.horapp;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView username;
    private TextView email;
    private ImageView back;
private TextView imageName;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        username = view.findViewById(R.id.nameUser);
        email = view.findViewById(R.id.emailUser);
        imageName = view.findViewById(R.id.nameImage);

        Shader textShader_2 = new LinearGradient(imageName.getPaint().measureText(imageName.getText().toString()), imageName.getTextSize(),0,0,
                new int[]{
                        Color.parseColor("#FF000000"),
                        Color.parseColor("#FFFFFFFF"),
                }, new float[]{0,1}, Shader.TileMode.CLAMP);
        imageName.getPaint().setShader(textShader_2);
        imageName.setTextColor(Color.argb(255,0,0,0));

        Bundle bundle = getArguments();
        username.setText(bundle.getString("name"));
        email.setText(bundle.getString("email"));
        imageName.setText(bundle.getString("name").substring(0,2));

        back = view.findViewById(R.id.back_in_profilePage);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
                View rootView = getActivity().findViewById(R.id.rootLo);
                ((Dashboard)getActivity()).enableViews(rootView);
                getActivity().findViewById(R.id.navigationBar).setVisibility(View.VISIBLE);
                back.setImageAlpha(50);
            }
        });

    }
}