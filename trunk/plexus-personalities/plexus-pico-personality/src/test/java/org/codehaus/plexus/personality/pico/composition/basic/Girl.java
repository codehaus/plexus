package org.codehaus.plexus.personality.pico.composition.basic;


public class Girl implements Kisser
{
    Kissable kissable;

    public Girl( Kissable kissable )
    {
        this.kissable = kissable;
    }

    public void kissSomeone()
    {
        kissable.kiss( this );
    }
}