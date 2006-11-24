package org.codehaus.plexus.rmi.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface MyRemoteService
    extends Remote
{
    String ROLE = MyRemoteService.class.getName();

    String partyTime() throws RemoteException;
}
