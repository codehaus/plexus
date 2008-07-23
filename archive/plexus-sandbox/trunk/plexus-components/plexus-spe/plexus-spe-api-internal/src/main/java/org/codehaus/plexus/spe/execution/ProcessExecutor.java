package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessExecutor
{
    String ROLE = ProcessExecutor.class.getName();

    void startProcess( ProcessDescriptor process, ProcessInstance state )
        throws ProcessException;
}
