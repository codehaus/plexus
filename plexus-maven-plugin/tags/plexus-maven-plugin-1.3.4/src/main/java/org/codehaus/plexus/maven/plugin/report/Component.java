package org.codehaus.plexus.maven.plugin.report;

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

import org.jdom.Element;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Component
    implements Comparable
{
    private String description;

    private String role;

    private String roleHint;

    private String alias;

    private String version;

    private String implementation;

    private String instantiationStrategy;

    private Requirements requirements;

    private Configuration configuration;

    public Component( Element component )
    {
        description = component.getChildText( "description" );
        role = component.getChildText( "role" );
        roleHint = component.getChildText( "role-hint" );
        alias = component.getChildText( "alias" );
        version = component.getChildText( "version" );
        implementation = component.getChildText( "implementation" );
        instantiationStrategy = component.getChildTextTrim( "instantiation-strategy" );

        List list = component.getChildren( "requirements" );
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            Element element = (Element) it.next();

            requirements = new Requirements( element );
        }

        list = component.getChildren( "configuration" );
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            Element element = (Element) it.next();
            configuration = new Configuration( element );
        }
    }

    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public boolean hasRoleHint()
    {
        return StringUtils.isNotEmpty( roleHint );
    }

    public void print( Sink sink, String javaDocDestDir, String jxrDestDir )
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String title;
        if ( !hasRoleHint() )
        {
            title = role;
        }
        else
        {
            title = roleHint;
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        sink.section3();
        sink.sectionTitle3();
        sink.text( "Component: " + title );
        sink.sectionTitle3_();
        sink.section3_();

        sink.table();

        writeField( sink, "Role", role );

        writeField( sink, "Role hint", roleHint );

        sink.tableRow();
        sink.tableCell();
        sink.text( "Implementation" );
        sink.tableCell_();
        sink.tableCell();
        sink.monospaced();
        sink.text( implementation );
        sink.monospaced_();
        sink.text( " " );
        sink.link( "../" + javaDocDestDir + "/" + implementation.replace( '.', '/' ) + ".html" );
        sink.text( "javadoc" );
        sink.link_();
        sink.link( "../" + jxrDestDir + "/" + implementation.replace( '.', '/' ) + ".html" );
        sink.text( " " );
        sink.text( "xref" );
        sink.link_();
        sink.tableCell_();
        sink.tableRow_();

        writeField( sink, "Description", description );

        writeField( sink, "Alias", alias );

        writeField( sink, "Version", version );

        writeField( sink, "Instantiation strategy", instantiationStrategy );

        sink.table_();

        if ( requirements != null )
        {
            requirements.print( sink );
        }

        if ( configuration != null )
        {
            configuration.print( sink );
        }
    }

    private void writeField( Sink sink, String name, String value )
    {
        if ( StringUtils.isNotEmpty( value ) )
        {
            sink.tableRow();
            sink.tableCell();
            sink.text( name );
            sink.tableCell_();
            sink.tableCell();
            sink.monospaced();
            sink.text( value );
            sink.monospaced_();
            sink.tableCell_();
            sink.tableRow_();
        }
    }

    public int compareTo( Object o )
    {
        return ( (Component) o ).getId().compareTo( getId() );
    }

    private String getId()
    {
        if ( !hasRoleHint() )
        {
            return role;
        }
        else
        {
            return role + ":" + roleHint;
        }
    }
}
