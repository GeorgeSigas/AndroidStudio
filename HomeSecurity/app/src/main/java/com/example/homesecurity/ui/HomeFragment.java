package com.example.homesecurity.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homesecurity.Profile;
import com.example.homesecurity.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Integer.valueOf;

public class HomeFragment extends Fragment{

    ImageButton onoff;
    Switch mode;
    Spinner prof_choice;
    ImageButton emergency;
    TextView event_log;
    Dialog emer_popup;

    boolean button_active;
    int selected_profile = 0;

    private ArrayList<Profile> Active_Profiles;

    public static HomeFragment newInstance(){
        HomeFragment home_frag = new HomeFragment();
        return home_frag;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //INITIALIZING Buttons and Values

        //on/off button
        onoff = (ImageButton) root.findViewById(R.id.imageButton);
        button_active = false;
        onoff.setImageResource(R.drawable.power_off);

        //quiet mode switch
        mode = root.findViewById(R.id.switch1);//only active when button is on
        mode.setChecked(false);
        mode.setTextColor(Color.LTGRAY);

        //drop-down menu (spinner)
        prof_choice = (Spinner) root.findViewById(R.id.prof_choice);
        Active_Profiles = read_file();
        setSpinnerAdapter(Active_Profiles);

        //emergency button
        emergency = (ImageButton) root.findViewById(R.id.emergency_button);

        //emergency pop-up
        emer_popup = new Dialog(root.getContext());
        emer_popup.setContentView(R.layout.popup_emergency);

        //log to display events
        event_log = (TextView) root.findViewById(R.id.event_log);
        event_log.setText("");
        event_log.setTextColor(Color.BLACK);


        setUpEmergencyPopUp(emer_popup);


        setUpListeners();


        return root;
    }

    private void setUpListeners()
    {
        //LISTENERS
        //Switch
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!mode.isChecked())
                {
                    mode.setTextColor(Color.GRAY);
                }
                else {
                    mode.setTextColor(Color.BLACK);
                }
            }
        });
        mode.setClickable(false); //initializing as not clickable


        //Button
        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                button_active = !button_active;

                //change image
                if (button_active)
                {
                    onoff.setImageResource(R.drawable.power_on);

                    String text = "Alarm Activated.";
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                    mode.setClickable(true);
                    mode.setTextColor(Color.GRAY);
                }
                else
                {
                    onoff.setImageResource(R.drawable.power_off);

                    String text = "Alarm Deactivated.";
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                    mode.setClickable(false);
                    mode.setChecked(false);
                    mode.setTextColor(Color.LTGRAY);
                }
            }
        });


        //Spinner
        prof_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_profile = position;//remember selected item
                String text = parent.getItemAtPosition(position).toString() + " selected.";
                Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Emergency button
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Show emergency pop-up
                emer_popup.show();
            }
        });
    }

    private void setUpEmergencyPopUp(final Dialog emer_popup)
    {
        TextView title = (TextView)  emer_popup.findViewById(R.id.emer_title);
        final TextView message = (TextView) emer_popup.findViewById(R.id.emer_message);

        ImageButton police = (ImageButton) emer_popup.findViewById(R.id.police_button);
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setTextColor(Color.WHITE);
                message.setText("Calling Police...");
            }
        });

        ImageButton siren = (ImageButton) emer_popup.findViewById(R.id.siren_button);
        siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setTextColor(Color.WHITE);
                message.setText("Activating Siren...");
            }
        });

        ImageButton fire = (ImageButton) emer_popup.findViewById(R.id.fire_button);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setTextColor(Color.WHITE);
                message.setText("Calling Fire Department...");
            }
        });

        ImageButton close = (ImageButton) emer_popup.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setText("Select Action");
                message.setTextColor(Color.parseColor("#FFC107"));

                emer_popup.dismiss();
            }
        });
    }


    //updates Spinner's contents
    public void setSpinnerAdapter(ArrayList<Profile> actives)
    {
        String[] profiles = new String[actives.size()];
        for (int i = 0; i < actives.size(); i++) {
            profiles[i] = actives.get(i).getName();
        }
        ArrayAdapter<String> adap = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, profiles);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prof_choice.setAdapter(adap);
    }


    public ArrayList<Profile> read_file()
    {
        ArrayList<Profile> return_data = new ArrayList<Profile>();

        Profile the_new;
        StringTokenizer strtok;

        try {
            InputStream input = getContext().openFileInput("Saved_profiles.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line;
            while( (line=br.readLine()) != null)
            {
                if(line.startsWith("#"))
                {
                    the_new = new Profile();

                    //read Profile name
                    line = br.readLine();
                    the_new.setName(line);

                    //read profile's active rooms
                    line =br.readLine();
                    strtok=new StringTokenizer(line,",");

                    while(strtok.hasMoreTokens())
                    {
                        the_new.addActive( valueOf(strtok.nextToken()) );
                    }

                    return_data.add(the_new);
                }
                else
                {
                    return null;
                }
            }

            return return_data;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return return_data;
    }
}