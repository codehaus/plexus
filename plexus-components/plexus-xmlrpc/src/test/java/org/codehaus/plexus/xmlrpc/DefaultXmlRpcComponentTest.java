package org.codehaus.plexus.xmlrpc;

/*
 * LICENSE
 */

import java.net.URL;
import java.util.Vector;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultXmlRpcComponentTest
    extends PlexusTestCase
{
    public void testExecution()
        throws Exception
    {
        URL url = new URL( "http://localhost:40000/rpc2" );

        XmlRpcComponent xmlRpc = (XmlRpcComponent) lookup( XmlRpcComponent.ROLE );

        Vector params = new Vector();

        params.add( "Travis" );

        Object obj = xmlRpc.executeRpc( url, "poker.poke", params);

        assertEquals( obj.getClass().getName(), String.class.getName() );

        String str = obj.toString();

        assertEquals( "Hello World Travis!", str );
    }
}
