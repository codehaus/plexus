package org.codehaus.plexus.spe;

import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

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

    Collection<ProcessDescriptor> loadProcessDirectory( File directory )
        throws ProcessException;

    int executeProcess( String processId, Map<String, Serializable> context )
        throws ProcessException;

    boolean hasCompleted( int processId )
        throws ProcessException;

    ProcessInstance getProcessInstance( int instanceId )
        throws ProcessException;
}
