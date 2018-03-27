package com.example.hoang_000.carexp1.Model;



/**
 * Created by hoang_000 on 08/03/2018.
 */

public class Geometry {
    private Viewport viewport;

    private Location1 location;

    public Viewport getViewport ()
    {
        return viewport;
    }

    public void setViewport (Viewport viewport)
    {
        this.viewport = viewport;
    }

    public Location1 getLocation1 ()
    {
        return location;
    }

    public void setLocation (Location1 location)
    {
        this.location = location;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [viewport = "+viewport+", location = "+location+"]";
    }
}
