package org.codehaus.plexus.spe.rmi;

import org.codehaus.plexus.spe.ProcessException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessEngineConnector
    extends Remote
{
    ProcessEngineConnection getConnection()
        throws RemoteException, ProcessException;
}
