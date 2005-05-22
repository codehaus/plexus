package org.codehaus.plexus.summit.exception;

/**
 * This is a base class of runtime exeptions thrown by Summit.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class SummitRuntimeException
    extends Exception
{
    /**
     * @param message   Exception message.
     * @param throwable
     */
    public SummitRuntimeException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
