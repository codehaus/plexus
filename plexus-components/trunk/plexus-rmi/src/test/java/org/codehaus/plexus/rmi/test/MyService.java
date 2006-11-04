package org.codehaus.plexus.rmi.test;

import java.rmi.Remote;
import java.io.Serializable;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface MyService
    extends Remote, Serializable
{
    String partyTime();
}
