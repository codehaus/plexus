package org.codehaus.plexus.pipeline.execution;

import org.codehaus.plexus.pipeline.PipelineRequest;
import org.codehaus.plexus.pipeline.PipelineException;
import org.codehaus.plexus.pipeline.PipelineRuntimeManager;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PipelineExecutor
{
    String ROLE = PipelineExecutor.class.getName();

    void execute( PipelineRuntimeManager runtimeManager, PipelineRequest request )
        throws PipelineException;
}
