package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.DefaultViewContext;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * @todo Use a Factory to create the particular flavour of the ViewContext
 */
public class CreateViewContextValve
    extends AbstractValve
{
    public void invoke( RunData data )
        throws IOException, SummitException
    {
        ViewContext viewContext = new DefaultViewContext();

        populateViewContext( data, viewContext );

        data.getMap().put( SummitConstants.VIEW_CONTEXT, viewContext );
    }

    /**
     * Populate the velocityContext.
     *
     * You might want to override this method in a subclass to provide
     * customized logic for populating the <code>ViewContext</code>. You
     * may want to look at the target view and populate the context
     * according to a set of rules based on that particular target
     * view.
     *
     * @param data RunData for request.
     * @param viewContext ViewContext that will be populated.
     */
    protected void populateViewContext( RunData data, ViewContext viewContext )
    {
        viewContext.put( "data", data );

        viewContext.put( "name", "root" );
    }
}
