package org.codehaus.plexus.pipeline;

import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component role-hint="foo-2"
 */
public class Foo2Valve
    implements Valve
{
    public ValveReturnCode invoke( Map context )
        throws Exception
    {
        Recorder.records.add( "foo-2" );

        return PROCEED;
    }
}
