package org.codehaus.plexus;

import org.codehaus.plexus.lifecycle.phase.Eeny;
import org.codehaus.plexus.lifecycle.phase.Meeny;
import org.codehaus.plexus.lifecycle.phase.Miny;
import org.codehaus.plexus.lifecycle.phase.Mo;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/** This component implements the custom lifecycle defined by the phases
 *
 *  Eeny
 *  Meeny
 *  Miny
 *  Mo
 *
 */
public class DefaultServiceH
    extends AbstractLogEnabled
    implements ServiceH, Eeny, Meeny, Miny, Mo
{
    boolean eeny;
    boolean meeny;
    boolean miny;
    boolean mo;

    // ----------------------------------------------------------------------
    // Lifecycle Management
    // ----------------------------------------------------------------------

    public void eeny()
    {
        eeny = true;
    }

    public void meeny()
    {
        meeny = true;
    }

    public void miny()
    {
        miny = true;
    }

    public void mo()
    {
        mo = true;
    }
}
