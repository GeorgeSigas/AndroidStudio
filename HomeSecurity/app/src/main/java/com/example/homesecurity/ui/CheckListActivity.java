package com.example.homesecurity.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesecurity.Profile;
import com.example.homesecurity.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.homesecurity.ui.ProfilesFragment.REQUEST_KEY_EDIT_PROF;
import static com.example.homesecurity.ui.ProfilesFragment.REQUEST_KEY_NEW_PROF;

public class CheckListActivity extends AppCompatActivity {

    int[] selectedItems; //values 0 or 1 (selected or not)

    EditText Prof_Name;
    ListView cl_1;

    boolean edit = false; //flag for save button listener
    Profile prof_to_edit; //when we are editing a profile

    int pos; //position of profile to edit in profile fragment's list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        Prof_Name = findViewById(R.id.prof_name);

        cl_1 = (ListView) findViewById(R.id.checklist1);
        cl_1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String[]items = { "Protected Rooms", "    Living Room", "    Kitchen",  "    Bedroom", "    Storage Room" };
        selectedItems = new int[items.length];
        for(int i= 0; i<selectedItems.length;i++) selectedItems[i]=0; //all unchecked

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, items);
        cl_1.setAdapter(adapter1);

        //Get Extras (for edit or delete profile)
        if(getIntent().getExtras() != null)
        {
            edit = true;

            prof_to_edit = (Profile) getIntent().getExtras().getSerializable(REQUEST_KEY_EDIT_PROF);
            pos = getIntent().getExtras().getInt("pos");

            //Set edit text (current name)
            Prof_Name.setText(prof_to_edit.getName());

            //set list selected items
            for(int i : prof_to_edit.getActive())
            {
                cl_1.setItemChecked(i, true);
                selectedItems[i] = 1;
            }
        }

        //Simulation of Select-All
        cl_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //uncheck
                if (selectedItems[position] == 1)
                {
                    selectedItems[position] = 0;

                    // If header is un-checked, un-check all items underneath
                    if(position == 0)
                    {
                        for(int i=0;i <selectedItems.length; i++)
                        {
                            selectedItems[i] = 0;
                            cl_1.setItemChecked(i, false);
                        }
                    }
                    else
                    {
                        //un-check header
                        selectedItems[0] = 0;
                        cl_1.setItemChecked(0,false);
                    }
                }
                //check
                else
                {
                    selectedItems[position] = 1;

                    // If header is checked, check all items underneath
                    if(position == 0)
                    {
                        for(int i=0;i <selectedItems.length; i++)
                        {
                            selectedItems[i] = 1;
                            cl_1.setItemChecked(i, true);
                        }
                    }

                    //if all selected, also select header
                    boolean select_header = true;
                    for(int i=1; i<selectedItems.length; i++)
                    {
                        if(selectedItems[i] != 1)
                        {
                            select_header = false;
                            break;
                        }
                    }
                    if(select_header)
                    {
                        selectedItems[0] = 1;
                        cl_1.setItemChecked(0,true);
                    }
                }
            }
        });//OnItemClickEND


        Button Save = (Button) findViewById(R.id.SaveButton);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating ArrayList from selectedItems[]
                ArrayList<Integer> new_actives = new ArrayList<Integer>();
                for(int i=0; i<selectedItems.length;i++)
                {
                    if (selectedItems[i] == 1){
                        new_actives.add(i);
                        System.out.println(i);
                    }
                }

                //Save only if user has entered name for profile
                if( ! Prof_Name.getText().toString().isEmpty() )
                {
                    if(edit)
                    {
                        //update profile's name
                        prof_to_edit.setName(Prof_Name.getText().toString());

                        //update profile's selected items
                        prof_to_edit.setActive(new_actives);

                        //return profile
                        Intent intent = new Intent();
                        intent.putExtra(REQUEST_KEY_EDIT_PROF, prof_to_edit);
                        intent.putExtra("pos", pos);
                        setResult(Activity.RESULT_OK, intent);

                        finish();
                    }
                    else
                    {
                        //create new Profile
                        Profile NewProf = new Profile(Prof_Name.getText().toString());
                        NewProf.setActive(new_actives);

                        //return the new Profile
                        Intent intent = new Intent();
                        intent.putExtra(REQUEST_KEY_NEW_PROF, NewProf);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
                else
                {
                    //Display error on EditText
                    Prof_Name.setError("You must enter a profile name");
                    return;
                }

                finish();
            }
        });//Save Listener END

        FloatingActionButton Delete = (FloatingActionButton) findViewById(R.id.Delete_Button);
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (edit)
                {
                    Intent intent = new Intent();
                    intent.putExtra(REQUEST_KEY_EDIT_PROF, (Profile)null);
                    intent.putExtra("pos", pos);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else
                {
                    finish();
                }

            }
        });

    }//onCreate END
}