package org.codehaus.plexus.summit.view;

/**
 * <p>A <code>View</code> is a container for the name of a resolved
 * view and anything else that might eventually be associated with
 * a view. </p>
 *
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface View
{
    /**
     * Set the name of the view.
     */
    void setName( String name );

    /**
     * Get the name of the view.
     */
    String getName();
}
