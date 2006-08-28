package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.StepDescriptor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface StepExecutor
{
    String ROLE = StepExecutor.class.getName();

    void execute( StepDescriptor stepDescriptor, Map<String, Serializable> context )
        throws ProcessException;
}
