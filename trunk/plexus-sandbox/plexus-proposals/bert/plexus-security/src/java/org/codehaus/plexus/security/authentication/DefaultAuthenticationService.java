package org.codehaus.plexus.security.authentication;

import java.util.Iterator;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.util.ServiceManagerUtils;

/**
 * Working AuthenticationService.
 * 
 * <p>Configuration format is as follows:
 * 
 * <pre><![CDATA[
 *	<authentication-handlers>
 *		<authentication-handler>
 *			<token-class>org.codehaus.plexus.security.authorisation.pap.PAPToken</token-class>	
 *			<handler-id>pap</handler-id>
 *		</authentication-handler>
 *		...
 *  </authentication-handlers>
 * ]]>
 * </pre>
 * 
 * The handler must be configured as a component with the AuthenticationHandler.ROLE and the
 * same id as &lt;handler-id&gt;..&lt/..&gt.
 * </p>
 * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultAuthenticationService
    extends AbstractAuthenticationService
    implements Configurable, Serviceable,Disposable
{
    private ServiceManager service;

    /**
     * 
     */
    public DefaultAuthenticationService()
    {
        super();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        Configuration[] handlers =
            config.getChild("authentication-handlers").getChildren("authentication-handler");
        for (int i = 0; i < handlers.length; i++)
        {
            String id = handlers[i].getChild("handler-id").getValue();
            String tokenClass = handlers[i].getChild("token-class").getValue();
            Class clazz;
            try
            {
                clazz = getClass().getClassLoader().loadClass(tokenClass);
                String key = ServiceManagerUtils.getKey(AuthenticationHandler.ROLE, id);
                AuthenticationHandler handler = (AuthenticationHandler) service.lookup(key);
                registerAuthenticationHandler(clazz, handler);
            }
            catch (ClassNotFoundException e)
            {
                throw new ConfigurationException(
                    "Token class:" + tokenClass + " not a valid class");
            }
            catch (ServiceException e)
            {
                throw new ConfigurationException(
                    "Could not lookup the authentication handler with id:" + id,
                    e);
            }
            catch (DuplicateHandlerException e)
            {
                throw new ConfigurationException(
                    "Duplicate Authenticationhandler for token:" + tokenClass,
                    e);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager service) throws ServiceException
    {
        this.service = service;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        //suppose we better release the AuthenticationHandlers..
        Iterator iter = getHandlers().iterator();
        while (iter.hasNext())
        {
            service.release(iter.next() );            
        }
        service = null;
    }

}
