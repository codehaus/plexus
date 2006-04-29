package org.codehaus.plexus.appserver.deploy;

import org.codehaus.plexus.appserver.ApplicationServerException;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public interface Deployer
{
    String ROLE = Deployer.class.getName();

    void deploy( String name, File location )
        throws ApplicationServerException;

    void redeploy( String name, File location )
        throws ApplicationServerException;

    void undeploy( String name )
        throws ApplicationServerException;

}
