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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.naming.Naming;
import org.codehaus.plexus.registry.Registry;
import org.codehaus.plexus.registry.RegistryException;
import org.codehaus.plexus.registry.RegistryListener;

import javax.naming.Context;
import javax.naming.InitialContext;
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
 * @author <a href="mailto:Olivier.LAMY@accor.com">olamy</a>
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
            //with a naming only one value can be returned
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

    /**
     * @see org.codehaus.plexus.registry.Registry#dump()
     */
    public String dump()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void addChangeListener( RegistryListener listener )
    {
        // TODO Auto-generated method stub

    }

    public Collection getKeys()
    {
        // TODO Auto-generated method stub
        return null;
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

    public Properties getProperties( String key )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Registry getSection( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getSubsetList( String key )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void save()
        throws RegistryException, UnsupportedOperationException
    {
        // TODO Auto-generated method stub

    }

    public void setBoolean( String key, boolean value )
    {
        // TODO Auto-generated method stub

    }

    public void setInt( String key, int value )
    {
        // TODO Auto-generated method stub

    }

    public void setString( String key, String value )
    {
        // TODO Auto-generated method stub

    }

}
