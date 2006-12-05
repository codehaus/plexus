package org.codehaus.plexus.pipeline;

import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface Pipeline
{
    String ROLE = Pipeline.class.getName();

    void processMessage( Map context )
        throws PipelineException;

    void processMessage( Map context, ExceptionHandler exceptionHandler )
        throws PipelineException;

    void setTraceExecution( boolean traceExecution );
}
