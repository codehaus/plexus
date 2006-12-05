package org.codehaus.plexus.pipeline;

import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface Valve
{
    ValveReturnCode PROCEED = new ValveReturnCode( "PROCEED" );
    ValveReturnCode STOP = new ValveReturnCode( "STOP" );

    ValveReturnCode invoke( Map context )
        throws Exception;
}
