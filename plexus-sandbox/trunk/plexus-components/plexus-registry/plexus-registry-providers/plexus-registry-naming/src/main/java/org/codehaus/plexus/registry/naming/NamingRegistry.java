package org.codehaus.plexus.registry.naming;

/*
 * Copyright 2007 The Codehaus Foundation.
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

import org.apache.naming.NamingContext;
import org.apache.naming.config.Config;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.naming.Naming;
import org.codehaus.plexus.registry.Registry;
import org.codehaus.plexus.registry.RegistryException;
import org.codehaus.plexus.registry.RegistryListener;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @version $Id$
 * @plexus.component role-hint="naming-registry"
 * @since 8 feb. 07
 */
public class NamingRegistry
    extends AbstractLogEnabled
    implements Registry
{

    //---------------------------------------------------------------
    // Plexus configuration
    //---------------------------------------------------------------
    /**
     * can be null if useDefaultNaming (new InitialContext)
     *
     * @plexus.requirement
     */
    private Naming naming;

    /**
     * @plexus.configuration default-value="true"
     */
    private boolean useDefaultNaming = true;

    private Context baseContext;

    private List registryListeners;

    public NamingRegistry()
    {
        // nothing
    }

    /**
     * this is use in order to return SubRegistry (reason of private)
     *
     * @param baseContext
     */
    private NamingRegistry( Context baseContext )
    {
        this.baseContext = baseContext;
    }

    //---------------------------------------------------------------
    // Plexus lifecycle
    //---------------------------------------------------------------    

    //---------------------------------------------------------------
    // Internal stuffs
    //---------------------------------------------------------------     

    private Context getBaseContext()
        throws NamingException
    {
        if ( this.baseContext != null )
        {
            return this.baseContext;
        }
        if ( useDefaultNaming )
        {
            InitialContext initialContext = new InitialContext();
            return (Context) initialContext.lookup( "java:comp/env" );
        }
        Context ctx = naming.createInitialContext();
        return (Context) ctx.lookup( "java:comp/env" );
    }

    private Object getObject( String key )
        throws NamingException, NameNotFoundException
    {
        return getBaseContext().lookup( key );
    }

    //---------------------------------------------------------------
    // Registry implementation
    //---------------------------------------------------------------     

    /**
     * @see org.codehaus.plexus.registry.Registry#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String key )
    {
        try
        {
            return ( (Boolean) getObject( key ) ).booleanValue();
        }
        catch ( NameNotFoundException e )
        {
            throw new NoSuchElementException( "key not found " );
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getBoolean(java.lang.String,boolean)
     */
    public boolean getBoolean( String key, boolean defaultValue )
    {
        try
        {
            return getBoolean( key );
        }
        catch ( NoSuchElementException e )
        {
            //ignore
        }
        return defaultValue;
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getInt(java.lang.String)
     */
    public int getInt( String key )
    {
        try
        {
            return ( (Integer) getObject( key ) ).intValue();
        }
        catch ( NameNotFoundException e )
        {
            throw new NoSuchElementException( "key not found " );
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getInt(java.lang.String,int)
     */
    public int getInt( String key, int defaultValue )
    {
        try
        {
            return getInt( key );
        }
        catch ( Exception e )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( e.getMessage(), e );
            }
        }
        return defaultValue;
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getString(java.lang.String)
     */
    public String getString( String key )
    {
        try
        {
            return (String) getObject( key );
        }
        catch ( NameNotFoundException e )
        {
            return null;
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getString(java.lang.String,java.lang.String)
     */
    public String getString( String key, String defaultValue )
    {
        try
        {
            return getString( key );
        }
        catch ( Exception e )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( e.getMessage(), e );
            }
        }
        return defaultValue;
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getList(java.lang.String)
     */
    public List getList( String key )
    {

        try
        {
            // with a naming only one value can be returned
            String value = getString( key );
            List list = new ArrayList( 1 );
            list.add( value );
            return list;
        }
        catch ( NoSuchElementException e )
        {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getSubRegistry(java.lang.String)
     */
    public Registry getSubset( String key )
    {
        try
        {
            Context context = (Context) this.getBaseContext().lookup( key );
            return new NamingRegistry( context );
        }
        catch ( NameNotFoundException e )
        {
            return new EmptyNamingRegistry();
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getKeys()
     */
    public Collection getKeys()
    {
        try
        {
            NamingEnumeration namingEnumeration = this.getBaseContext().list( "" );
            List keys = new ArrayList();
            while ( namingEnumeration.hasMoreElements() )
            {
                Object o = namingEnumeration.next();
                if ( o instanceof NameClassPair )
                {
                    NameClassPair nameClassPair = (NameClassPair) o;
                    keys.add( nameClassPair.getName() );
                }

            }
            return keys;
        }
        catch ( NamingException e )
        {
            getLogger().debug( e.getMessage(), e );
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getProperties(java.lang.String)
     */
    public Properties getProperties( String key )
    {
        try
        {
            NamingEnumeration namingEnumeration = this.getBaseContext().list( key );
            NamingRegistry subOne = (NamingRegistry) this.getSubset( key );
            Properties properties = new Properties();
            while ( namingEnumeration.hasMoreElements() )
            {
                Object o = namingEnumeration.next();
                if ( o instanceof NameClassPair )
                {
                    NameClassPair nameClassPair = (NameClassPair) o;
                    String className = nameClassPair.getClassName();
                    String name = nameClassPair.getName();
                    getLogger().debug( name );
                    if ( !Context.class.isAssignableFrom( Class.forName( className ) ) )
                    {
                        properties.put( name, subOne.getObject( name ) );
                    }
                }
            }
            return properties;
        }
        catch ( NameNotFoundException e )
        {
            return new Properties();
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * <b>do same as getSubset</b>
     *
     * @see org.codehaus.plexus.registry.Registry#getSection(java.lang.String)
     */
    public Registry getSection( String name )
    {
        return this.getSubset( name );
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#getSubsetList(java.lang.String)
     */
    public List getSubsetList( String key )
    {
        throw new UnsupportedOperationException( "getSubsetList not supported in NamingRegistry" );
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#isEmpty()
     */
    public boolean isEmpty()
    {
        try
        {
            NamingEnumeration namingEnumeration = this.getBaseContext().list( "" );
            return !namingEnumeration.hasMoreElements();
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.registry.Registry#dump()
     */
    public String dump()
    {
        try
        {
            String ls = System.getProperty( "line.separator" );
            StringBuffer dump = new StringBuffer();
            NamingEnumeration namingEnumeration = this.getBaseContext().list( "" );
            while ( namingEnumeration.hasMoreElements() )
            {
                Object o = namingEnumeration.next();
                if ( o instanceof NameClassPair )
                {
                    NameClassPair nameClassPair = (NameClassPair) o;
                    if ( nameClassPair.getClassName().equals( NamingContext.class.getName() ) )
                    {
                        NamingRegistry sub = new NamingRegistry( (Context) this.getObject( nameClassPair.getName() ) );
                        dump.append( sub.dump() );
                    }
                    else
                    {
                        dump.append( "key " + nameClassPair.getName() + ", type " + nameClassPair.getClassName() );
                        dump.append( ", value " + this.getObject( nameClassPair.getName() ) ).append( ls );
                    }
                }

            }
            return dump.toString();
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public void addChangeListener( RegistryListener registryListener )
    {
        if ( this.registryListeners == null )
        {
            this.registryListeners = new ArrayList( 1 );
        }
        this.registryListeners.add( registryListener );
    }

    // loading methods 

    /**
     * @see org.codehaus.plexus.registry.Registry#addConfigurationFromFile(java.io.File)
     */
    public void addConfigurationFromFile( File file )
        throws RegistryException
    {
        // TODO 

    }

    /**
     * @see org.codehaus.plexus.registry.Registry#addConfigurationFromResource(java.lang.String)
     */
    public void addConfigurationFromResource( String resource )
        throws RegistryException
    {
        // TODO 

    }

    public void addConfigurationFromFile( File file, String prefix )
        throws RegistryException
    {
        // TODO Auto-generated method stub

    }

    public void addConfigurationFromResource( String resource, String prefix )
        throws RegistryException
    {
        // TODO Auto-generated method stub

    }

    // write methods

    public void save()
        throws RegistryException, UnsupportedOperationException
    {
        // TODO Auto-generated method stub

    }

    public void setBoolean( String key, boolean value )
    {
        try
        {
            this.setValue( Boolean.toString( value ), Boolean.class.getName(), key );
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public void setInt( String key, int value )
    {
        try
        {
            this.setValue( Integer.toString( value ), Integer.class.getName(), key );
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }

    }

    public void setString( String key, String value )
    {
        try
        {
            this.setValue( value, String.class.getName(), key );
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }

    }

    private void setValue( String value, String javaClassName, String key )
        throws NamingException
    {
        Context envContext = this.getBaseContext();
        if ( envContext instanceof org.apache.naming.NamingContext )
        {
            Config.Environment config = new Config.Environment();
            config.setName( key );
            config.setType( javaClassName );
            config.setValue( value );
            try
            {
                CompositeName name = new CompositeName( key );
                for ( int i = 1; i <= name.size() - 1; i++ )
                {
                    try
                    {
                        envContext.createSubcontext( name.getPrefix( i ) );
                    }
                    catch ( NameAlreadyBoundException e )
                    {
                        // The prefix is already added as a sub context
                    }
                }
                envContext.bind( key, config.createValue() );
            }
            catch ( NameAlreadyBoundException e )
            {
                // skip already bind
            }
        }
        else
        {
            throw new UnsupportedOperationException( "not supported if non org.apache.naming.NamingContext" );
        }
    }

    public void remove( String key )
    {
        // TODO
        throw new UnsupportedOperationException( "not yet implemented - NamingRegistry.remove()" );
    }

    public void removeSubset( String key )
    {
        // TODO
        throw new UnsupportedOperationException( "not yet implemented - NamingRegistry.removeSubset()" );
    }
}
