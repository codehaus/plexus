package org.codehaus.plexus.lifecycle.avalon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
  * ServiceManager delegate which restricts access to only a specified list of
  * components.
  * 
 * <p>Configuration format is as follows:
 * 
 * <pre><![CDATA[
 *	<allow-components>
 *		<role>myRole</role>
 *		<role>anotherRole</role>
 *  </allow-components>
 * ]]>
 * </pre>
 * 
 * The handler must be configured as a component with the AuthenticationHandler.ROLE and the
 * same id as &lt;handler-id&gt;..&lt/..&gt.
 * </p>
 * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RestrictedServiceManagerDelegate implements ServiceManager, Configurable, Serviceable
{
    /** List of components which access is granted to. The names are the
     * names of the roles used to lookup components. Since this list is not
     * expected to be modified once generated then there will only be
     * concurrent reads, so a  write threadsafe list is not required. */
    private List allowedComponents;

    /**
     * The service manager to delegate to
     */
    private ServiceManager service;

    /**
     * 
     */
    public RestrictedServiceManagerDelegate()
    {
        super();
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String role)
    {
        return allowedComponents.contains(role) && service.hasService(role);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String role) throws ServiceException
    {
        if (allowedComponents.contains(role))
        {
            return service.lookup(role);
        }
        else
        {
            throw new ServiceException(role, "Can not find the specified role");
        }
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object role)
    {
        service.release(role);
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        Configuration[] roles = config.getChild("allow-components").getChildren("role");
        allowedComponents = new ArrayList();
		Collections.synchronizedList(allowedComponents);
        for (int i = 0; i < roles.length; i++)
        {
            allowedComponents.add(roles[i].getValue());
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager service) throws ServiceException
    {
        this.service = service;
    }

}
