package org.codehaus.plexus.summit.pull;

import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Tools that go into the Toolbox should implement this interface.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface RequestTool
{
    /**
     * Initialize the RequestTool with RunData from the request.
     *
     * @param data initialization data
     */
    public void setRunData( RunData data );

    /**
     * Refresh the application tool. This is
     * necessary for development work where you
     * probably want the tool to refresh itself
     * if it is using configuration information
     * that is typically cached after initialization
     */
    public void refresh();
}
