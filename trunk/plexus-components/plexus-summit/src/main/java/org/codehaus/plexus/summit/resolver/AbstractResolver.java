package org.codehaus.plexus.summit.resolver;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.view.DefaultView;
import org.codehaus.plexus.summit.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>The base class from which all <code>Resolver</code>s are derived.</p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo Propagate ModuleNotFoundException in getModule()
 * @todo Use a factory to instantiate the View class.
 */
public abstract class AbstractResolver
    extends AbstractSummitComponent
    implements Resolver
{
    /** The view that will be used when there is an error. */
    private String errorView;

    /** The default view i.e. index.vm */
    private String defaultView;

    /**
     * The name that will be searched for when a match cannot be made using the
     * target's name within a <i>package</i> . This default base name is used
     * for both Modules and Views. So, for example, if a layout module based on
     * the target's name cannot be found within a package and the default base
     * name is <i>Default</i> then Default.class will be searched for.
     */
    private String defaultBaseName;

    /**
     * Default extension to use for views.
     */
    private String defaultViewExtension;

    /**
     * Returns the error view.
     * @see org.codehaus.plexus.summit.resolver.Resolver#getErrorView()
     */
    public String getErrorView()
    {
        return errorView;
    }

    /**
     * Set the default base name used in part to resolve <b>Modules</b> and <b>
     * Views</b> .
     *
     * @param defaultBaseName
     */
    void setDefaultBaseName( String defaultBaseName )
    {
        this.defaultBaseName = defaultBaseName;
    }

    /**
     * Get the default base name used in part to resolve <b>Modules</b> and <b>
     * Views</b> .
     *
     * @return String Default base name
     */
    public String getDefaultBaseName()
    {
        return defaultBaseName;
    }

    public String getDefaultView()
    {
        return defaultView;
    }

    /**
     * Set the default view extension used in part to resolve <b>Views</b> .
     */
    void setDefaultViewExtension( String defaultViewExtension )
    {
        this.defaultViewExtension = defaultViewExtension;
    }

    /**
     * Get the default view extension used in part to resolve <b>Views</b> .
     *
     * @return defaultViewExtension
     */
    public String getDefaultViewExtension()
    {
        return defaultViewExtension;
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

    /**
     * Gets the view attribute of the AbstractResolver object
     */
    protected View getView( String target, String targetPrefix, String defaultView )
        throws Exception
    {
        List possibleViews = getPossibleViews( target, targetPrefix );

        String screenType;

        if ( target.indexOf( "form" ) > 0 )
        {
            screenType = "form";
        }
        else
        {
            screenType = "velocity";
        }

//        System.out.println( "target = " + target );
//
//        System.out.println( "screenType = " + screenType );

        Renderer renderer = (Renderer) lookup( Renderer.ROLE, screenType );

        if ( defaultView != null && defaultView.length() > 0 )
        {
            possibleViews.add( defaultView );
        }

        for ( Iterator i = possibleViews.iterator(); i.hasNext(); )
        {
            String view = (String) i.next();

//            System.out.println( "view = " + view );

            if ( renderer.viewExists( view ) )
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

        List possibleViews = ResolverUtils.getPossibleViews( target, getDefaultBaseName() );

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

            view = view + "." + getDefaultViewExtension();

            views.add( view );
        }

        return views;
    }
}
