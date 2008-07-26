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

import org.codehaus.plexus.registry.Registry;
import org.codehaus.plexus.registry.RegistryException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * @author <a href="mailto:Olivier.LAMY@accor.com">olamy</a>
 * @version $Id$
 * @since 9 feb. 07
 */
public class EmptyNamingRegistry
    extends NamingRegistry
{

    public String dump()
    {
        return "empty";
    }

    public boolean getBoolean( String key, boolean defaultValue )
    {
        return defaultValue;
    }

    /**
     * @see org.codehaus.plexus.registry.naming.NamingRegistry#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String key )
    {
        throw new NoSuchElementException();
    }

    public int getInt( String key, int defaultValue )
    {
        return defaultValue;
    }

    public int getInt( String key )
    {
        throw new NoSuchElementException();
    }

    public List getList( String key )
    {
        return Collections.EMPTY_LIST;
    }

    public Properties getProperties( String key )
    {
        return new Properties();
    }

    public Registry getSection( String name )
    {
        return new EmptyNamingRegistry();
    }

    public String getString( String key, String defaultValue )
    {
        return defaultValue;
    }

    /**
     * @see org.codehaus.plexus.registry.naming.NamingRegistry#getString(java.lang.String)
     */
    public String getString( String key )
    {
        return null;
    }

    /**
     * @see org.codehaus.plexus.registry.naming.NamingRegistry#getSubset(java.lang.String)
     */
    public Registry getSubset( String key )
    {
        return new EmptyNamingRegistry();
    }

    /**
     * @see org.codehaus.plexus.registry.naming.NamingRegistry#getSubsetList(java.lang.String)
     */
    public List getSubsetList( String key )
    {
        return Collections.EMPTY_LIST;
    }

    public boolean isEmpty()
    {
        return true;
    }

    public void save()
        throws RegistryException, UnsupportedOperationException
    {
        super.save();
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
