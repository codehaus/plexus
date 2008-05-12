/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.plexus.spring;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * A plexus component implements Configurable
 *
 * @author <a href="mailto:olamy@apache.org">Olivier Lamy</a>
 */
public class Xpp3DomPlexusConfiguration
    implements PlexusConfiguration
{
    
    private Xpp3Dom root;

    public Xpp3DomPlexusConfiguration( Xpp3Dom xpp3Dom )
    {
        this.root = xpp3Dom;
    }

    public void addChild( PlexusConfiguration arg0 )
    {
        throw new UnsupportedOperationException( "immutable" );
    }

    public String getAttribute( String paramName )
        throws PlexusConfigurationException
    {
        throw new UnsupportedOperationException( "not done yet" );
    }

    public String getAttribute( String name, String defaultValue )
    {
        throw new UnsupportedOperationException( "not done yet" );
    }

    public String[] getAttributeNames()
    {
        throw new UnsupportedOperationException( "not done yet" );
    }

    public PlexusConfiguration getChild( String name )
    {
        if ( this.root == null )
        {
            return null;
        }
        Xpp3Dom child = this.root.getChild( name );
        return child == null ? null : new Xpp3DomPlexusConfiguration( child );
    }

    public PlexusConfiguration getChild( int i )
    {
        if ( this.root == null )
        {
            return null;
        }
        Xpp3Dom child = this.root.getChild( i );
        return child == null ? null : new Xpp3DomPlexusConfiguration( child );
    }

    public PlexusConfiguration getChild( String arg0, boolean arg1 )
    {
        throw new UnsupportedOperationException( "immutable" );
    }

    public int getChildCount()
    {
        if ( this.root == null )
        {
            return 0;
        }
        return this.root.getChildCount();
    }

    public PlexusConfiguration[] getChildren()
    {
        if ( this.root == null )
        {
            return null;
        }
        PlexusConfiguration[] childrens = new PlexusConfiguration[this.root.getChildCount()];
        for ( int i = 0; i < this.root.getChildCount(); i++ )
        {
            childrens[i] = new Xpp3DomPlexusConfiguration( this.root.getChild( i ) );
        }
        return childrens;
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        if ( this.root == null )
        {
            return null;
        }
        PlexusConfiguration[] childrens = new PlexusConfiguration[this.root.getChildCount()];
        for ( int i = 0; i < this.root.getChildCount(); i++ )
        {
            if ( StringUtils.equals( this.root.getChild( i ).getName(), name ) )
                childrens[i] = new Xpp3DomPlexusConfiguration( this.root.getChild( i ) );
        }
        return childrens;
    }

    public String getName()
    {
        if ( this.root == null )
        {
            return null;
        }
        return this.root.getName();
    }

    public String getValue()
        throws PlexusConfigurationException
    {
        if ( this.root == null )
        {
            return null;
        }
        return this.root.getValue();
    }

    public String getValue( String arg0 )
    {
        throw new UnsupportedOperationException( "immutable" );
    }

}
