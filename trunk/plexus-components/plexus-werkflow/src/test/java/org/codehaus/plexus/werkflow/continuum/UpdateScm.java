package org.codehaus.plexus.werkflow.continuum;

import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.codehaus.plexus.action.Action;
import org.codehaus.werkflow.spi.Instance;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class UpdateScm
    extends Assert
    implements Action
{
    public void execute( Map context )
        throws Exception
    {
        Instance instance = (Instance) context.get( "instance" );

        Properties properties = (Properties) context.get( "properties" );

        instance.put( "action-update-scm", "done" );
    }
}
