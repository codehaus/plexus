/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.codehaus.plexus.maven.plugin.report;

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

    public void print( Sink sink )
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
        sink.link( "../javadoc/" + implementation.replace( '.', '/' ) + ".html" );
        sink.text( "javadoc" );
        sink.link_();
        sink.link( "../xref/" + implementation.replace( '.', '/' ) + ".html" );
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
        return ((Component) o).getId().compareTo( getId() );
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
