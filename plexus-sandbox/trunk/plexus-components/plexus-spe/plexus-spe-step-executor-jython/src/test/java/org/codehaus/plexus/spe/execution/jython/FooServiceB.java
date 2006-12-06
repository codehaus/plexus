package org.codehaus.plexus.spe.execution.jython;

/**
 * @plexus.component role-hint="test-a"
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class FooServiceB
    implements FooService
{
    public static boolean beenTouched;

    public void touch()
    {
        System.out.println( "FooServiceB.touch" );
        beenTouched = true;
    }
}
