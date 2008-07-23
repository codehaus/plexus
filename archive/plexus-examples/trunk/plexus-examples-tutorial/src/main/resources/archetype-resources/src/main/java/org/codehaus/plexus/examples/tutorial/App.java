package org.codehaus.plexus.examples.tutorial;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

public class App 
{
    public static void main( String[] args )
        throws Exception
    {
        PlexusContainer container = new DefaultPlexusContainer();
        
        Cheese cheese = (Cheese) container.lookup( Cheese.ROLE, "parmesan" );
        System.out.println( "Parmesan is " + cheese.getAroma() );
        
        container.dispose();
    }
}
