package org.codehaus.plexus.examples.tutorial;

public class ParmesanCheese
    implements Cheese
{
    public void slice( int slices )
    {
        throw new UnsupportedOperationException( "No can do" );
    }

    public String getAroma()
    {
        return "strong";
    }
}

