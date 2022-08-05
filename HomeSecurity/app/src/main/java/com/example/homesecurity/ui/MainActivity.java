package com.example.homesecurity.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.homesecurity.Profile;
import com.example.homesecurity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import static java.lang.Integer.valueOf;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity
{

    //Static booleans for functionality
    private static boolean FRONT_DOOR = true;
    private static boolean LIVINGROOM_BALC = true;
    private static boolean KITCHEN_BALC = true;
    private static boolean KITCHEN_WINDOW = true;
    private static boolean BEDROOM_BALC = false;
    private static boolean STORAGE_DOOR = false;

    private static boolean LIVINGROOM_SENSOR = false;
    private static boolean KITCHEN_SENSOR = false;
    private static boolean BEDROOM_SENSOR = true;
    private static boolean STORAGE_SENSOR = false;


    HomeFragment homeFragment;
    String TAG_HOME="home";

    CamerasFragment camerasFragment;
    String TAG_CAMERAS="cameras";

    ProfilesFragment profilesFragment;
    String TAG_PROFILES="profiles";

    AlertDialog alert;
    ArrayList<Integer> protected_rooms;//of current profile

    //Bottom Navigation Menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    showHomeFragment();
                    break;
                case R.id.navigation_cameras:
                    showCameraFragment();
                    break;
                case R.id.navigation_profiles:
                    showProfilesFragment();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        camerasFragment = new CamerasFragment();
        profilesFragment = new ProfilesFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showHomeFragment();

        //Create dialog for invasion
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Warning!");
        builder.setCancelable(true);

        builder.setPositiveButton("Check Cameras", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showCameraFragment();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert = builder.create();

        //start Thread for tests
        Checker c = new Checker();
        c.start();
    }

    public void showHomeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (homeFragment.isAdded())
        {
            ft.show(homeFragment);

            //update drop-down menu's list (to receive changes)
            ArrayList<Profile> actives = homeFragment.read_file();
            homeFragment.setSpinnerAdapter(actives);

            //remember selected item
            if(homeFragment.prof_choice.getAdapter().getCount() <= homeFragment.selected_profile)
            {
                homeFragment.selected_profile = 0;
            }
            homeFragment.prof_choice.setSelection(homeFragment.selected_profile);
        }
        else
        {
            ft.add(R.id.content, homeFragment, TAG_HOME);
        }

        if (camerasFragment.isAdded()) ft.hide(camerasFragment);
        if (profilesFragment.isAdded()) ft.hide(profilesFragment);

        ft.commit();
    }

    public void showCameraFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (camerasFragment.isAdded())
        {
            ft.show(camerasFragment);
        }
        else
        {
            ft.add(R.id.content, camerasFragment, TAG_CAMERAS);
        }

        if (homeFragment.isAdded()) ft.hide(homeFragment);
        if (profilesFragment.isAdded()) ft.hide(profilesFragment);

        ft.commit();
    }

    public void showProfilesFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (profilesFragment.isAdded())
        {
            ft.show(profilesFragment);
        }
        else
        {
            ft.add(R.id.content, profilesFragment, TAG_PROFILES);
        }

        if (homeFragment.isAdded()) ft.hide(homeFragment);
        if (camerasFragment.isAdded()) ft.hide(camerasFragment);

        ft.commit();
    }



    //Sub-Class Checker for functionality
    class Checker extends Thread
    {
        public void run()
        {
            int wait;

            //check values and call corresponding method
            while (true)
            {
                try {
                    //wait for 4 - 8 seconds randomly for next check
                    Random r = new Random();
                    wait = r.nextInt((8000 - 4000) + 1) + 4000;
                    Thread.sleep(wait);

                    //always notify for doors and windows
                    check_entries();

                    //notify for sensors only if alarm is active
                    if(homeFragment.button_active) check_invasion();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }//main run END


        //Check for Doors and Windows and update Log
        public void check_entries()
        {
            Date date = new Date();
            String time= DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
            time = "<"+time+">"+" ";

            if (FRONT_DOOR) {
                homeFragment.event_log.setText(homeFragment.event_log.getText()+time+"Front Door Opened\n");
                FRONT_DOOR = false;
                return;
            }
            if (LIVINGROOM_BALC) {
                homeFragment.event_log.setText(homeFragment.event_log.getText()+time+"Living Room Balcony Door Opened\n");
                LIVINGROOM_BALC = false;
                return;
            }
            if (KITCHEN_BALC) {
                homeFragment.event_log.setText(homeFragment.event_log.getText()+time+"Kitchen Balcony Door Opened\n");
                KITCHEN_BALC = false;
                return;
            }
            if (KITCHEN_WINDOW){
                homeFragment.event_log.setText(homeFragment.event_log.getText()+time+"Kitchen Window Opened\n");
                KITCHEN_WINDOW = false;
                return;
            }
            if (BEDROOM_BALC){
                homeFragment.event_log.setText(homeFragment.event_log.getText()+time+"Bedroom 1 Balcony Door Opened\n");
                BEDROOM_BALC = false;
                return;
            }
            if (STORAGE_DOOR) {
                homeFragment.event_log.setText(homeFragment.event_log.getText()+time+"Storage Door Opened\n");
                STORAGE_DOOR = false;
                return;
            }
        }//Entry Checker END


        //Check for room movement or doors/windows
        //Only checks currently protected rooms
        //Notifies user
        public void check_invasion()
        {
            //get active profile from homeFragment
            int current_profile_pos = homeFragment.prof_choice.getSelectedItemPosition();
            if(!profilesFragment.Profile_list.isEmpty()){
                protected_rooms = profilesFragment.Profile_list.get(current_profile_pos).getActive();
            }else{
                protected_rooms = new ArrayList<Integer>();
            }

            //Check Rooms 1 by 1
            if (LIVINGROOM_SENSOR || FRONT_DOOR || LIVINGROOM_BALC)
            {
                //if room is not protected in current profile return
                if(!protected_rooms.contains(1)) return;

                //Pop-up Window Over the Fragment
                //Prompting user to check cameras
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(homeFragment.mode.isChecked()) System.out.println("ACTIVATING SIREN...");

                        alert.setMessage("Invasion Detected at Living room!");
                        alert.show();
                        LIVINGROOM_BALC =false;
                        LIVINGROOM_SENSOR =false;
                        FRONT_DOOR =false;
                    }
                });

                return;
            }

            if (KITCHEN_SENSOR || KITCHEN_BALC || KITCHEN_WINDOW)
            {
                if(!protected_rooms.contains(2)) return;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(homeFragment.mode.isChecked()) System.out.println("ACTIVATING SIREN...");

                        alert.setMessage("Invasion Detected at Kitchen!");
                        alert.show();
                        KITCHEN_BALC =false;
                        KITCHEN_SENSOR =false;
                        FRONT_DOOR =false;

                    }
                });
                return;
            }

            if (BEDROOM_SENSOR || BEDROOM_BALC)
            {
                if(!protected_rooms.contains(3)) return;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(homeFragment.mode.isChecked()) System.out.println("ACTIVATING SIREN...");

                        alert.setMessage("Invasion Detected at Bedroom !");
                        alert.show();
                        BEDROOM_BALC =false;
                        BEDROOM_SENSOR =false;
                    }
                });
                return;
            }

            if (STORAGE_SENSOR || STORAGE_DOOR)
            {
                if(!protected_rooms.contains(4)) return;

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if(homeFragment.mode.isChecked()) System.out.println("ACTIVATING SIREN...");

                        alert.setMessage("Invasion Detected at Storage room!");
                        alert.show();
                        STORAGE_DOOR =false;
                        STORAGE_SENSOR =false;
                    }
                });
                return;
            }
        }//check invasion END

    }//Checker END

}//Main Activity END
