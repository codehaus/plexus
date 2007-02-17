package org.codehaus.plexus.spe.rmi;

import org.codehaus.plexus.spe.ProcessService;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessEngineConnection
{
    ProcessService getProcessService();
}
