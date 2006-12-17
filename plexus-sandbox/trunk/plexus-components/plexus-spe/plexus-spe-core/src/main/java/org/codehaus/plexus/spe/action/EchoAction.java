package org.codehaus.plexus.spe.action;

import org.codehaus.plexus.action.AbstractAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.action.Action" role-hint="echo-message"
 */
public class EchoAction
    extends AbstractAction
{
    private String message;

    public static List<String> messages = new ArrayList<String>();

    public static String lastMessage;

    public void execute( Map context )
        throws Exception
    {
        String m = this.message;

        if ( m == null )
        {
            m = (String) context.get( "message" );
        }

        getLogger().info( "Message:" +  m );
        lastMessage = m;
        messages.add( m );
    }
}
