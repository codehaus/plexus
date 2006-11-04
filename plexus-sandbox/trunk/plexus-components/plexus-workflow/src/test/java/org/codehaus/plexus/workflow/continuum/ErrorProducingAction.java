package org.codehaus.plexus.workflow.continuum;

import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.codehaus.plexus.action.Action;
import org.codehaus.werkflow.spi.Instance;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class ErrorProducingAction
    extends Assert
    implements Action
{
    public void execute( Map context )
        throws Exception
    {
        throw new Exception( "error" );
    }
}
