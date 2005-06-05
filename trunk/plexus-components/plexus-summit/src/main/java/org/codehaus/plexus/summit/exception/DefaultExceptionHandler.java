package org.codehaus.plexus.summit.exception;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.pipeline.Pipeline;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.util.ExceptionUtils;

/**
 * @plexus.component
 */
public class DefaultExceptionHandler
    extends AbstractSummitComponent
    implements ExceptionHandler
{
    public void handleException( RunData data, Throwable throwable )
        throws Exception
    {
        Resolver resolver = (Resolver) lookup( Resolver.ROLE );

        data.setTarget( resolver.getErrorView() );

        data.getMap().put( SummitConstants.STACK_TRACE, ExceptionUtils.getFullStackTrace( throwable ) );

        Pipeline pipeline = null;

        pipeline = (Pipeline) lookup( Pipeline.ROLE );

        pipeline.invoke( data );
    }
}
