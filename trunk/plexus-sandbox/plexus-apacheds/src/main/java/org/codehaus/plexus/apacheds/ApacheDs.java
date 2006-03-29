package org.codehaus.plexus.apacheds;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ApacheDs
{
    String ROLE = ApacheDs.class.getName();

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    void setBasedir( File basedir );

    void setEnableNetworking( boolean enableNetworking );

    void addPartition( Partition partition )
        throws NamingException;

    // ----------------------------------------------------------------------
    // Server control
    // ----------------------------------------------------------------------

    void startServer()
        throws Exception;

    void stopServer()
        throws Exception;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    InitialDirContext getAdminContext()
        throws NamingException;

    InitialDirContext getSystemContext()
        throws NamingException;
}
