package org.codehaus.plexus.pipeline;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component role-hint="foo-1"
 */
public class Foo1Valve
    implements Valve
{
    public ValveReturnCode invoke( ValveRequest request )
        throws Exception
    {
        Recorder.records.add( "foo-1" );

        return PROCEED;
    }
}
