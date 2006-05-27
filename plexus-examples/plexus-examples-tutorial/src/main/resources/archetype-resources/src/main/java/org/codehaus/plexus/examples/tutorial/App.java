package org.codehaus.plexus.examples.tutorial;

import org.codehaus.plexus.embed.Embedder;

public class App 
{
    public static void main( String[] args )
        throws Exception
    {
        Embedder embedder = new Embedder();
        embedder.start();

        Cheese cheese = (Cheese) embedder.lookup( Cheese.ROLE, "parmesan" );
        System.out.println( "Parmesan is " + cheese.getAroma() );
    }
}
