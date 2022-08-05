package com.example.homesecurity.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homesecurity.R;


public class CamerasFragment extends Fragment {

    ImageButton livingroom;
    ImageButton kitchen;
    ImageButton bedroom;
    ImageButton storage;

    public static CamerasFragment newInstance(){
        CamerasFragment live_frag = new CamerasFragment();
        return live_frag;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cameras, container, false);

        //Zoomed-in view of camera
        //(different content depending on what was clicked)
        final ImageView Zoomed_in = (ImageView) root.findViewById(R.id.zoomed_in);
        Zoomed_in.setClickable(false);
        Zoomed_in.setVisibility(View.INVISIBLE);

        //Image Buttons and Listeners for each camera

        livingroom = (ImageButton) root.findViewById(R.id.livingroom_img);
        livingroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Zoomed_in.setImageResource(R.drawable.livingroom);
                Enlarge(Zoomed_in);
            }
        });

        kitchen = (ImageButton) root.findViewById(R.id.kitchen_img);
        kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Zoomed_in.setImageResource(R.drawable.kitchen);
                Enlarge(Zoomed_in);
            }
        });

        bedroom = (ImageButton) root.findViewById(R.id.bedroom_img);
        bedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Zoomed_in.setImageResource(R.drawable.bedroom);
                Enlarge(Zoomed_in);
            }
        });

        storage = (ImageButton) root.findViewById(R.id.storage_img);
        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Zoomed_in.setImageResource(R.drawable.storage);
                Enlarge(Zoomed_in);
            }
        });


        //Click Listener of zoomed-in image (to close it)
        Zoomed_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Restore image
                Zoomed_in.setVisibility(View.INVISIBLE);
                Zoomed_in.setClickable(false);
            }
        });

        return root;
    }


    private void Enlarge (ImageView image)
    {
        image.setVisibility(View.VISIBLE);
        image.setClickable(true);
    }
}