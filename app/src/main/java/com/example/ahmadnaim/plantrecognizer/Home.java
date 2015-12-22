package com.example.ahmadnaim.plantrecognizer;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {


    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentActivity home = (FragmentActivity) super.getActivity();

        RelativeLayout relative    = (RelativeLayout)    inflater.inflate(R.layout.fragment_home, container, false);

        relative.findViewById(R.id.homelayout);
        return relative;
    }


}

