package org.codehaus.plexus.pipeline;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ValveExceptionHandler
{
    /**
     * @param request The request that the failing valve was given.
     * @param throwable The Throwable the valve threw.
     * @return Optional return value. If a return code is returned it will override the configured default in the executor.
     */
    ValveReturnCode handleValveException( ValveRequest request, Throwable throwable );
}
