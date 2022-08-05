package com.example.homesecurity.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homesecurity.Profile;
import com.example.homesecurity.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Integer.valueOf;

public class ProfilesFragment extends Fragment {

    private static final int REQUEST_CODE_NEW_PROF = 2392;
    static final String REQUEST_KEY_NEW_PROF = "2392";

    private static final int REQUEST_CODE_EDIT_PROF = 1456;
    static final String REQUEST_KEY_EDIT_PROF = "1456";

    ListView Profiles;
    ArrayAdapter<String> adapter;

    ArrayList<String> Profile_Names;
    ArrayList<Profile> Profile_list = new ArrayList<Profile>(); //parallel to Profile_Names

    FloatingActionButton Add;

    public static ProfilesFragment newInstance(){
        ProfilesFragment prof_frag = new ProfilesFragment();
        return prof_frag;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profiles, container, false);

        //List of Profiles
        Profiles = (ListView) root.findViewById(R.id.profile_list);

        //initialize list
        Profile_list = read_file();

        //initialize names list
        Profile_Names = new ArrayList<String>();
        for(Profile p : Profile_list)
        {
            Profile_Names.add(p.getName());
        }

        //adapter for list view
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Profile_Names);
        Profiles.setAdapter(adapter);

        //Add profile Button
        Add = (FloatingActionButton) root.findViewById(R.id.add_prof);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), CheckListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_PROF);
            }
        });

        Profiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getActivity(), CheckListActivity.class);
                intent.putExtra(REQUEST_KEY_EDIT_PROF, Profile_list.get(position) ); //Profile object
                intent.putExtra("pos", position); //position in list
                startActivityForResult(intent, REQUEST_CODE_EDIT_PROF);
            }
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Profile n_prof;

         switch(requestCode)
         {
             case REQUEST_CODE_NEW_PROF: //New Profile
                 if(resultCode == Activity.RESULT_OK)
                 {
                     //Get new profile
                     n_prof = (Profile) data.getExtras().getSerializable(REQUEST_KEY_NEW_PROF);

                     //add to lists
                     Profile_Names.add(n_prof.getName());
                     Profile_list.add(n_prof);

                     for(int i=0;i<n_prof.getActive().size();i++)
                     {
                         System.out.println(n_prof.getActiveAt(i));
                     }

                     update_file();
                 }
                 else
                 {
                     Log.i("app", "Activity canceled");
                 }
                 break;
             case REQUEST_CODE_EDIT_PROF: //Edit or Delete Profile

                 if(resultCode == Activity.RESULT_OK)
                 {
                     //get profile
                     n_prof = (Profile) data.getExtras().getSerializable(REQUEST_KEY_EDIT_PROF);
                     int pos = data.getExtras().getInt("pos");

                     //EDIT
                     if(n_prof != null)
                     {
                         //replace in lists
                         Profile_Names.set(pos, n_prof.getName());
                         Profile_list.set(pos, n_prof);

                         update_file();
                     }
                     else //DELETE
                     {
                         String text = " \"" +Profile_Names.get(pos)+"\" " + "profile removed.";
                         Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                         Profile_Names.remove(pos);
                         Profile_list.remove(pos);

                         update_file();
                     }
                 }
                 else
                 {
                     Log.i("app", "Activity canceled");
                 }
                 break;
         }

         //update list
         Profiles.setAdapter(adapter);
    }

    public void update_file()
    {
        try {
            OutputStreamWriter output = new OutputStreamWriter(getContext().openFileOutput("Saved_profiles.txt", Context.MODE_PRIVATE));
            BufferedWriter bw = new BufferedWriter(output);

            for (int i = 0; i < Profile_list.size(); i++)
            {
                Profile the_new = Profile_list.get(i);

                bw.write("#" + "\n"); //separation line
                bw.write(the_new.getName() + "\n"); //name line

                if(the_new.getActive().size() == 0) bw.write("\n"); //if active list empty

                for (int j = 0; j < the_new.getActive().size(); j++) //active rooms line
                {
                    //for the last item
                    if (j == the_new.getActive().size() - 1)
                    {
                        bw.write(Integer.toString(the_new.getActiveAt(j)) + "\n");
                    } else //for all other items
                    {
                        bw.write(Integer.toString(the_new.getActiveAt(j)) + ",");
                    }
                }//for all active rooms

            }//for all profiles

            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            }

            return return_data;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return return_data;
    }

}