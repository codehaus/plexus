package org.codehaus.plexus.summit.pull;

import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * <p>The Pull Service manages the creation of application tools that are
 * available to all templates in a Turbine application. By using the Pull
 * Service you can avoid having to make Screens to populate a context for use in
 * a particular template. The Pull Service creates a set of tools, as specified
 * in the TR. props file.</p>
 *
 * <p>These tools can have global scope, request scope, session scope (i.e.
 * stored in user temp hashmap) or persistent scope (i.e. stored in user perm
 * hashmap).</p>
 *
 * <p>The standard way of referencing these global tools is through the toolbox
 * handle. This handle is typically $toolbox, but can be specified in the TR.
 * props file.</p>
 *
 * <p>So, for example, if you had a UI Manager tool which created a set of UI
 * attributes from a properties file, and one of the properties was 'bgcolor',
 * then you could access this UI attribute with $ui.bgcolor. The identifier that
 * is given to the tool, in this case 'ui', can be specified as well.</p>
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface PullService
{
    /** The Avalon role */
    public static final String ROLE = PullService.class.getName();

    /**
     * Populate the given context with all request, session and persistent scope
     * tools (it is assumed that the context already wraps the global context,
     * and thus already contains the global tools).
     *
     * @param context a ViewContext to populate
     * @param data a RunData object for request specific data
     */
    public void populateContext(ViewContext context, RunData data);

    /**
     * Release tool instances from the given context to the
     * object pool
     *
     * @param context a ViewContext to release tools from
     */
    public void releaseTools(ViewContext context);

}
