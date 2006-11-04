package org.codehaus.plexus.apacheds;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import java.io.File;
import java.util.Set;

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

    void addPartition( String name, String root, Set indexedAttributes, Attributes partitionAttributes )
        throws NamingException;

    void addPartition( Partition partition )
        throws NamingException;

    /**
     * Creates a partition usable for testing and other light usage.
     *
     * @param domainComponents E.g. "plexus", "codehaus", "org"
     * @throws NamingException
     */
    Partition addSimplePartition( String ... domainComponents )
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
