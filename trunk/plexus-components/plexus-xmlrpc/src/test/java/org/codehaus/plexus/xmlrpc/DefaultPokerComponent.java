package org.codehaus.plexus.xmlrpc;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPokerComponent
    implements PokerComponent
{
    public String poke( String name )
    {
        return "Hello World " + name + "!";
    }
}
