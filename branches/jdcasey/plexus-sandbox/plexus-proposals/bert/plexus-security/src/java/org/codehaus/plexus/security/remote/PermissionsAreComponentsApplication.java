package org.codehaus.plexus.security.remote;

import java.util.Collection;
import java.util.Vector;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.SessionManager;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.request.RequestInterceptor;
import org.codehaus.plexus.security.request.SecurityRequestInterceptorDelegate;
import org.codehaus.plexus.security.request.proxy.ProxyServiceManagerDelegate;
import org.codehaus.plexus.security.simple.Permission;
import org.codehaus.plexus.security.simple.SimpleAgent;
import org.codehaus.plexus.util.DebugUtils;

/**
 * <p>Configuration format is as follows:
 * 
 * <pre><![CDATA[
 * 	<name>myAppName</name>
 * 	<majorVersion>myAppVersion.major</majorVersion>
 * 	<minorVersion>myAppVersion.minor</minorVersion>
 * 	<build>myAppBuild</build>
 * 	<registeredTo>myAppRegisteredTo</registeredTo>
 * ]]>
 * </pre>
 * 
 * <p>Requires:
 * <ul>
 * 	<li>SessionManager</li>
 * 	<li>Agent to be a <code>{@ @link org.codehaus.plexus.security.simple.SimpleAgent}</code></li>
 * </ul>
 * </p>
 * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class PermissionsAreComponentsApplication
    implements Application, Serviceable, Initializable, Disposable, Configurable
{
    private String appName;
    private String appMajorVersion;
    private String appMinorVersion;
    private String appBuild;
    private String appRegisteredTo;

    /** The security service to authenticate against */
    private SessionManager security;

    /** The source ServiceManager */
    private ServiceManager service;

    /** Wraps the RestrictedServiceManager to build proxied components which
     * begin and end a request on each method invocation */
    private ProxyServiceManagerDelegate proxyServiceManager;

    /** The request interceptor to pass to the proxyServiceManager to enable proxied 
     * components to notify the system of request demarcation */
    private RequestInterceptor requestInterceptor;
    /** Configuration passed in by the container */
    private Configuration config;

    //private Map session = new ThreadSafeMap();

    /**
     * @see org.codehaus.plexus.security.remote.Application#authenticate(java.lang.Object)
     */
    public ApplicationSession authenticate(Object token) throws AuthenticationException
    {

        //@todo possibly keep track of the ApplicationSession so we return the same one
        //even if the client re authenticates. Possibly make it configureable if a new session
        //or an existing session is returned. Returning an already active session allows one
        //to share sessions across devices like PDA's,browsers,WAP phones etc by
        //simply logging in 
        PlexusSession sess = security.authenticate(token);
        SimpleAgent agent = (SimpleAgent)sess.getAgent();
        //accessible components are simply the names of the permissions..
        Permission[] permissions = agent.getACL().getPermissions();
        Collection components = new Vector();
        for (int i = 0; i < permissions.length; i++)
        {
            components.add(permissions[i].getName() );
        }                
        RestrictedApplicationSession appSession =
            new RestrictedApplicationSession(sess, proxyServiceManager, requestInterceptor,components);
        return appSession;
    }

    /**
     * @see org.codehaus.plexus.security.remote.Application#getBuild()
     */
    public String getBuild()
    {
        return appBuild;
    }

    /**
     * @see org.codehaus.plexus.security.remote.Application#getName()
     */
    public String getName()
    {
        return appName;
    }

    /**
     * @see org.codehaus.plexus.security.remote.Application#getVersion()
     */
    public String getMajorVersion()
    {
        return appMajorVersion;
    }

    /**
    	 * @see org.codehaus.plexus.security.remote.Application#getMinorVersion()
    	 */
    public String getMinorVersion()
    {
        return appMinorVersion;
    }

    /**
     * @see org.codehaus.plexus.security.remote.Application#getRegisteredTo()
     */
    public String getRegisteredTo()
    {
        return appRegisteredTo;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager service) throws ServiceException
    {
        this.service = service;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        security = (SessionManager) service.lookup(SessionManager.ROLE);

        proxyServiceManager = new ProxyServiceManagerDelegate();
        proxyServiceManager.service(service);

        //setup the request interceptor to delegate to the securityService the
        //task of handling request demarcation
        requestInterceptor = new SecurityRequestInterceptorDelegate(security);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        if (security != null)
        {
            service.release(security);
        }
        service = null;
        security = null;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        this.config = config;
        appBuild = config.getChild("build").getValue("");
        appMajorVersion = config.getChild("majorVersion").getValue("");
		appMinorVersion = config.getChild("minorVersion").getValue("");
        appName = config.getChild("name").getValue("");
        appRegisteredTo = config.getChild("registeredTo").getValue("");
    }

}
