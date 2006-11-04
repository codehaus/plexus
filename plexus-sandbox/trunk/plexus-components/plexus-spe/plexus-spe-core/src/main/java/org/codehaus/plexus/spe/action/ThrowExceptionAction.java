package org.codehaus.plexus.spe.action;

import org.codehaus.plexus.action.AbstractAction;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ThrowExceptionAction
    extends AbstractAction
{
    private String message;

    private boolean runtime;

    public void execute( Map context )
        throws Exception
    {
        String m = this.message;

        if ( m == null )
        {
            m = (String) context.get( "message" );
        }

        if ( runtime )
        {
            throw new RuntimeException( m );
        }
        else
        {
            throw new Exception( m );
        }
    }
}
