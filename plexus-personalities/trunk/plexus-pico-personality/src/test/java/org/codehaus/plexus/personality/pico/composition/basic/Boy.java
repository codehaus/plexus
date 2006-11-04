package org.codehaus.plexus.personality.pico.composition.basic;

public class Boy implements Kissable
{
    public void kiss( Object kisser )
    {
        System.out.println( "I was kissed by " + kisser );
    }
}
