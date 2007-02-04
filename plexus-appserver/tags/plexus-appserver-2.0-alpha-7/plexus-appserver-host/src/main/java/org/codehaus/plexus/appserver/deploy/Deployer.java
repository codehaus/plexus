package org.codehaus.plexus.appserver.deploy;

import org.codehaus.plexus.appserver.ApplicationServerException;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public interface Deployer
{
    String ROLE = Deployer.class.getName();

    void deploy( String id, File location )
        throws ApplicationServerException;

    void redeploy( String id )
        throws ApplicationServerException;

    void undeploy( String id )
        throws ApplicationServerException;

    boolean isDeployed( String id );
}
