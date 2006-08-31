package org.codehaus.plexus.rmi;

import java.rmi.registry.Registry;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface RmiService {
    String ROLE = RmiService.class.getName();

    Registry getRegistry() throws RmiServiceException;
}
