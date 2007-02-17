package org.codehaus.plexus.spe.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface RemoteInvocationHandler
    extends Remote
{
    public Object invoke( String methodName, Class[] types, Object[] args )
        throws RemoteException;
}
