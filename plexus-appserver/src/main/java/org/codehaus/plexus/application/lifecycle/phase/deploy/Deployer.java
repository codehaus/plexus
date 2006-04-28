package org.codehaus.plexus.application.lifecycle.phase.deploy;

import org.codehaus.plexus.application.ApplicationServerException;

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
