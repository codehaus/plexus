package org.codehaus.plexus.summit;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * <p>The default <code>SummitView</code> implementation.</p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 * @todo Logging on a per application view basis
 * @todo Fix the classloading and get rid of Class.forName(...)
 */
public class DefaultSummitView
    extends AbstractLogEnabled
    implements SummitView
{
    /** The ID of this view. */
    private String id;

    /** The name of this view. */
    private String name;

    /** The context that is to be used when contextualizing. */
    private Context summitContext;

    /**
     * Obtain the context from the parent container and create a new
     * child-context that contains a reference to this instance so
     * that components created by this instance can obtain a reference
     * to the appropriate SummitView.
     *
     * @param context The context received from the parent container.
     * @throws org.codehaus.plexus.context.ContextException If there is an error contextualizing.
     */
    public void contextualize( Context context )
        throws ContextException
    {
    }

    public void initialize()
        throws Exception
    {
        try
        {
            getLogger().info( "Initializing SummitView " + id );
        }
        catch ( Throwable e )
        {
            throw new Exception( "View initialization error!", e );
        }
    }
}
