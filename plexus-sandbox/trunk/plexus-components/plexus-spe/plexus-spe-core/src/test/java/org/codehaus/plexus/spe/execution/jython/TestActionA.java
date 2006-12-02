package org.codehaus.plexus.spe.execution.jython;

import org.codehaus.plexus.action.AbstractAction;

import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class TestActionA
    extends AbstractAction
{
    public static boolean beenTouched;

    public void execute( Map context )
        throws Exception
    {
    }
}
