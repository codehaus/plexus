package org.codehaus.plexus.summit.exception;

import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Interface that defines an exception handler for the Summit servlet. If a
 * throwable is thrown by the pipeline, the servlet will pass the Throwable
 * and RunData to the handleException method of the configured ExceptionHandler
 * to deal with.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 */
public interface ExceptionHandler
{
    public static final String ROLE = ExceptionHandler.class.getName();

    /**
     * This method will be called to handle a throwable thrown by the pipeline
     *
     * @param data      RunData for the request where the error occured
     * @param throwable The throwable that was thrown
     * @throw Exception if an error occurs handling the throwable. Turbine will
     * then attempt to provide some minimal handling.
     */
    public void handleException( RunData data, Throwable throwable )
        throws Exception;
}
