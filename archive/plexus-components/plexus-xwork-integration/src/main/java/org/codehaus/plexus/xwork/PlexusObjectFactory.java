package org.codehaus.plexus.xwork;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 * @noinspection ProhibitedExceptionDeclared
 */
public class PlexusObjectFactory
    extends ObjectFactory
    implements ObjectFactoryInitializable
{
    private static final String PLEXUS_COMPONENT_TYPE = "plexus.component.type";

    private static final String PLEXUS_NOT_LOADED_ERROR_MSG = "********** FATAL ERROR STARTING UP " +
        "PLEXUS-WEBWORK INTEGRATION **********\n" +
        "Looks like the Plexus listener was not configured for your web app! \n" +
        "You need to add the following to web.xml: \n" + "\n" + "    <listener>\n" +
        "        <listener-class>org.codehaus.plexus.xwork.PlexusLifecycleListener</listener-class>\n" +
        "    </listener>";

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
        base = (PlexusContainer) servletContext.getAttribute( PlexusLifecycleListener.KEY );

        // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
        if ( base == null )
        {
            servletContext.log( PLEXUS_NOT_LOADED_ERROR_MSG );
        }
    }

    public Object buildAction( String actionName, String namespace, ActionConfig config, Map extraContext )
        throws Exception
    {
        Map context = extraContext;
        if ( context == null )
        {
            context = new HashMap();
        }

        context.put( PLEXUS_COMPONENT_TYPE, Action.class.getName() );

        return super.buildAction( actionName, namespace, config, context );
    }

    public Interceptor buildInterceptor( InterceptorConfig interceptorConfig, Map interceptorRefParams )
        throws ConfigurationException
    {
        String interceptorClassName = interceptorConfig.getClassName();
        Map thisInterceptorClassParams = interceptorConfig.getParams();
        Map params = thisInterceptorClassParams == null ? new HashMap() : new HashMap( thisInterceptorClassParams );
        params.putAll( interceptorRefParams );

        String message;
        Throwable cause;

        //noinspection ErrorNotRethrown
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
        Map context = extraContext;
        if ( context == null )
        {
            context = new HashMap();
        }

        context.put( PLEXUS_COMPONENT_TYPE, Result.class.getName() );

        return super.buildResult( resultConfig, context );
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

    /**
     * Used to provide useful exception messages to a web-app during a lookup in {@link #getClassInstance(String)}
     *
     * @param clazz     the type of class to look up
     * @param className the name of the specific class (or plexus role) to look up.
     * @return the class that was found.
     * @throws ComponentNotFoundException if the component was simply not found.
     * @throws ComponentCreationException if the component was found, but failed to be created correctly.
     */
    private Class lookupClass( Class clazz, String className )
        throws ComponentNotFoundException, ComponentCreationException
    {
        return lookup( base, clazz.getName(), className ).getClass();
    }

    public Class getClassInstance( String className )
        throws ClassNotFoundException
    {
        List exceptions = new ArrayList();

        try
        {
            return lookupClass( Class.class, className );
        }
        catch ( ComponentCreationException e )
        {
            getLogger().error( e.getMessage(), e );
            throw new ClassNotFoundException( className, e );
        }
        catch ( ComponentNotFoundException e )
        {
            exceptions.add( e );
            // Fall Thru to next lookup Technique.
        }

        try
        {
            return lookupClass( Action.class, className );
        }
        catch ( ComponentCreationException e )
        {
            getLogger().error( e.getMessage(), e );
            throw new ClassNotFoundException( className, e );
        }
        catch ( ComponentNotFoundException e )
        {
            exceptions.add( e );
            // Fall Thru to next lookup Technique.
        }

        try
        {
            return lookupClass( Interceptor.class, className );
        }
        catch ( ComponentCreationException e )
        {
            getLogger().error( e.getMessage(), e );
            throw new ClassNotFoundException( className, e );
        }
        catch ( ComponentNotFoundException e )
        {
            exceptions.add( e );
            // Fall Thru to next lookup Technique.
        }

        try
        {
            return lookupClass( Validator.class, className );
        }
        catch ( ComponentCreationException e )
        {
            getLogger().error( e.getMessage(), e );
            throw new ClassNotFoundException( className, e );
        }
        catch ( ComponentNotFoundException e )
        {
            exceptions.add( e );
            // Fall Thru to next lookup Technique.
        }

        try
        {
            return lookupClass( Result.class, className );
        }
        catch ( ComponentCreationException e )
        {
            getLogger().error( e.getMessage(), e );
            throw new ClassNotFoundException( className, e );
        }
        catch ( ComponentNotFoundException e )
        {
            exceptions.add( e );
            // Fall Thru to next lookup Technique.
        }

        getLogger().debug( "All standard lookups have failed for getClassInstance( \"" + className +
            "\" ), the following exceptions detail the problem." );

        Iterator it = exceptions.iterator();
        while ( it.hasNext() )
        {
            Exception e = (Exception) it.next();
            getLogger().debug( e.getMessage(), e );
        }

        // Try the xwork component lookup as a fallback.
        return super.getClassInstance( className );
    }

    private Object lookup( String role, Map extraContext )
        throws Exception
    {
        return lookup( role, null, extraContext );
    }

    private Object lookup( String role, String roleHint, Map extraContext )
        throws Exception
    {
        String id = role + ":" + roleHint;

        if ( plexusComponents.contains( id ) )
        {
            return loadComponentWithPlexus( base, role, roleHint );
        }

        if ( otherComponents.contains( id ) )
        {
            return loadComponentWithXWork( base, role, roleHint, extraContext );
        }

        try
        {
            Object o = loadComponentWithPlexus( base, role, roleHint );
            plexusComponents.add( id );
            return o;
        }
        catch ( ComponentCreationException e )
        {
            getLogger().error( e.getMessage(), e );
            throw e;
        }
        catch ( ComponentNotFoundException e )
        {
            Object o = loadComponentWithXWork( base, role, roleHint, extraContext );
            otherComponents.add( id );
            return o;
        }
    }

    private Object loadComponentWithPlexus( PlexusContainer pc, String role, String roleHint )
        throws ComponentNotFoundException, ComponentCreationException
    {
        return lookup( pc, role, roleHint );
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

    /**
     * Specialized Lookup supplement for standard plexus process, used to differentiate between an object
     * that just doesn't match the provided criteria, and one that did match, but failed to be created due to
     * component composition issues.
     * <p/>
     * Used to provide useful exception messages to a web-app during a lookup in {@link #getClassInstance(String)} and
     * {@link #lookup(String,String,Map)}
     *
     * @param plexus   the plexus container to use for looking up the component
     * @param role     the component role to look up
     * @param roleHint the hint for the plexus role (if required).
     * @return the class that was found.
     * @throws ComponentNotFoundException if the component was simply not found.
     * @throws ComponentCreationException if the component was found, but failed to be created correctly.
     */
    private Object lookup( PlexusContainer plexus, String role, String roleHint )
        throws ComponentNotFoundException, ComponentCreationException
    {
        if ( StringUtils.isEmpty( role ) )
        {
            throw new ComponentNotFoundException( "Unable to find component for empty role." );
        }

        try
        {
            return plexus.lookup( role, roleHint, plexus.getLookupRealm() );
        }
        catch ( ComponentLookupException e )
        {
            Throwable cause = e.getCause();
            while ( cause != null )
            {
                if ( cause instanceof CompositionException )
                {
                    throw new ComponentCreationException(
                        "Unable look up " + role + ":" + roleHint + " due to plexus misconfiguration.", e );
                }
                cause = cause.getCause();
            }
            throw new ComponentNotFoundException(
                "Failed lookup for " + role + ":" + roleHint + " in realm " + plexus.getLookupRealm(), e );
        }
    }

    private Logger getLogger()
    {
        // Cheating here...
        return base.getLoggerManager().getLoggerForComponent( ObjectFactory.class.getName(), "plexus" );
    }
}
