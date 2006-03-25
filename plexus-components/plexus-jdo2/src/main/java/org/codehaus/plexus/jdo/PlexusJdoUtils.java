package org.codehaus.plexus.jdo;

import javax.jdo.Transaction;
import javax.jdo.PersistenceManager;
import javax.jdo.JDOUserException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusJdoUtils
{
    public static void rollbackIfActive( Transaction tx )
    {
        PersistenceManager pm = tx.getPersistenceManager();

        try
        {
            if ( tx.isActive() )
            {
                tx.rollback();
            }
        }
        finally
        {
            closePersistenceManager( pm );
        }
    }

    public static void closePersistenceManager( PersistenceManager pm )
    {
        try
        {
            pm.close();
        }
        catch ( JDOUserException e )
        {
            // ignore
        }
    }
}
