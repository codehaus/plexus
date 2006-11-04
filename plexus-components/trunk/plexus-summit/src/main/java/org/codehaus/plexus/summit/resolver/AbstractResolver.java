package org.codehaus.plexus.summit.resolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.view.DefaultView;
import org.codehaus.plexus.summit.view.View;

/**
 * <p>The base class from which all <code>Resolver</code>s are derived.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractResolver
    extends AbstractSummitComponent
    implements Resolver
{
    /**
     * @plexus.configuration
     *  default-value="Error.vm"
     */
    private String errorView;

    /**
     * @plexus.configuration
     *  default-value="Error.vm"
     */
    private String resultMessagesView;

    /**
     * @plexus.configuration
     *  default-value="Index.vm"
     */
    private String defaultView;

    private String initialView;

    public String getErrorView()
    {
        return errorView;
    }

    public String getResultMessagesView()
    {
        return resultMessagesView;
    }

    public String getDefaultView()
    {
        return defaultView;
    }

    public String getInitialView()
    {
        return initialView;
    }

    // -------------------------------------------------------------------------
    // V I E W  M E T H O D S
    // -------------------------------------------------------------------------

    /**
     * Gets the view attribute of the AbstractResolver object
     */
    protected View getView( String target )
        throws Exception
    {
        return getView( target, null, null );
    }

    /**
     * Gets the view attribute of the AbstractResolver object
     */
    protected View getView( String target, String targetPrefix )
        throws Exception
    {
        return getView( target, targetPrefix, null );
    }

    protected abstract Renderer getRenderer( String target ) throws Exception;

    /**
     * Gets the view attribute of the AbstractResolver object
     */
    protected View getView( String target, String targetPrefix, String defaultView )
        throws Exception
    {
        List possibleViews = getPossibleViews( target, targetPrefix );

        if ( defaultView != null && defaultView.length() > 0 )
        {
            possibleViews.add( defaultView );
        }

        for ( Iterator i = possibleViews.iterator(); i.hasNext(); )
        {
            String view = (String) i.next();

            if ( getRenderer( target ).viewExists( view ) )
            {
                return new DefaultView( view );
            }
        }

        return null;
    }

    List getPossibleViews( String target, String targetPrefix )
        throws Exception
    {
        List views = new ArrayList();

        List possibleViews = ResolverUtils.getPossibleViews( target, getDefaultView() );

        for ( Iterator i = possibleViews.iterator(); i.hasNext(); )
        {
            String view;

            String possibleView = (String) i.next();

            if ( targetPrefix != null && targetPrefix.length() > 0 )
            {
                view = targetPrefix + "/" + possibleView;
            }
            else
            {
                view = possibleView;
            }

            views.add( view );
        }

        return views;
    }
}
