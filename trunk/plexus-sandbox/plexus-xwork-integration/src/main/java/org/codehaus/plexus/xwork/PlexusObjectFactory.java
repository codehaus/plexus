package org.codehaus.plexus.xwork;

import com.opensymphony.webwork.util.ObjectFactoryInitializable;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.validator.Validator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.PlexusContainer;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class PlexusObjectFactory
    extends ObjectFactory
    implements ObjectFactoryInitializable
{
    private static final Log log = LogFactory.getLog( PlexusObjectFactory.class );

    private static final String PLEXUS_COMPONENT_TYPE = "plexus.component.type";

    // ----------------------------------------------------------------------
    // Privates
    // ----------------------------------------------------------------------

    private PlexusContainer base;

    private List plexusComponents = new ArrayList();

    private List otherComponents = new ArrayList();

    // ----------------------------------------------------------------------
    // ObjectFactory overrides
    // ----------------------------------------------------------------------

    public void init( ServletContext servletContext )
    {
        if ( !PlexusLifecycleListener.loaded )
        {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP PLEXUS-WEBWORK INTEGRATION **********\n" +
                "Looks like the Plexus listener was not configured for your web app! \n" +
                "You need to add the following to web.xml: \n" + "\n" +
                "    <!-- this should be before the webwork filter -->\n" + "    <filter>\n" +
                "        <filter-name>plexus</filter-name>\n" +
                "        <filter-class>org.codehaus.plexus.xwork.PlexusFilter</filter-class>\n" + "    </filter>\n" +
                "\n" + "...\n" + "\n" + "    <!-- this should be before the webwork filter -->\n" +
                "    <filter-mapping>\n" + "        <filter-name>plexus</filter-name>\n" +
                "        <url-pattern>/*</url-pattern>\n" + "    </filter-mapping>\n" + "\n" + "...\n" + "\n" +
                "    <listener>\n" +
                "        <listener-class>org.codehaus.plexus.xwork.PlexusLifecycleListener</listener-class>\n" +
                "    </listener>";
            log.fatal( message );
            return;
        }

        base = (PlexusContainer) servletContext.getAttribute( PlexusLifecycleListener.KEY );

        // used by the servlet configuration phase
        base.getContext().put( ServletContext.class.getName(), servletContext );
    }

    public Object buildAction( String actionName, String namespace, ActionConfig config, Map extraContext )
        throws Exception
    {
        if ( extraContext == null )
        {
            extraContext = new HashMap();
        }

        extraContext.put( PLEXUS_COMPONENT_TYPE, Action.class.getName() );

        return super.buildAction( actionName, namespace, config, extraContext );
    }

    public Interceptor buildInterceptor( InterceptorConfig interceptorConfig, Map interceptorRefParams )
        throws ConfigurationException
    {
        String interceptorClassName = interceptorConfig.getClassName();
        Map thisInterceptorClassParams = interceptorConfig.getParams();
        Map params = ( thisInterceptorClassParams == null ) ? new HashMap() : new HashMap( thisInterceptorClassParams );
        params.putAll( interceptorRefParams );

        String message;
        Throwable cause;

        try
        {
            Map extraContext = new HashMap();
            extraContext.put( PLEXUS_COMPONENT_TYPE, Interceptor.class.getName() );
            Interceptor interceptor = (Interceptor) buildBean( interceptorClassName, extraContext );
            OgnlUtil.setProperties( params, interceptor );
            interceptor.init();

            return interceptor;
        }
        catch ( InstantiationException e )
        {
            cause = e;
            message = "Unable to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        }
        catch ( IllegalAccessException e )
        {
            cause = e;
            message = "IllegalAccessException while attempting to instantiate an instance of Interceptor class [" +
                interceptorClassName + "].";
        }
        catch ( ClassCastException e )
        {
            cause = e;
            message = "Class [" + interceptorClassName +
                "] does not implement com.opensymphony.xwork.interceptor.Interceptor";
        }
        catch ( Exception e )
        {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        }
        catch ( NoClassDefFoundError e )
        {
            cause = e;
            message = "Could not load class " + interceptorClassName +
                ". Perhaps it exists but certain dependencies are not available?";
        }

        throw new ConfigurationException( message, cause );
    }

    public Result buildResult( ResultConfig resultConfig, Map extraContext )
        throws Exception
    {
        if ( extraContext == null )
        {
            extraContext = new HashMap();
        }

        extraContext.put( PLEXUS_COMPONENT_TYPE, Result.class.getName() );

        return super.buildResult( resultConfig, extraContext );
    }

    public Validator buildValidator( String className, Map params, Map extraContext )
        throws Exception
    {
        Map context = new HashMap();
        context.put( PLEXUS_COMPONENT_TYPE, Validator.class.getName() );
        Validator validator = (Validator) buildBean( className, context );
        OgnlUtil.setProperties( params, validator );

        return validator;
    }

    public Object buildBean( String className, Map extraContext )
        throws Exception
    {
        if ( extraContext != null )
        {
            String type = (String) extraContext.get( PLEXUS_COMPONENT_TYPE );

            if ( type != null )
            {
                return lookup( type, className, extraContext );
            }
        }

        return super.buildBean( className, extraContext );
    }

    public Object buildBean( Class clazz, Map extraContext )
        throws Exception
    {
        if ( extraContext != null )
        {
            String type = (String) extraContext.get( PLEXUS_COMPONENT_TYPE );

            if ( type != null )
            {
                return lookup( type, clazz.getName(), extraContext );
            }
        }

        return lookup( clazz.getName(), extraContext );
    }

    public Class getClassInstance( String className )
        throws ClassNotFoundException
    {
        PlexusContainer pc = PlexusThreadLocal.getPlexusContainer();

        if ( pc == null )
        {
            pc = base;
        }

        // TODO: these chain of exceptions can potentially hide useful errors. Perhaps Plexus could differentiate a
        // component lookup exception from a component not found exception.
        try
        {
            return pc.lookup( className ).getClass();
        }
        catch ( Exception e1 )
        {
            try
            {
                return pc.lookup( Action.class.getName(), className ).getClass();
            }
            catch ( Exception e2 )
            {
                try
                {
                    return pc.lookup( Interceptor.class.getName(), className ).getClass();
                }
                catch ( Exception e3 )
                {
                    try
                    {
                        return pc.lookup( Validator.class.getName(), className ).getClass();
                    }
                    catch ( Exception e4 )
                    {
                        try
                        {
                            return pc.lookup( Result.class.getName(), className ).getClass();
                        }
                        catch ( Exception e5 )
                        {
                            return super.getClassInstance( className );
                        }
                    }
                }
            }
        }
    }

    private Object lookup( String role )
        throws Exception
    {
        return lookup( role, null, null );
    }

    private Object lookup( String role, Map extraContext )
        throws Exception
    {
        return lookup( role, null, extraContext );
    }

    private Object lookup( String role, String roleHint )
        throws Exception
    {
        return lookup( role, roleHint, null );
    }

    private Object lookup( String role, String roleHint, Map extraContext )
        throws Exception
    {
        String id = role + ":" + roleHint;
        PlexusContainer pc = PlexusThreadLocal.getPlexusContainer();

        if ( pc == null )
        {
            pc = base;
        }

        if ( plexusComponents.contains( id ) )
        {
            return loadComponentWithPlexus( pc, role, roleHint );
        }

        if ( otherComponents.contains( id ) )
        {
            return loadComponentWithXWork( pc, role, roleHint, extraContext );
        }

        try
        {
            Object o = loadComponentWithPlexus( pc, role, roleHint );
            plexusComponents.add( id );
            return o;
        }
        catch ( Exception e )
        {
            log.debug( "Can't load component (" + role + "/" + roleHint + ") with plexus, try now with webwork.", e );
            Object o = loadComponentWithXWork( pc, role, roleHint, extraContext );
            otherComponents.add( id );
            return o;
        }
    }

    private Object loadComponentWithPlexus( PlexusContainer pc, String role, String roleHint )
        throws Exception
    {
        return pc.lookup( role, roleHint );
    }

    private Object loadComponentWithXWork( PlexusContainer pc, String role, String roleHint, Map extraContext )
        throws Exception
    {
        String className = role;

        if ( Action.class.getName().equals( role ) || Interceptor.class.getName().equals( role ) ||
            Result.class.getName().equals( role ) || Validator.class.getName().equals( role ) )
        {
            className = roleHint;
        }

        Object o = super.buildBean( super.getClassInstance( className ), extraContext );
        pc.autowire( o );
        return o;
    }
}
