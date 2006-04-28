package org.codehaus.plexus.appserver.lifecycle.phase.deploy;

import org.codehaus.plexus.appserver.ApplicationServerException;

/**
 * @author Jason van Zyl
 */
public interface Deployer
{
    String ROLE = Deployer.class.getName();

    void deploy( String name, String location )
        throws ApplicationServerException;

    void redeploy( String name, String location )
        throws ApplicationServerException;

    void undeploy( String name )
        throws ApplicationServerException;

}
