package org.codehaus.plexus.spe.execution.jython;

/**
 * @plexus.component role-hint="testb"
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BFooService
    implements FooService
{
    public static boolean beenTouched;

    public void touch()
    {
        beenTouched = true;
    }
}
