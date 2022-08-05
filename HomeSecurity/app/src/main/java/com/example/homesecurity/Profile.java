package com.example.homesecurity;

import java.io.Serializable;
import java.util.ArrayList;

public class Profile implements Serializable
{
    String Name;

    //Holds which rooms are protected on this profile
    //Corresponds to their indexes in CheckListActivity (+1)
    //e.g. 1,3,4 for living room, bedroom, storage
    ArrayList<Integer> Active;

    public Profile()
    {
        this.Name = "New Profile";
        this.Active = new ArrayList<Integer>();
    }

    public Profile(String name)
    {
        this.Name = name;
        this.Active = new ArrayList<Integer>();
    }

    public void setName(String n)
    {
        this.Name = n;
    }

    public String getName()
    {
        return Name;
    }

    public void setActive(ArrayList<Integer> a)
    {
        this.Active = a;
    }

    public ArrayList<Integer> getActive()
    {
        return Active;
    }

    public int getActiveAt(int pos)
    {
        return Active.get(pos);
    }


    public void addActive(int n)
    {
        Active.add(n);
    }
}
