package org.codehaus.plexus.spe.execution.jython;

/**
 * @plexus.component role-hint="jython-a"
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AFooService
    implements FooService
{
    public static boolean beenTouched;
}
