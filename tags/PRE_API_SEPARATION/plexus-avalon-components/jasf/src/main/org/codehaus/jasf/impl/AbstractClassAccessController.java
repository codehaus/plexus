package org.codehaus.jasf.impl;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.jasf.ResourceController;

/**
 * Implements basic functionality that all
 * <code>ResourceAccessController</code>s for classes can use.
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 24, 2003
 */
public abstract class AbstractClassAccessController
    implements ResourceController
{
    private boolean defaultAuthorization;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        // Should we return false or true for methods which don't have a 
        // credential attribute?
        defaultAuthorization =
            config.getChild("defaultAuthorization").getValueAsBoolean(true);
    }

    /**
     * Returns the defaultAuthorization.
     * @return boolean
     */
    public boolean getDefaultAuthorization()
    {
        return defaultAuthorization;
    }

}
