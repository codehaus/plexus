package org.codehaus.plexus.spe;

import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.ProcessException;

import java.net.URL;
import java.util.Map;
import java.io.Serializable;

/**
 * Facade service for the entire process system.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessService
{
    String ROLE = ProcessService.class.getName();

    ProcessDescriptor loadProcess( URL url )
        throws ProcessException;

    int executeProcess( String processId, Map<String, Serializable> context )
        throws ProcessException;

    boolean hasCompleted( int processId )
        throws ProcessException;
}
