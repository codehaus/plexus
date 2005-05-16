package org.codehaus.plexus.summit.resolver;

/**
 * <p>A resolver is a strategy for determining how target view will be
 * rendered. The target view may have sibling views which are to be
 * rendered and there may be <code>Modules</code> that must be
 * executed in order to populate the <code>ViewContext</code>.</p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface Resolver
{
    public final static String ROLE = Resolver.class.getName();

    Resolution resolve( String view )
        throws Exception;

    String getInitialView();

    String getDefaultView();

    String getErrorView();
}
