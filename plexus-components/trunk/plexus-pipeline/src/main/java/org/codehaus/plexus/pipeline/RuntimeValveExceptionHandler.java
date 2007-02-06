package org.codehaus.plexus.pipeline;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class RuntimeValveExceptionHandler
    implements ValveExceptionHandler
{
    public ValveReturnCode handleValveException( ValveRequest request, Throwable throwable )
    {
        throw new RuntimeException( "Error in pipeline.", throwable );
    }
}
