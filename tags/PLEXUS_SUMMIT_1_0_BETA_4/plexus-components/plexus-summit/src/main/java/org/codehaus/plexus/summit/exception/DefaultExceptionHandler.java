package org.codehaus.plexus.summit.exception;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Default <code>ExceptionHandler</code> implementation.
 */
public class DefaultExceptionHandler
    extends AbstractSummitComponent
    implements ExceptionHandler
{
    /**
     * @see ExceptionHandler#handleException
     */
    public void handleException( RunData data, Throwable throwable )
        throws Exception
    {
        Resolver resolver = (Resolver) lookup( Resolver.ROLE );
        data.setTarget( resolver.getErrorView() );
        data.getMap().put( SummitConstants.STACK_TRACE, org.codehaus.plexus.util.ExceptionUtils.getStackTrace( throwable ) );
    }
}
