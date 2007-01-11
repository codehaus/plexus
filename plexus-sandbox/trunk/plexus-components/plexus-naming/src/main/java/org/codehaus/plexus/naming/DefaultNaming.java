package org.codehaus.plexus.naming;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;

import org.apache.naming.ResourceRef;
import org.apache.naming.config.Config;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.reflection.ReflectorException;

/**
 * Default implementation of naming.
 *
 * @author Brett Porter
 * @plexus.component role="org.codehaus.plexus.naming.Naming" role-hint="default"
 */
public class DefaultNaming
    implements Naming, Initializable
{
    private static final String COMP_CONTEXT_NAME = "java:comp";

    private static final String ENV_CONTEXT_NAME = "env";

    private Context envContext;

    /**
     * @plexus.configuration
     */
    private List resources;

    /**
     * @plexus.configuration
     */
    private List environments;

    /**
     * @plexus.configuration default-value="true"
     */
    private boolean setSystemProperties = true; // deliberately set true default here so you must declare it false, not just omit it

    public Context createInitialContext()
        throws NamingException
    {
        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, org.apache.naming.java.javaURLContextFactory.class.getName() );
        env.put( Context.URL_PKG_PREFIXES, "org.apache.naming" );

        return new InitialContext( env );
    }

    public void initialize()
        throws InitializationException
    {
        if ( setSystemProperties )
        {
            System.setProperty( Context.INITIAL_CONTEXT_FACTORY, org.apache.naming.java.javaURLContextFactory.class
                .getName() );
            System.setProperty( Context.URL_PKG_PREFIXES, "org.apache.naming" );
        }

        try
        {
            Context initialContext = createInitialContext();
            Context subcontext = getOrCreate( initialContext, COMP_CONTEXT_NAME );
            envContext = getOrCreate( subcontext, ENV_CONTEXT_NAME );

            loadConfiguration();
        }
        catch ( NamingException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }
        catch ( ReflectorException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }
    }

    private static Context getOrCreate( Context initialContext, String name )
        throws NamingException
    {
        Context subcontext;
        try
        {
            subcontext = initialContext.createSubcontext( name );
        }
        catch ( NameAlreadyBoundException e )
        {
            subcontext = (Context) initialContext.lookup( name );
        }
        return subcontext;
    }

    /**
     * Destroys initial context.
     * <p/>
     * Invokes <code>Context.destroySubcontext(Name)</code> only on top-level
     * subcontexts.
     *
     * @throws NamingException if a NamingException occurs.
     */
    public synchronized void destroyInitialContext()
        throws NamingException
    {
        Context initialContext = new InitialContext();
        NamingEnumeration contexts = initialContext.list( "" );
        while ( contexts.hasMore() )
        {
            initialContext.destroySubcontext( ( (NameClassPair) contexts.next() ).getName() );
        }
        envContext = null;
    }

    /**
     * Loads xml configuration data from the component configuration.
     *
     * @throws NamingException if a NamingException occurs.
     */
    public synchronized void loadConfiguration()
        throws NamingException, ClassNotFoundException, ReflectorException
    {

        if ( environments != null )
        {
            for ( Iterator iterator = this.environments.iterator(); iterator.hasNext(); )
            {
                Environment environment = (Environment) iterator.next();
                Config.Environment config = new Config.Environment();
                config.setName( environment.getName() );
                config.setType( environment.getType() );
                config.setValue( environment.getValue() );
                envContext.bind( environment.getName(), config.createValue() );
            }
        }

        if ( resources != null )
        {
            for ( Iterator j = resources.iterator(); j.hasNext(); )
            {
                Resource r = (Resource) j.next();

                Name name = new CompositeName( r.getName() );

                try
                {
                    for ( int i = 1; i <= name.size() - 1; i++ )
                    {
                        envContext.createSubcontext( name.getPrefix( i ) );
                    }
                }
                catch ( NameAlreadyBoundException e )
                {
                    // The prefix is already added as a sub context
                }

                envContext.bind( r.getName(), createResource( r ) );
            }
        }
    }

    private ResourceRef createResource( Resource r )
    {
        ResourceRef ref = new ResourceRef( r.getType(), null, null, null );
        Properties parameters = r.getProperties();
        for ( Iterator i = parameters.keySet().iterator(); i.hasNext(); )
        {
            String name = (String) i.next();
            String value = parameters.getProperty( name );
            ref.add( new StringRefAddr( name, value ) );
        }
        return ref;
    }
}
